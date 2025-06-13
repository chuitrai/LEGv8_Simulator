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

    public InstructionExecutor(CPU cpu, Memory memory) {
        this.cpu = cpu;
        this.memory = memory;
    }

    /**
     * Phương thức điều phối chính để thực thi một lệnh.
     * Nó tự động tăng PC và sau đó gọi phương thức thực thi chuyên biệt.
     * Các lệnh rẽ nhánh sẽ tự ghi đè PC nếu cần.
     * @param instruction Đối tượng lệnh đã được giải mã.
     */
    public void execute(Instruction instruction) {
        long currentPC = cpu.getPC().getValue();

        if (instruction instanceof RFormatInstruction) {
            executeRFormat((RFormatInstruction) instruction);
        } else if (instruction instanceof IFormatInstruction) {
            executeIFormat((IFormatInstruction) instruction);
        } else if (instruction instanceof DFormatInstruction) {
            executeDFormat((DFormatInstruction) instruction);
        } else if (instruction instanceof BFormatInstruction) {
            // Lệnh B/BL cần PC của lệnh hiện tại để tính toán, nên truyền vào
            executeBFormat((BFormatInstruction) instruction, currentPC);
        } else if (instruction instanceof CBFormatInstruction) {
            // Lệnh CB cũng cần PC của lệnh hiện tại
            executeCBFormat((CBFormatInstruction) instruction, currentPC);
        } else if (instruction instanceof IMFormatInstruction) {
            executeIMFormat((IMFormatInstruction) instruction);
        } else if (instruction instanceof UnknownInstruction) {
            handleUnknownInstruction((UnknownInstruction) instruction);
        }
    }

    // =========================================================================
    // I. Arithmetic & Logical Instructions (R-Format)
    // =========================================================================
    private void executeRFormat(RFormatInstruction i) {
        long rnVal = cpu.getRegisterFile().read(i.getRn());
        long rmVal = cpu.getRegisterFile().read(i.getRm());
        long result;
        String mnemonic = i.getOpcodeMnemonic();

        switch (mnemonic) {
            case "ADD":
            case "ADDS":
                result = rnVal + rmVal;
                cpu.getRegisterFile().write(i.getRd(), result);
                if (mnemonic.endsWith("S")) updateFlagsForAdd(result, rnVal, rmVal);
                break;
            case "SUB":
            case "SUBS":
                result = rnVal - rmVal;
                cpu.getRegisterFile().write(i.getRd(), result);
                if (mnemonic.endsWith("S")) updateFlagsForSub(result, rnVal, rmVal);
                break;
            case "MUL":
                result = rnVal * rmVal;
                cpu.getRegisterFile().write(i.getRd(), result);
                break; // MUL không cập nhật cờ
            case "AND":
            case "ANDS":
                result = rnVal & rmVal;
                cpu.getRegisterFile().write(i.getRd(), result);
                if (mnemonic.endsWith("S")) updateFlagsNZ(result);
                break;
            case "ORR":
                result = rnVal | rmVal;
                cpu.getRegisterFile().write(i.getRd(), result);
                break; // ORR không cập nhật cờ
            case "EOR":
                result = rnVal ^ rmVal;
                cpu.getRegisterFile().write(i.getRd(), result);
                break; // EOR không cập nhật cờ
            case "LSL":
                // shamt được lấy từ trường shamt của lệnh R-format
                result = rnVal << i.getShamt();
                cpu.getRegisterFile().write(i.getRd(), result);
                break;
            case "LSR":
                // Dịch phải logic (unsigned)
                result = rnVal >>> i.getShamt();
                cpu.getRegisterFile().write(i.getRd(), result);
                break;
            case "ASR":
                // Dịch phải số học (signed)
                result = rnVal >> i.getShamt();
                cpu.getRegisterFile().write(i.getRd(), result);
                break;
            case "BR":
                // Ghi đè PC đã được tăng lên 4 ở trên
                cpu.getPC().setValue(rnVal);
                break;
            default:
                throw new IllegalArgumentException("Unhandled R-format mnemonic: " + mnemonic);
        }
    }

    // =========================================================================
    // I. Arithmetic & Logical Instructions (I-Format)
    // =========================================================================
    private void executeIFormat(IFormatInstruction i) {
        long rnVal = cpu.getRegisterFile().read(i.getRn());
        long imm = i.getImmediate(); // Immediate đã được sign-extended
        long result;
        String mnemonic = i.getOpcodeMnemonic();

        switch (mnemonic) {
            case "ADDI":
            case "ADDIS":
                result = rnVal + imm;
                cpu.getRegisterFile().write(i.getRd(), result);
                if (mnemonic.endsWith("S")) updateFlagsForAdd(result, rnVal, imm);
                break;
            case "SUBI":
            case "SUBIS":
                result = rnVal - imm;
                cpu.getRegisterFile().write(i.getRd(), result);
                if (mnemonic.endsWith("S")) updateFlagsForSub(result, rnVal, imm);
                break;
            case "ANDI": // ANDI tự động cập nhật cờ theo đặc tả
                result = rnVal & imm;
                cpu.getRegisterFile().write(i.getRd(), result);
                updateFlagsNZ(result);
                break;
            case "ORRI":
                result = rnVal | imm;
                cpu.getRegisterFile().write(i.getRd(), result);
                break; // ORRI không cập nhật cờ
            case "EORI":
                result = rnVal ^ imm;
                cpu.getRegisterFile().write(i.getRd(), result);
                break; // EORI không cập nhật cờ
            default:
                throw new IllegalArgumentException("Unhandled I-format mnemonic: " + mnemonic);
        }
    }


    // =========================================================================
    // III. Data Transfer Instructions (D-Format)
    // =========================================================================
    private void executeDFormat(DFormatInstruction i) {
        long baseAddr = cpu.getRegisterFile().read(i.getRn());
        long offset = i.getDtAddress(); // Đã được sign-extended
        long effectiveAddress = baseAddr + offset;

        switch (i.getOpcodeMnemonic()) {
            case "LDUR":
                cpu.getRegisterFile().write(i.getRt(), memory.loadDoubleWord(effectiveAddress));
                break;
            case "STUR":
                memory.storeDoubleWord(effectiveAddress, cpu.getRegisterFile().read(i.getRt()));
                break;
            case "LDURSW":
                int wordVal = memory.loadWord(effectiveAddress);
                cpu.getRegisterFile().write(i.getRt(), (long)wordVal); // Java's cast sign-extends int to long
                break;
            case "STURW":
                memory.storeWord(effectiveAddress, (int)cpu.getRegisterFile().read(i.getRt()));
                break;
            case "LDURH":
                short halfVal = memory.loadHalfWord(effectiveAddress);
                cpu.getRegisterFile().write(i.getRt(), halfVal & 0xFFFFL); // Zero-extend
                break;
            case "STURH":
                memory.storeHalfWord(effectiveAddress, (short)cpu.getRegisterFile().read(i.getRt()));
                break;
            case "LDURB":
                byte byteVal = memory.loadByte(effectiveAddress);
                cpu.getRegisterFile().write(i.getRt(), byteVal & 0xFFL); // Zero-extend
                break;
            case "STURB":
                memory.storeByte(effectiveAddress, (byte)cpu.getRegisterFile().read(i.getRt()));
                break;
            default:
                throw new IllegalArgumentException("Unhandled D-format mnemonic: " + i.getOpcodeMnemonic());
        }
    }

    // =========================================================================
    // IV. Conditional Branch Instructions (CB-Format)
    // =========================================================================
    private void executeCBFormat(CBFormatInstruction i, long currentPC) {
        long offset = (long) i.getCondBrAddress() * 4;
        boolean takeBranch = false;
        String mnemonic = i.getOpcodeMnemonic();

        if (mnemonic.equals("CBZ")) {
            takeBranch = (cpu.getRegisterFile().read(i.getRtOrCond()) == 0);
        } else if (mnemonic.equals("CBNZ")) {
            takeBranch = (cpu.getRegisterFile().read(i.getRtOrCond()) != 0);
        } else if (mnemonic.startsWith("B.")) {
            takeBranch = checkBranchCondition(mnemonic);
        }

        if (takeBranch) {
            cpu.getPC().setValue(currentPC + offset);
        }
    }

    // =========================================================================
    // V. Unconditional Branch Instructions (B-Format)
    // =========================================================================
    private void executeBFormat(BFormatInstruction i, long currentPC) {
        long offset = (long) i.getBrAddress() * 4;

        if ("BL".equals(i.getOpcodeMnemonic())) {
            // Ghi địa chỉ của lệnh TIẾP THEO vào Link Register (X30)
            cpu.getRegisterFile().write(Constants.LR_REGISTER_INDEX, currentPC + 4);
        }
        
        // Cập nhật PC để nhảy
        cpu.getPC().setValue(currentPC + offset);
    }
    
    // =========================================================================
    // VI. Move Wide Instructions (IM-Format)
    // =========================================================================
    private void executeIMFormat(IMFormatInstruction i) {
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
    }
    
    // =========================================================================
    // VII. System and Unknown Instructions
    // =========================================================================
    private void handleUnknownInstruction(UnknownInstruction i) {
        // Ném một ngoại lệ để báo cho Simulator rằng có lỗi xảy ra.
        // Simulator có thể bắt (catch) và quyết định dừng chương trình.
        throw new UnsupportedOperationException(
            "Execution failed: Encountered an unknown instruction with machine code: 0x" + 
            Integer.toHexString(i.getMachineCode())
        );
    }

    // =========================================================================
    // Helper Methods for Flag Updates
    // =========================================================================

   /**
     * Cập nhật cờ N (Negative) và Z (Zero) dựa trên kết quả của một phép toán.
     * Các phép toán luận lý như ANDS thường chỉ cập nhật hai cờ này.
     * @param result Kết quả 64-bit của phép toán.
     */
    private void updateFlagsNZ(long result) {
        CPU.FlagsRegister flags = cpu.getFlagsRegister();
        flags.updateNZ(result);
    }

    /**
     * Cập nhật đầy đủ 4 cờ (N, Z, C, V) cho một phép CỘNG.
     * @param result Kết quả của phép cộng.
     * @param op1 Toán hạng thứ nhất.
     * @param op2 Toán hạng thứ hai.
     */
    private void updateFlagsForAdd(long result, long op1, long op2) {
        CPU.FlagsRegister flags = cpu.getFlagsRegister();
        flags.updateNZ(result);
        flags.updateCVForAdd(op1, op2, result);
    }

    /**
     * Cập nhật đầy đủ 4 cờ (N, Z, C, V) cho một phép TRỪ.
     * @param result Kết quả của phép trừ.
     * @param op1 Toán hạng thứ nhất (bị trừ).
     * @param op2 Toán hạng thứ hai (số trừ).
     */
    private void updateFlagsForSub(long result, long op1, long op2) {
        CPU.FlagsRegister flags = cpu.getFlagsRegister();
        flags.updateNZ(result);
        flags.updateCVForSub(op1, op2, result);
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