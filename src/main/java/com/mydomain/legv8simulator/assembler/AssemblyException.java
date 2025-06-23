package main.java.com.mydomain.legv8simulator.assembler;

/**
 * Lớp exception tùy chỉnh cho các lỗi xảy ra trong quá trình hợp dịch.
 * Việc có một lớp exception riêng giúp phân biệt lỗi hợp dịch với các lỗi hệ thống khác.
 */
public class AssemblyException extends Exception {

    private int lineNumber = -1; // -1 cho biết không có thông tin về số dòng

    /**
     * Constructor với chỉ một thông báo lỗi.
     * @param message Thông điệp mô tả lỗi.
     */
    public AssemblyException(String message) {
        super(message);
    }

    /**
     * Constructor với thông báo lỗi và số dòng nơi lỗi xảy ra.
     * @param message Thông điệp mô tả lỗi.
     * @param lineNumber Số dòng trong file assembly gây ra lỗi.
     */
    public AssemblyException(String message, int lineNumber) {
        super(message + " (at line " + lineNumber + ")");
        this.lineNumber = lineNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}