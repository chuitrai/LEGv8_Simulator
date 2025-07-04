package main.java.com.mydomain.legv8simulator.simulator.control;

import main.java.com.mydomain.legv8simulator.core.ALUOperation;
import main.java.com.mydomain.legv8simulator.instruction.*;
import main.java.com.mydomain.legv8simulator.simulator.control.ControlSignals;

/**
 * Lớp ControlUnit mô phỏng bộ điều khiển trung tâm của CPU.
 * Nhiệm vụ chính của nó là nhận một lệnh đã được giải mã và tạo ra
 * một tập hợp các tín hiệu điều khiển để điều phối hoạt động của datapath.
 */
public class ControlUnit {

    /**
     * Tạo ra một bộ tín hiệu điều khiển dựa trên một lệnh cụ thể.
     * Đây là logic cốt lõi của Control Unit.
     *
     * @param instruction Đối tượng lệnh đã được giải mã.
     * @return Một đối tượng ControlSignals chứa tất cả các tín hiệu điều khiển cần thiết.
     */
    public ControlSignals generateSignals(Instruction instruction) {
        if (instruction == null || instruction.getFormat() == InstructionFormat.UNKNOWN) {
            // Trường hợp không có lệnh (NOP) hoặc lệnh không xác định
            // Trả về một bộ tín hiệu không làm gì cả.
            return new ControlSignals.Builder().build();
        }

        String mnemonic = instruction.getOpcodeMnemonic();
        ControlSignals.Builder signalsBuilder = new ControlSignals.Builder();

        switch (mnemonic) {
            // --- R-Format: ALU operations ---
            case "ADD", "SUB", "AND", "ORR", "EOR", "MUL", "SDIV", "UDIV", "LSL", "LSR", "ASR":
                signalsBuilder
                        .regWrite(true)   // Ghi kết quả vào thanh ghi
                        .aluSrc(false)    // Đầu vào B của ALU là từ thanh ghi (Rm)
                        .aluOperation(getAluOperationFromMnemonic(mnemonic)) // Lấy phép toán ALU từ mnemonic
                        .aluOp(10)
                        .aluControl(getAluControlFromMnemonic(mnemonic));
                        break;

            // --- R-Format: ALU operations with flag setting ---
            case "ADDS", "SUBS", "ANDS":
                signalsBuilder
                        .regWrite(true)
                        .aluSrc(false)
                        .flagWrite(true) // Bật cờ cho phép cập nhật PSTATE
                        .aluOperation(getAluOperationFromMnemonic(mnemonic))
                        .aluOp(10)
                        .aluControl(getAluControlFromMnemonic(mnemonic));
                break;
            
            // --- R-Format: Branch to Register ---
            case "BR":
                signalsBuilder
                        .uncondBranch(true) // Là một loại rẽ nhánh
                        .aluControl(getAluControlFromMnemonic(mnemonic));
                        break;


            // --- I-Format ---
            case "ADDI", "SUBI", "ANDI", "ORRI", "EORI":
                signalsBuilder
                        .regWrite(true)
                        .aluSrc(true)   // Đầu vào B của ALU là từ immediate
                        .aluOperation(getAluOperationFromMnemonic(mnemonic))
                        .aluOp(10)
                        .aluControl(getAluControlFromMnemonic(mnemonic));
                break;
            
            case "ADDIS", "SUBIS":
                signalsBuilder
                        .regWrite(true)
                        .aluSrc(true)
                        .flagWrite(true)
                        .aluOperation(getAluOperationFromMnemonic(mnemonic))
                        .aluOp(10)
                        .aluControl(getAluControlFromMnemonic(mnemonic));
                break;
                
            // --- D-Format: Load instructions ---
            case "LDUR", "LDURSW", "LDURH", "LDURB":
                signalsBuilder
                        .regWrite(true)   // Ghi dữ liệu từ memory vào thanh ghi
                        .memRead(true)    // Đọc từ memory
                        .memToReg(true)   // Dữ liệu ghi vào thanh ghi đến từ memory
                        .aluSrc(true)     // ALU tính địa chỉ (base + offset)
                        .aluOperation(ALUOperation.ADD)
                        .aluOp(0)
                        .aluControl(getAluControlFromMnemonic(mnemonic));
                break;

            // --- D-Format: Store instructions ---
            case "STUR", "STURW", "STURH", "STURB":
                signalsBuilder
                        .reg2Loc(true)
                        .memWrite(true)   // Ghi vào memory
                        .aluSrc(true)     // ALU tính địa chỉ (base + offset)
                        .aluOperation(ALUOperation.ADD)
                        .aluOp(0)
                        .aluControl(getAluControlFromMnemonic(mnemonic));
                break;

            // --- Branch Formats ---
            case "B", "BL":
                signalsBuilder
                        .uncondBranch(true)
                        .aluOp(-1)
                        .aluControl(getAluControlFromMnemonic(mnemonic));
                break;

            case "CBZ", "CBNZ":
                signalsBuilder
                        .reg2Loc(true)
                        .zeroBranch(true)
                        .aluOperation(ALUOperation.SUBTRACT) // ALU cần thực hiện phép trừ để kiểm tra cờ Zero
                        .aluOp(1)
                        .aluControl(getAluControlFromMnemonic(mnemonic)); 
                break;
            
            // B.cond: Rẽ nhánh dựa trên cờ PSTATE
            case "B.EQ", "B.NE", "B.HS", "B.CS", "B.LO", "B.CC", "B.MI",
                 "B.PL", "B.VS", "B.VC", "B.HI", "B.LS", "B.GE", "B.LT", "B.GT", "B.LE":
                signalsBuilder
                        .reg2Loc(true)
                        .flagBranch(true)
                        .aluOp(-1)
                        .aluControl(-1); 
                break;

            // --- IM-Format ---
            case "MOVZ", "MOVK":
                signalsBuilder
                        .regWrite(true)
                        .aluOp(11)
                        .aluControl(getAluControlFromMnemonic(mnemonic)); // MOVZ/MOVK không cần ALUOperation
                // MOVZ/MOVK có thể được thực hiện mà không cần ALU trong một số thiết kế,
                // nhưng để đơn giản, chúng ta có thể dùng ALU để chuyển tiếp giá trị.
                // Ở đây, ta không cần ALU, dữ liệu sẽ được tạo và ghi trực tiếp.
                // Ta sẽ để aluOperation mặc định.
                break;
                
            // --- System ---
            case "HALT":
                // HALT không tạo ra tín hiệu điều khiển nào, nó sẽ được xử lý ở Simulator.
                break;

            default:
                System.err.println("Warning: ControlUnit received an unhandled mnemonic: " + mnemonic);
                break;
        }

        return signalsBuilder.build();
    }

