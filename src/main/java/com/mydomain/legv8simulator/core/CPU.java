package main.java.com.mydomain.legv8simulator.core;

import  main.java.com.mydomain.legv8simulator.core.Memory; // Giả định bạn có lớp Memory
import  main.java.com.mydomain.legv8simulator.instruction.*; // Import tất cả các loại lệnh

public class CPU {
    // Kích thước thanh ghi LEGv8 là 64-bit, nên dùng long
    private final long[] registers; // X0-X30, X31 là XZR (Zero Register)
    private long programCounter;   // PC: Địa chỉ của lệnh hiện tại
    private final Memory memory;     // Bộ nhớ chính
    private final InstructionDecoder decoder; // Bộ giải mã lệnh

    // Các hằng số cho thanh ghi
    public static final int NUM_REGISTERS = 32; // X0 - X30, XZR (X31)
    public static final int XZR = 31; // Index của Zero Register

    public CPU(Memory memory) {
        this.memory = memory;
        this.registers = new long[NUM_REGISTERS];
        // Khởi tạo các thanh ghi về 0
        for (int i = 0; i < NUM_REGISTERS; i++) {
            registers[i] = 0;
        }
        // XZR (X31) luôn là 0, ghi vào nó sẽ không có tác dụng
        // Trong quá trình thực thi, ta sẽ đảm bảo nó luôn là 0

        this.programCounter = 0; // Bắt đầu từ địa chỉ 0
        this.decoder = new InstructionDecoder();
    }

    /**
     * Nạp một chương trình vào bộ nhớ và đặt PC về điểm bắt đầu.
     * @param startAddress Địa chỉ bắt đầu nạp chương trình.
     * @param machineCode Program bytes to load into memory.
     */
    public void loadProgram(long startAddress, int[] machineCode) {
        // Ví dụ: Giả định machineCode là một mảng các int (32-bit instruction)
        // và mỗi lệnh chiếm 4 byte
        for (int i = 0; i < machineCode.length; i++) {
            memory.storeWord(startAddress + (i * 4), machineCode[i]);
        }
        this.programCounter = startAddress; // Đặt PC về địa chỉ bắt đầu chương trình
        System.out.println("Program loaded at address: 0x" + String.format("%X", startAddress));
    }

    /**
     * Chạy trình giả lập.
     * @param maxInstructions Tối đa số lệnh để chạy (để tránh vòng lặp vô hạn)
     */
    public void run(int maxInstructions) {
        int instructionCount = 0;
        System.out.println("Starting CPU simulation...");
        while (instructionCount < maxInstructions) {
            // Đảm bảo XZR luôn là 0 (X31)
            registers[XZR] = 0;

            // Bước 1: Fetch (Đọc lệnh từ bộ nhớ)
            int machineCode;
            try {
                // Đọc một từ (4 byte) từ bộ nhớ
                machineCode = memory.loadWord(programCounter);
                System.out.println("\nPC: 0x" + String.format("%X", programCounter) +
                                   ", Machine Code: 0x" + String.format("%08X", machineCode));
            } catch (IndexOutOfBoundsException e) {
                System.err.println("Error: Attempted to fetch instruction from invalid memory address 0x" + String.format("%X", programCounter));
                break; // Dừng nếu truy cập bộ nhớ không hợp lệ
            }

            // Bước 2: Decode (Giải mã lệnh)
            Instruction instruction;
            try {
                instruction = decoder.decode(machineCode);
                System.out.println("Decoded: " + instruction.toString());
            } catch (IllegalArgumentException | UnsupportedOperationException e) {
                System.err.println("Error decoding instruction: " + e.getMessage());
                break; // Dừng nếu giải mã lỗi
            }

            // Bước 3: Execute (Thực thi lệnh)
            long nextPC = programCounter + 4; // Mặc định PC tăng 4 (next instruction)
            try {
                nextPC = execute(instruction, nextPC);
            } catch (Exception e) {
                System.err.println("Error executing instruction: " + e.getMessage());
                break; // Dừng nếu thực thi lỗi
            }

            programCounter = nextPC;
            instructionCount++;

            // Kiểm tra điều kiện dừng (ví dụ: PC vượt quá giới hạn bộ nhớ hợp lệ)
            if (programCounter < 0 || programCounter >= memory.getSize()) { // Giả định memory.getSize() trả về kích thước tối đa của bộ nhớ
                System.out.println("End of program or invalid PC address reached.");
                break;
            }
        }
        System.out.println("\nSimulation finished after " + instructionCount + " instructions.");
        printRegisters();
    }

