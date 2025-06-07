package main.java.com.mydomain.legv8simulator.core;

import  main.java.com.mydomain.legv8simulator.core.Memory; // Giả định bạn có lớp Memory
import  main.java.com.mydomain.legv8simulator.instruction.*; // Import tất cả các loại lệnh
import  main.java.com.mydomain.legv8simulator.utils.BitUtils; // Giả định bạn có lớp BitUtils để mở rộng dấu và zero

public class CPU {
    private final long[] registers;
    private long programCounter;
    private final Memory memory;
    private final InstructionDecoder decoder;
    private final FlagsRegister flags; // Thêm thanh ghi cờ

    public static final int NUM_REGISTERS = 32;
    public static final int XZR = 31;
    public static final int LR = 30; // Link Register (X30)

    // Giả định một giá trị đặc biệt cho HALT mà decoder có thể nhận diện
    // Hoặc bạn có thể dùng một lệnh đặc biệt, ví dụ mnemonic "HALT"
    public static final int HALT_MACHINE_CODE = 0xFFFFFFFF;


    // Lớp nội để quản lý cờ trạng thái
    private static class FlagsRegister {
        boolean N, Z, C, V; // Negative, Zero, Carry, Overflow

        public void reset() {
            N = false;
            Z = false;
            C = false;
            V = false;
        }

        // Các phương thức cập nhật cờ sẽ được gọi bởi ALU hoặc các lệnh S
        public void updateNZ(long result) {
            N = (result < 0); // Bit cao nhất của kết quả 64-bit
            Z = (result == 0);
        }

        // Logic cập nhật C và V phức tạp hơn và phụ thuộc vào phép toán
        // Đây là ví dụ đơn giản hóa, bạn cần tham khảo đặc tả ARMv8
        public void updateCV_Add(long operand1, long operand2, long result) {
            // Carry for addition (unsigned overflow)
            // If (op1 + op2) < op1 (or op2), then carry occurred
            C = (Long.compareUnsigned(result, operand1) < 0);

            // Overflow for addition (signed overflow)
            // If signs of operands are the same and sign of result is different
            if ((operand1 > 0 && operand2 > 0 && result < 0) ||
                (operand1 < 0 && operand2 < 0 && result >= 0)) { // Chú ý >=0 cho số âm
                V = true;
            } else {
                V = false;
            }
        }

        public void updateCV_Sub(long operand1, long operand2, long result) {
            // Carry for subtraction (no borrow)
            // If op1 >= op2 (unsigned), then C=1 (no borrow)
            C = (Long.compareUnsigned(operand1, operand2) >= 0);

            // Overflow for subtraction (signed overflow)
            // If signs of operands are different and sign of result is same as operand2
             if (((operand1 > 0 && operand2 < 0) && result < 0) ||
                 ((operand1 < 0 && operand2 > 0) && result >= 0)) {
                V = true;
            } else {
                V = false;
            }
        }

        @Override
        public String toString() {
            return String.format("Flags: N=%d Z=%d C=%d V=%d", N?1:0, Z?1:0, C?1:0, V?1:0);
        }
    }


    public CPU(Memory memory) {
        this.memory = memory;
        this.registers = new long[NUM_REGISTERS];
        for (int i = 0; i < NUM_REGISTERS; i++) {
            registers[i] = 0;
        }
        this.programCounter = 0;
        this.decoder = new InstructionDecoder(); // Giả định InstructionDecoder đã được cập nhật
        this.flags = new FlagsRegister();
    }

    public void loadProgram(long startAddress, int[] machineCode) {
        for (int i = 0; i < machineCode.length; i++) {
            memory.storeWord(startAddress + (i * 4L), machineCode[i]); // Sử dụng long cho địa chỉ
        }
        this.programCounter = startAddress;
        System.out.println("Program loaded at address: 0x" + String.format("%X", startAddress));
    }

