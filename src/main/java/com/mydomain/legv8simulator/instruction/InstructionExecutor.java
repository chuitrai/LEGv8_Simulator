package main.java.com.mydomain.legv8simulator.instruction;

import main.java.com.mydomain.legv8simulator.core.*;
import main.java.com.mydomain.legv8simulator.utils.BitUtils;
import main.java.com.mydomain.legv8simulator.utils.Constants;

/**
 * Chịu trách nhiệm thực thi các lệnh đã được giải mã.
 * Lớp này thay đổi trạng thái của CPU (thanh ghi, PC, cờ) và Memory.
 * Nó nhận vào các đối tượng đã được đóng gói và thực hiện logic nghiệp vụ.
 */
public class InstructionExecutor {

    private final CPU cpu;
    private final Memory memory;
    private final ALU alu; // Thêm ALU vào executor

    public InstructionExecutor(CPU cpu, Memory memory) {
        this.cpu = cpu;
        this.memory = memory;
        this.alu = new ALU(); // Khởi tạo ALU để thực hiện các phép toán
    }

    /**
     * Phương thức điều phối chính để thực thi một lệnh.
     * Nó tự động tăng PC và sau đó gọi phương thức thực thi chuyên biệt.
     * Các lệnh rẽ nhánh sẽ tự ghi đè PC nếu cần.
     * @param instruction Đối tượng lệnh đã được giải mã.
     */
    public void execute(Instruction instruction, long currentPC) {
        // PC sẽ được cập nhật trong các hàm con.
        // Mặc định, nó sẽ là currentPC + 4 nếu không phải lệnh rẽ nhánh.
        System.out.println("Executing instruction at PC 0x" + Long.toHexString(currentPC) + ": " + instruction.getOpcodeMnemonic());

        if (instruction instanceof RFormatInstruction) {
            executeRFormat((RFormatInstruction) instruction, currentPC);
        } else if (instruction instanceof IFormatInstruction) {
            executeIFormat((IFormatInstruction) instruction, currentPC);
        } else if (instruction instanceof DFormatInstruction) {
            executeDFormat((DFormatInstruction) instruction, currentPC);
        } else if (instruction instanceof BFormatInstruction) {
            executeBFormat((BFormatInstruction) instruction, currentPC);
        } else if (instruction instanceof CBFormatInstruction) {
            executeCBFormat((CBFormatInstruction) instruction, currentPC);
        } else if (instruction instanceof IMFormatInstruction) {
            executeIMFormat((IMFormatInstruction) instruction, currentPC);
        } else if (instruction instanceof UnknownInstruction) {
            handleUnknownInstruction((UnknownInstruction) instruction);
        }
    }

    // =========================================================================
    // I. Arithmetic & Logical Instructions (R-Format)
    // =========================================================================
     private void executeRFormat(RFormatInstruction i, long currentPC) {
        long rnVal = cpu.getRegisterFile().read(i.getRn());
        long rmVal = cpu.getRegisterFile().read(i.getRm());
        ALUResult aluResult;
        String mnemonic = i.getOpcodeMnemonic();

        // Mặc định PC tiếp theo là lệnh tuần tự
        long nextPC = currentPC + 4;

        switch (mnemonic) {
            case "ADD":
                aluResult = alu.add(rnVal, rmVal);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                break;
            case "ADDS":
                aluResult = alu.add(rnVal, rmVal);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                cpu.getFlagsRegister().updateFlags(aluResult);
                break;
            case "SUB":
                aluResult = alu.subtract(rnVal, rmVal);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                break;
            case "SUBS":
                aluResult = alu.subtract(rnVal, rmVal);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                cpu.getFlagsRegister().updateFlags(aluResult);
                break;
            case "MUL":
                aluResult = alu.multiply(rnVal, rmVal);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                break;
            case "SDIV":
            case "UDIV":
                try {
                    if (mnemonic.equals("SDIV")) {
                        aluResult = alu.signedDivide(rnVal, rmVal);
                    } else {
                        aluResult = alu.unsignedDivide(rnVal, rmVal);
                    }
                    cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                } catch (ArithmeticException e) {
                    System.err.println("Error at PC 0x" + Long.toHexString(currentPC) + ": " + e.getMessage());
                    // Xử lý lỗi: ghi 0 vào thanh ghi đích và bật cờ Overflow
                    cpu.getRegisterFile().write(i.getRd(), 0);
                    cpu.getFlagsRegister().setV(true);
                }
                break;
            case "AND":
                aluResult = alu.and(rnVal, rmVal);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                break;
            case "ANDS":
                aluResult = alu.and(rnVal, rmVal);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                cpu.getFlagsRegister().updateNZ(aluResult.getResult());
                break;
            case "ORR":
                aluResult = alu.or(rnVal, rmVal);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                break;
            case "EOR":
                aluResult = alu.xor(rnVal, rmVal);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                break;
            case "LSL":
                aluResult = alu.logicalShiftLeft(rnVal, i.getShamt());
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                break;
            case "LSR":
                aluResult = alu.logicalShiftRight(rnVal, i.getShamt());
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                break;
            case "ASR":
                aluResult = alu.arithmeticShiftRight(rnVal, i.getShamt());
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                break;
            case "BR":
                nextPC = rnVal; // Ghi đè PC tiếp theo
                break;
            default:
                throw new IllegalArgumentException("Unhandled R-format mnemonic: " + mnemonic);
        }
        cpu.getPC().setValue(nextPC);
    }