    /**
     * Thực thi một lệnh.
     * Đây sẽ là phần phức tạp nhất, cần xử lý từng loại lệnh.
     * @param instruction Đối tượng lệnh đã được giải mã.
     * @param defaultNextPC PC mặc định nếu lệnh không phải là nhánh.
     * @return Địa chỉ PC mới.
     */
    private long execute(Instruction instruction, long defaultNextPC) {
        long currentPC = programCounter; // Lưu lại PC hiện tại trước khi thực thi

        switch (instruction.getFormat()) {
            case R_FORMAT:
                return executeRFormat((RFormatInstruction) instruction, defaultNextPC);
            case I_FORMAT:
                return executeIFormat((IFormatInstruction) instruction, defaultNextPC);
            case D_FORMAT:
                return executeDFormat((DFormatInstruction) instruction, defaultNextPC);
            case B_FORMAT:
                return executeBFormat((BFormatInstruction) instruction, currentPC); // B-format branches relative to current PC
            case CB_FORMAT:
                return executeCBFormat((CBFormatInstruction) instruction, currentPC); // CB-format branches relative to current PC
            // Thêm các định dạng khác khi bạn implement chúng
            default:
                throw new UnsupportedOperationException("Execution of " + instruction.getFormat() + " not yet implemented.");
        }
    }

    // --- Các phương thức thực thi lệnh theo định dạng ---

    private long executeRFormat(RFormatInstruction instr, long defaultNextPC) {
        int rd = instr.getRd();
        int rn = instr.getRn();
        int rm = instr.getRm();
        int shamt = instr.getShamt();

        long valueRn = registers[rn];
        long valueRm = registers[rm];

        long result = 0;

        switch (instr.getOpcodeMnemonic()) {
            case "ADD":
                result = valueRn + valueRm;
                break;
            case "SUB":
                result = valueRn - valueRm;
                break;
            case "AND":
                result = valueRn & valueRm;
                break;
            case "ORR":
                result = valueRn | valueRm;
                break;
            case "LSL": // Logical Shift Left
                result = valueRn << shamt;
                break;
            case "LSR": // Logical Shift Right
                result = valueRn >>> shamt; // Unsigned right shift
                break;
            case "ASR": // Arithmetic Shift Right
                result = valueRn >> shamt;  // Signed right shift
                break;
            case "BR": // Branch to Register
                // Đối với BR, PC sẽ được cập nhật bằng giá trị trong thanh ghi Rn
                return registers[rn]; // Cập nhật PC trực tiếp
            default:
                throw new UnsupportedOperationException("R-format instruction not implemented: " + instr.getOpcodeMnemonic());
        }

        // Ghi kết quả vào thanh ghi đích, trừ khi đó là XZR (X31)
        if (rd != XZR) {
            registers[rd] = result;
        } else {
            registers[XZR] = 0; // Đảm bảo XZR luôn là 0
        }

        return defaultNextPC; // PC tăng 4 mặc định
    }

    private long executeIFormat(IFormatInstruction instr, long defaultNextPC) {
        int rd = instr.getRd();
        int rn = instr.getRn();
        long immediate = instr.getImmediate(); // Immediate đã được sign-extended

        long valueRn = registers[rn];
        long result = 0;

        switch (instr.getOpcodeMnemonic()) {
            case "ADDI":
                result = valueRn + immediate;
                break;
            case "SUBI":
                result = valueRn - immediate;
                break;
            // Thêm các lệnh I-format khác (ANDI, ORRI, EORI)
            default:
                throw new UnsupportedOperationException("I-format instruction not implemented: " + instr.getOpcodeMnemonic());
        }

        if (rd != XZR) {
            registers[rd] = result;
        } else {
            registers[XZR] = 0;
        }

        return defaultNextPC;
    }

