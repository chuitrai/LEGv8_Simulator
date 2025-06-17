package main.java.com.mydomain.legv8simulator.instruction;

/**
 * Đại diện cho một lệnh không thể nhận dạng hoặc không được hỗ trợ trong trình giả lập.
 * Lớp này được trả về bởi InstructionDecoder khi không thể giải mã một mã máy nhất định.
 * Nó đóng vai trò như một "null object" an toàn, cho phép hệ thống xử lý lỗi một cách duyên dáng
 * thay vì ném NullPointerException.
 */
public class UnknownInstruction implements Instruction {

    private final int machineCode;

    /**
     * Constructor cho một lệnh không xác định.
     * @param machineCode Mã máy 32-bit không thể giải mã được.
     */
    public UnknownInstruction(int machineCode) {
        this.machineCode = machineCode;
    }

    /**
     * Trả về định dạng UNKNOWN.
     * Phương thức này thực thi phương thức trừu tượng từ lớp cha.
     * @return InstructionFormat.UNKNOWN
     * */

    @Override
    public InstructionFormat getFormat() {
        return InstructionFormat.UNKNOWN;
    }

    @Override
    public int getMachineCode() {
        return machineCode;
    }

    @Override
    public String getOpcodeMnemonic() {
        return "UNKNOWN";
    }

    /**
     * Cung cấp một biểu diễn chuỗi rõ ràng cho lệnh không xác định.
     * Hữu ích cho việc ghi log và gỡ lỗi.
     * @return Chuỗi mô tả lệnh không xác định và mã máy của nó.
     */
    @Override
    public String toString() {
        return String.format("UNKNOWN Instruction (Machine Code: 0x%08X)", getMachineCode());
    }
}