    private void executeIFormat(IFormatInstruction i, long currentPC) {
        long rnVal = cpu.getRegisterFile().read(i.getRn());
        long imm = i.getImmediate();
        ALUResult aluResult;
        String mnemonic = i.getOpcodeMnemonic();

        switch (mnemonic) {
            case "ADDI":
                aluResult = alu.add(rnVal, imm);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                break;
            case "ADDIS":
                aluResult = alu.add(rnVal, imm);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                cpu.getFlagsRegister().updateFlags(aluResult);
                break;
            case "SUBI":
                aluResult = alu.subtract(rnVal, imm);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                break;
            case "SUBIS":
                aluResult = alu.subtract(rnVal, imm);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                cpu.getFlagsRegister().updateFlags(aluResult);
                break;
            case "ANDI":
                aluResult = alu.and(rnVal, imm);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                cpu.getFlagsRegister().updateNZ(aluResult.getResult());
                break;
            case "ORRI":
                aluResult = alu.or(rnVal, imm);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                break;
            case "EORI":
                aluResult = alu.xor(rnVal, imm);
                cpu.getRegisterFile().write(i.getRd(), aluResult.getResult());
                break;
            default:
                throw new IllegalArgumentException("Unhandled I-format mnemonic: " + mnemonic);
        }
        cpu.getPC().setValue(currentPC + 4);
    }


    private void executeDFormat(DFormatInstruction i, long currentPC) {
        long baseAddr = cpu.getRegisterFile().read(i.getRn());
        long offset = i.getDtAddress();
        long effectiveAddress = baseAddr + offset;

        switch (i.getOpcodeMnemonic()) {
            case "LDUR":
                cpu.getRegisterFile().write(i.getRt(), memory.loadDoubleWord(effectiveAddress));
                break;
            case "STUR":
                memory.storeDoubleWord(effectiveAddress, cpu.getRegisterFile().read(i.getRt()));
                break;
            case "LDURSW":
                cpu.getRegisterFile().write(i.getRt(), (long)memory.loadWord(effectiveAddress));
                break;
            case "STURW":
                memory.storeWord(effectiveAddress, (int)cpu.getRegisterFile().read(i.getRt()));
                break;
            case "LDURH":
                cpu.getRegisterFile().write(i.getRt(), memory.loadHalfWord(effectiveAddress) & 0xFFFFL);
                break;
            case "STURH":
                memory.storeHalfWord(effectiveAddress, (short)cpu.getRegisterFile().read(i.getRt()));
                break;
            case "LDURB":
                cpu.getRegisterFile().write(i.getRt(), memory.loadByte(effectiveAddress) & 0xFFL);
                break;
            case "STURB":
                memory.storeByte(effectiveAddress, (byte)cpu.getRegisterFile().read(i.getRt()));
                break;
            default:
                throw new IllegalArgumentException("Unhandled D-format mnemonic: " + i.getOpcodeMnemonic());
        }
        cpu.getPC().setValue(currentPC + 4);
    }