    private long executeDFormat(DFormatInstruction instr, long defaultNextPC) {
        int rt = instr.getRt();
        int rn = instr.getRn();
        long dtAddress = instr.getDtAddress(); // Offset đã được sign-extended

        long baseAddress = registers[rn];
        long effectiveAddress = baseAddress + dtAddress;

        switch (instr.getOpcodeMnemonic()) {
            case "LDUR": // Load Register Unsigned Register
                // Đọc một từ (long) từ bộ nhớ
                if (rt != XZR) {
                    registers[rt] = memory.loadWord(effectiveAddress); // Giả định loadWord trả về long
                } else {
                    registers[XZR] = 0;
                }
                break;
            case "STUR": // Store Register Unsigned Register
                // Ghi một từ (long) vào bộ nhớ
                memory.storeWord(effectiveAddress, registers[rt]); // Giả định storeWord nhận long
                break;
            // Thêm các lệnh D-format khác nếu cần (LDURB, STURB, LDURH, STURH, v.v.)
            default:
                throw new UnsupportedOperationException("D-format instruction not implemented: " + instr.getOpcodeMnemonic());
        }

        return defaultNextPC;
    }

    private long executeBFormat(BFormatInstruction instr, long currentPC) {
        // B-format: Nhảy tuyệt đối hoặc tương đối
        // Trong LEGv8, địa chỉ nhánh thường là tương đối với PC hiện tại.
        // Địa chỉ trong lệnh là "word-aligned", nghĩa là giá trị thực tế = brAddress * 4
        long branchOffset = instr.getBrAddress() * 4; // Đã được sign-extended

        // Địa chỉ nhánh = PC của lệnh tiếp theo + offset (PC + 4 + offset)
        // Hoặc PC của lệnh hiện tại + offset (PC + offset)
        // Trong LEGv8, địa chỉ nhánh (branch target address) = (PC của lệnh hiện tại) + offset
        // offset là giá trị signed extended và nhân 4.
        return currentPC + branchOffset; // Cập nhật PC
    }

    private long executeCBFormat(CBFormatInstruction instr, long currentPC) {
        int rtOrCond = instr.getRtOrCond();
        long condBrAddress = instr.getCondBrAddress() * 4; // Offset đã được sign-extended và nhân 4

        boolean branchTaken = false;

        switch (instr.getOpcodeMnemonic()) {
            case "CBZ": // Compare and Branch on Zero
                if (registers[rtOrCond] == 0) {
                    branchTaken = true;
                }
                break;
            case "CBNZ": // Compare and Branch on Non-Zero
                if (registers[rtOrCond] != 0) {
                    branchTaken = true;
                }
                break;
            // Xử lý các lệnh B.cond (B.EQ, B.NE, ...)
            // Điều này đòi hỏi bạn phải có một cách để theo dõi các cờ trạng thái (flags: N, Z, C, V)
            // hoặc tính toán điều kiện dựa trên các thanh ghi và các phép toán trước đó.
            // Hiện tại, chúng ta giả định bạn sẽ thêm logic cho các cờ này sau.
            case "B.EQ": // Branch if Equal (Z flag set)
                // Cần cơ chế để kiểm tra Z flag
                // Ví dụ: if (flags.isZ()) branchTaken = true;
                throw new UnsupportedOperationException("B.EQ requires flag implementation.");
            case "B.NE": // Branch if Not Equal (Z flag clear)
                // Cần cơ chế để kiểm tra Z flag
                // Ví dụ: if (!flags.isZ()) branchTaken = true;
                throw new UnsupportedOperationException("B.NE requires flag implementation.");
            // ... thêm các điều kiện nhánh khác
            default:
                throw new UnsupportedOperationException("CB-format instruction not implemented: " + instr.getOpcodeMnemonic());
        }

        if (branchTaken) {
            return currentPC + condBrAddress; // Nhảy
        } else {
            return currentPC + 4; // Tiếp tục lệnh kế tiếp
        }
    }

    /**
     * In ra trạng thái của các thanh ghi.
     */
    public void printRegisters() {
        System.out.println("\n--- Registers State ---");
        for (int i = 0; i < NUM_REGISTERS; i++) {
            System.out.printf("X%d: 0x%016X (%d)\n", i, registers[i], registers[i]);
        }
        System.out.printf("PC: 0x%016X\n", programCounter);
        System.out.println("-----------------------");
    }

    // Getter cho các thành phần khác (tùy chọn)
    public long getProgramCounter() {
        return programCounter;
    }

    public long getRegister(int index) {
        if (index < 0 || index >= NUM_REGISTERS) {
            throw new IllegalArgumentException("Invalid register index: " + index);
        }
        return registers[index];
    }
}