    public void run(int maxInstructions) {
        int instructionCount = 0;
        System.out.println("Starting CPU simulation...");
        flags.reset(); // Reset cờ khi bắt đầu

        while (instructionCount < maxInstructions) {
            registers[XZR] = 0; // Đảm bảo XZR luôn là 0

            if (programCounter < 0 || programCounter >= memory.getSize()) {
                System.out.println("PC out of bounds. Halting.");
                break;
            }

            int machineCode;
            try {
                machineCode = memory.loadWord(programCounter);
                System.out.println("\nPC: 0x" + String.format("%04X", programCounter) + // Định dạng PC ngắn hơn
                                   ", Machine Code: 0x" + String.format("%08X", machineCode));
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Error: Attempted to fetch instruction from invalid memory address 0x" + String.format("%X", programCounter));
                break;
            }

            if (machineCode == HALT_MACHINE_CODE) {
                System.out.println("HALT instruction encountered. Simulation stopped.");
                break;
            }

            Instruction instruction;
            try {
                instruction = decoder.decode(machineCode);
                // Nếu bạn có log chi tiết từ decoder (như "Decoded R-format: ..."), nó sẽ ở đây
                System.out.println("Decoded: " + instruction.toString());
            } catch (IllegalArgumentException | UnsupportedOperationException e) {
                System.err.println("Error decoding instruction 0x" + Integer.toHexString(machineCode) + ": " + e.getMessage());
                programCounter += 4; // Thử bỏ qua lệnh lỗi và tiếp tục
                instructionCount++;
                continue;
            }
             if (instruction.getFormat() == InstructionFormat.UNKNOWN) {
                System.err.println("Unknown instruction encountered: " + instruction.toString());
                programCounter += 4; // Bỏ qua lệnh không rõ
                instructionCount++;
                continue;
            }


            long nextPC = programCounter + 4;
            try {
                nextPC = execute(instruction, nextPC);
            } catch (Exception e) {
                System.err.println("Error executing instruction " + instruction.getOpcodeMnemonic() + " at PC 0x" + String.format("%X", programCounter) + ": " + e.getMessage());
                e.printStackTrace(); // In stack trace để debug
                break;
            }

            programCounter = nextPC;
            instructionCount++;
            System.out.println(flags.toString()); // In trạng thái cờ sau mỗi lệnh
        }
        System.out.println("\nSimulation finished after " + instructionCount + " instructions.");
        printRegisters();
    }

    private long execute(Instruction instruction, long defaultNextPC) {
        long currentPC = programCounter; // PC của lệnh hiện tại

        switch (instruction.getFormat()) {
            case R_FORMAT:
                return executeRFormat((RFormatInstruction) instruction, defaultNextPC);
            case I_FORMAT:
                return executeIFormat((IFormatInstruction) instruction, defaultNextPC);
            case D_FORMAT:
                return executeDFormat((DFormatInstruction) instruction, defaultNextPC);
            case B_FORMAT:
                return executeBFormat((BFormatInstruction) instruction, currentPC);
            case CB_FORMAT:
                return executeCBFormat((CBFormatInstruction) instruction, currentPC);
            case IM_FORMAT:
                return executeIMFormat((IMFormatInstruction) instruction, defaultNextPC);
            default:
                System.err.println("Execution of " + instruction.getFormat() + " (Mnemonic: " + instruction.getOpcodeMnemonic() + ") not yet implemented.");
                return defaultNextPC; // Hoặc ném lỗi
        }
    }

    private long executeRFormat(RFormatInstruction instr, long defaultNextPC) {
        int rd = instr.getRd();
        int rn = instr.getRn();
        int rm = instr.getRm();
        int shamt = instr.getShamt();

        long valRn = (rn == XZR) ? 0 : registers[rn];
        long valRm = (rm == XZR) ? 0 : registers[rm];
        long result = 0;

        switch (instr.getOpcodeMnemonic()) {
            case "ADD":
                result = valRn + valRm;
                break;
            case "ADDS":
                result = valRn + valRm;
                flags.updateNZ(result);
                flags.updateCV_Add(valRn, valRm, result);
                break;
            case "SUB":
                result = valRn - valRm;
                break;
            case "SUBS": // Dùng cho CMP Rd, Rn bằng cách SUB XZR, Rd, Rn
                result = valRn - valRm;
                flags.updateNZ(result);
                flags.updateCV_Sub(valRn, valRm, result);
                break;
            case "MUL":
                result = valRn * valRm; // Chỉ lấy 64 bit thấp
                break;
            case "AND":
                result = valRn & valRm;
                break;
            case "ANDS":
                result = valRn & valRm;
                flags.updateNZ(result);
                // ANDS không cập nhật C, V theo chuẩn ARM
                flags.C = false; // Hoặc giữ nguyên giá trị C trước đó
                flags.V = false; // Hoặc giữ nguyên giá trị V trước đó
                break;
            case "ORR":
                result = valRn | valRm;
                break;
            case "EOR":
                result = valRn ^ valRm;
                break;
            case "LSL":
                result = valRn << shamt;
                break;
            case "LSR":
                result = valRn >>> shamt;
                break;
            case "ASR":
                result = valRn >> shamt;
                break;
            case "BR":
                return valRn; // PC mới là giá trị trong thanh ghi Rn
            default:
                throw new UnsupportedOperationException("R-format mnemonic not implemented: " + instr.getOpcodeMnemonic());
        }

        if (rd != XZR) {
            registers[rd] = result;
        }
        return defaultNextPC;
    }