    private void executeCBFormat(CBFormatInstruction i, long currentPC) {
        long offset = (long) i.getCondBrAddress(); 
        boolean takeBranch = false;
        String mnemonic = i.getOpcodeMnemonic();

        if (mnemonic.equals("CBZ")) {
            takeBranch = (cpu.getRegisterFile().read(i.getRtOrCond()) == 0);
        } else if (mnemonic.equals("CBNZ")) {
            takeBranch = (cpu.getRegisterFile().read(i.getRtOrCond()) != 0);
        } else if (mnemonic.startsWith("B.")) {
            takeBranch = checkBranchCondition(mnemonic);
        }

        System.out.println("offset: " + offset + ", takeBranch: " + takeBranch);

        if (takeBranch) {
            cpu.getPC().setValue(currentPC + offset);
        } else {
            cpu.getPC().setValue(currentPC + 4);
        }
    }

    private void executeBFormat(BFormatInstruction i, long currentPC) {
        long offset = (long) i.getBrAddress() * 4;

        if ("BL".equals(i.getOpcodeMnemonic())) {
            cpu.getRegisterFile().write(Constants.LR_REGISTER_INDEX, currentPC + 4);
        }
        
        cpu.getPC().setValue(currentPC + offset);
    }
    
    private void executeIMFormat(IMFormatInstruction i, long currentPC) {
        long imm16 = i.getImmediate();
        int shift = i.getHw() * 16;
        int rd = i.getRd();

        if ("MOVZ".equals(i.getOpcodeMnemonic())) {
            cpu.getRegisterFile().write(rd, imm16 << shift);
        } else if ("MOVK".equals(i.getOpcodeMnemonic())) {
            long currentVal = cpu.getRegisterFile().read(rd);
            long mask = 0xFFFFL << shift;
            long newVal = (currentVal & ~mask) | (imm16 << shift);
            cpu.getRegisterFile().write(rd, newVal);
        }
        cpu.getPC().setValue(currentPC + 4);
    }
    
    private void handleUnknownInstruction(UnknownInstruction i) {
        throw new UnsupportedOperationException(
            "Execution failed: Encountered an unknown instruction with machine code: 0x" + 
            Integer.toHexString(i.getMachineCode())
        );
    }


    /**
     * Kiểm tra xem điều kiện rẽ nhánh có được thỏa mãn hay không,
     * dựa trên trạng thái hiện tại của các cờ trong CPU.
     * @param mnemonic Tên gợi nhớ của lệnh rẽ nhánh (ví dụ: "B.EQ", "B.LT").
     * @return true nếu điều kiện rẽ nhánh đúng, ngược lại là false.
     */
    private boolean checkBranchCondition(String mnemonic) {
        // Lấy đối tượng FlagsRegister từ CPU để truy cập trạng thái cờ
        CPU.FlagsRegister flags = cpu.getFlagsRegister();

        // Lấy giá trị của từng cờ
        boolean N = flags.isN();
        boolean Z = flags.isZ();
        boolean C = flags.isC();
        boolean V = flags.isV();

        switch (mnemonic) {
            // --- Các điều kiện cơ bản ---
            case "B.EQ": // Equal (Z=1)
                return Z;
            case "B.NE": // Not Equal (Z=0)
                return !Z;
            case "B.CS": // Carry Set (C=1), còn gọi là B.HS (Higher or Same, unsigned)
            case "B.HS":
                return C;
            case "B.CC": // Carry Clear (C=0), còn gọi là B.LO (Lower, unsigned)
            case "B.LO":
                return !C;
            case "B.MI": // Minus / Negative (N=1)
                return N;
            case "B.PL": // Plus / Positive or Zero (N=0)
                return !N;
            case "B.VS": // Overflow Set (V=1)
                return V;
            case "B.VC": // Overflow Clear (V=0)
                return !V;
                
            // --- Các điều kiện phức hợp (so sánh có dấu và không dấu) ---
            case "B.HI": // Higher (unsigned) (C=1 AND Z=0)
                return C && !Z;
            case "B.LS": // Lower or Same (unsigned) (C=0 OR Z=1)
                return !C || Z;
            case "B.GE": // Greater or Equal (signed) (N == V)
                return N == V;
            case "B.LT": // Less Than (signed) (N != V)
                return N != V;
            case "B.GT": // Greater Than (signed) (Z=0 AND (N == V))
                return !Z && (N == V);
            case "B.LE": // Less or Equal (signed) (Z=1 OR (N != V))
                return Z || (N != V);
                
            default:
                // Nếu mnemonic không khớp với bất kỳ điều kiện nào, không rẽ nhánh.
                // Hoặc có thể ném lỗi để báo hiệu một mnemonic không được hỗ trợ.
                System.err.println("Warning: Unrecognized branch condition: " + mnemonic);
                return false;
        }
    }
}