    /**
     * Hàm helper để ánh xạ từ mnemonic của lệnh sang phép toán ALU tương ứng.
     * Giúp tránh lặp lại logic switch-case.
     *
     * @param mnemonic Tên gợi nhớ của lệnh.
     * @return ALUOperation enum tương ứng.
     */
    private ALUOperation getAluOperationFromMnemonic(String mnemonic) {
        if (mnemonic.startsWith("ADD")) return ALUOperation.ADD;
        if (mnemonic.startsWith("SUB")) return ALUOperation.SUBTRACT;
        if (mnemonic.startsWith("AND")) return ALUOperation.AND;
        if (mnemonic.startsWith("ORR")) return ALUOperation.OR;
        if (mnemonic.startsWith("EOR")) return ALUOperation.XOR;
        if (mnemonic.equals("LSL")) return ALUOperation.LOGICAL_SHIFT_LEFT;
        if (mnemonic.equals("LSR")) return ALUOperation.LOGICAL_SHIFT_RIGHT;
        if (mnemonic.equals("ASR")) return ALUOperation.ARITHMETIC_SHIFT_RIGHT;
        if (mnemonic.equals("SDIV")) return ALUOperation.SIGNED_DIVIDE;
        if (mnemonic.equals("UDIV")) return ALUOperation.UNSIGNED_DIVIDE;
        
        // Trả về một giá trị mặc định hoặc ném lỗi nếu không tìm thấy
        throw new IllegalArgumentException("No ALU operation defined for mnemonic: " + mnemonic);
    }
    /**
     * Hàm kiểm tra Mnemonic để xác định aluControl.
     * @param mnemonic Tên gợi nhớ của lệnh.
     * @return Giá trị ALUControl tương ứng với mnemonic là số binary.
     */
    private int getAluControlFromMnemonic(String mnemonic) {
        switch (mnemonic) {
            case "ADD", "ADDI", "ADDS", "ADDIS":
                return 0b0010; // ADD
            case "SUB", "SUBI", "SUBS", "SUBIS":
                return 0b0110; // SUBTRACT
            case "AND", "ANDI", "ANDS", "ANDIS":
                return 0b0000; // AND
            case "ORR", "ORRI":
                return 0b0001; // OR
            case "EOR", "EORI":
                return 0b0011; // XOR
            case "LSL":
                return 0b1000; // Logical Shift Left
            case "LSR":
                return 0b1001; // Logical Shift Right
            case "ASR":
                return 0b0010; // Arithmetic Shift Right
            case "SDIV":
                return 0b1111; // Signed Divide
            case "UDIV":
                return 0b1111; // Unsigned Divide
            case "MUL":
                return 0b1100; // Multiply
            case "MOVZ": 
                return 0b1111; // Move Zero
            case "MOVK":
                return 0b1110; // Move Keep
            case "B", "BL":
                return -1; // Không cần ALUControl cho rẽ nhánh
            case "CBZ", "CBNZ":
                return 0b0111; // ALU cần thực hiện phép trừ để kiểm
        }
        return 10;
    }
}