    private long executeIFormat(IFormatInstruction instr, long defaultNextPC) {
        int rd = instr.getRd();
        int rn = instr.getRn();
        long immediate = instr.getImmediate(); // Đã sign-extended bởi decoder

        long valRn = (rn == XZR) ? 0 : registers[rn];
        long result = 0;

        switch (instr.getOpcodeMnemonic()) {
            case "ADDI":
                result = valRn + immediate;
                break;
            case "ADDIS":
                result = valRn + immediate;
                flags.updateNZ(result);
                flags.updateCV_Add(valRn, immediate, result);
                break;
            case "SUBI":
                result = valRn - immediate;
                break;
            case "SUBIS":
                result = valRn - immediate;
                flags.updateNZ(result);
                flags.updateCV_Sub(valRn, immediate, result);
                break;
            case "ANDI": // ANDS immediate
                result = valRn & immediate;
                flags.updateNZ(result);
                flags.C = false; flags.V = false; // Theo chuẩn
                break;
            case "ORRI": // ORRS immediate
                result = valRn | immediate;
                flags.updateNZ(result); // ORRI thường không set cờ, nhưng nếu là ORRIS thì có
                                        // Giả sử đây là ORRI (không set cờ logic)
                break;
            case "EORI": // EORS immediate
                result = valRn ^ immediate;
                flags.updateNZ(result); // Tương tự ORRI
                break;
            default:
                throw new UnsupportedOperationException("I-format mnemonic not implemented: " + instr.getOpcodeMnemonic());
        }
        if (rd != XZR) {
            registers[rd] = result;
        }
        return defaultNextPC;
    }

    private long executeDFormat(DFormatInstruction instr, long defaultNextPC) {
        int rt = instr.getRt();
        int rn = instr.getRn();
        long dtAddress = instr.getDtAddress(); // Offset đã sign-extended

        long baseAddress = (rn == XZR) ? 0 : registers[rn];
        long effectiveAddress = baseAddress + dtAddress;

        switch (instr.getOpcodeMnemonic()) {
            case "LDUR": // Load Doubleword (64-bit)
                if (rt != XZR) registers[rt] = memory.loadDoubleWord(effectiveAddress);
                break;
            case "STUR": // Store Doubleword (64-bit)
                memory.storeDoubleWord(effectiveAddress, registers[rt]);
                break;
            case "LDURSW": // Load Word (32-bit) Sign-extended
                int wordValue = memory.loadWord(effectiveAddress);
                if (rt != XZR) registers[rt] = BitUtils.signExtend64(wordValue, 32); // Mở rộng dấu từ 32 lên 64 bit
                break;
            case "STURW": // Store Word (32-bit)
                memory.storeWord(effectiveAddress, (int) registers[rt]); // Lưu 32 bit thấp
                break;
            case "LDURH": // Load Halfword (16-bit) Zero-extended
                short halfwordValue = memory.loadHalfWord(effectiveAddress); // Cần hàm này trong Memory
                if (rt != XZR) registers[rt] = BitUtils.zeroExtend64(halfwordValue & 0xFFFF, 16); // &0xFFFF để đảm bảo lấy phần dương
                break;
            case "STURH": // Store Halfword (16-bit)
                memory.storeHalfWord(effectiveAddress, (short) registers[rt]); // Cần hàm này trong Memory
                break;
            case "LDURB": // Load Byte (8-bit) Zero-extended
                byte byteValue = memory.loadByte(effectiveAddress); // Cần hàm này trong Memory
                if (rt != XZR) registers[rt] = BitUtils.zeroExtend64(byteValue & 0xFF, 8);
                break;
            case "STURB": // Store Byte (8-bit)
                memory.storeByte(effectiveAddress, (byte) registers[rt]); // Cần hàm này trong Memory
                break;
            default:
                throw new UnsupportedOperationException("D-format mnemonic not implemented: " + instr.getOpcodeMnemonic());
        }
        return defaultNextPC;
    }

    private long executeBFormat(BFormatInstruction instr, long currentPC) {
        long branchOffset = instr.getBrAddress() * 4L; // Đã được sign-extended, nhân 4

        if ("BL".equals(instr.getOpcodeMnemonic())) {
            registers[LR] = currentPC + 4; // Lưu địa chỉ lệnh kế tiếp vào LR
        }
        return currentPC + branchOffset;
    }

    private long executeCBFormat(CBFormatInstruction instr, long currentPC) {
        int rtOrCond = instr.getRtOrCond(); // Thanh ghi cho CBZ/CBNZ, mã điều kiện cho B.cond
        long condBrAddressOffset = instr.getCondBrAddress() * 4L; // Đã sign-extended, nhân 4

        boolean branchTaken = false;
        long valRt = (rtOrCond < XZR && rtOrCond >=0) ? registers[rtOrCond] : 0; // Lấy giá trị thanh ghi nếu là CBZ/CBNZ

        switch (instr.getOpcodeMnemonic()) {
            case "CBZ":
                if (valRt == 0) branchTaken = true;
                break;
            case "CBNZ":
                if (valRt != 0) branchTaken = true;
                break;
            // --- B.cond ---
            case "B.EQ": branchTaken = flags.Z; break;
            case "B.NE": branchTaken = !flags.Z; break;
            case "B.HS": branchTaken = flags.C; break; // Unsigned >=
            case "B.LO": branchTaken = !flags.C; break; // Unsigned <
            case "B.MI": branchTaken = flags.N; break;
            case "B.PL": branchTaken = !flags.N; break;
            case "B.VS": branchTaken = flags.V; break;
            case "B.VC": branchTaken = !flags.V; break;
            case "B.HI": branchTaken = flags.C && !flags.Z; break; // Unsigned >
            case "B.LS": branchTaken = !flags.C || flags.Z; break; // Unsigned <=
            case "B.GE": branchTaken = (flags.N == flags.V); break; // Signed >=
            case "B.LT": branchTaken = (flags.N != flags.V); break; // Signed <
            case "B.GT": branchTaken = (!flags.Z && (flags.N == flags.V)); break; // Signed >
            case "B.LE": branchTaken = (flags.Z || (flags.N != flags.V)); break; // Signed <=
            default:
                throw new UnsupportedOperationException("CB-format/B.cond mnemonic not implemented: " + instr.getOpcodeMnemonic());
        }

        return branchTaken ? (currentPC + condBrAddressOffset) : (currentPC + 4);
    }

     private long executeIMFormat(IMFormatInstruction instr, long defaultNextPC) {
        int rd = instr.getRd();
        int immediate = instr.getImmediate(); // 16-bit unsigned immediate
        int shift = instr.getShiftAmount(); // 0, 16, 32, 48

        long valueToMove = (long)immediate << shift;

        switch (instr.getOpcodeMnemonic()) {
            case "MOVZ": // Move Wide with Zero
                if (rd != XZR) {
                    registers[rd] = valueToMove;
                }
                break;
            case "MOVK": // Move Wide with Keep
                if (rd != XZR) {
                    // Tạo mask để giữ các bit không bị ghi đè
                    long mask = ~(((1L << 16) - 1) << shift);
                    registers[rd] = (registers[rd] & mask) | valueToMove;
                }
                break;
            default:
                throw new UnsupportedOperationException("IM-format mnemonic not implemented: " + instr.getOpcodeMnemonic());
        }
        return defaultNextPC;
    }


    public void printRegisters() {
        System.out.println("\n--- Registers State ---");
        for (int i = 0; i < NUM_REGISTERS; i++) {
            System.out.printf("X%d: 0x%016X (%d)\n", i, registers[i], registers[i]);
        }
        System.out.printf("PC: 0x%04X\n", programCounter); // Định dạng PC ngắn hơn
        System.out.println(flags.toString());
        System.out.println("-----------------------");
    }

    // ... (getters nếu cần) ...
    public long getRegisterValue(int regIndex) {
        if (regIndex < 0 || regIndex >= NUM_REGISTERS) throw new IllegalArgumentException("Invalid register index");
        if (regIndex == XZR) return 0;
        return registers[regIndex];
    }

    public void setRegisterValue(int regIndex, long value) {
        if (regIndex < 0 || regIndex >= NUM_REGISTERS) throw new IllegalArgumentException("Invalid register index");
        if (regIndex != XZR) registers[regIndex] = value;
    }

     public FlagsRegister getFlags() {
        return flags;
    }
}