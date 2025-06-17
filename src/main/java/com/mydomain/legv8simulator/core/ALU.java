package main.java.com.mydomain.legv8simulator.core;

/**
 * Lớp ALU (Arithmetic Logic Unit) mô phỏng đơn vị số học và logic của CPU.
 * Nó thực hiện các phép toán và tính toán các cờ trạng thái tương ứng,
 * sau đó trả về một đối tượng ALUResult.
 * Lớp này không có trạng thái (stateless).
 */
public class ALU {

    // --- Phép toán số học ---

    public ALUResult add(long operand1, long operand2) {
        long result = operand1 + operand2;
        boolean n = result < 0;
        boolean z = result == 0;
        // Carry for addition (unsigned overflow)
        boolean c = Long.compareUnsigned(result, operand1) < 0;
        // Overflow for addition (signed overflow)
        boolean v = ((operand1 > 0 && operand2 > 0 && result < 0) ||
                     (operand1 < 0 && operand2 < 0 && result >= 0));
        return new ALUResult(result, n, z, c, v);
    }

    public ALUResult subtract(long operand1, long operand2) {
        long result = operand1 - operand2;
        boolean n = result < 0;
        boolean z = result == 0;
        // Carry for subtraction (no borrow): op1 >= op2 (unsigned)
        boolean c = Long.compareUnsigned(operand1, operand2) >= 0;
        // Overflow for subtraction (signed overflow)
        boolean v = ((operand1 > 0 && operand2 < 0 && result < 0) ||
                     (operand1 < 0 && operand2 > 0 && result >= 0));
        return new ALUResult(result, n, z, c, v);
    }

    public ALUResult multiply(long operand1, long operand2) {
        long result = operand1 * operand2;
        // Phép nhân trong LEGv8 cơ bản không cập nhật cờ
        return new ALUResult(result, false, false, false, false);
    }

    public ALUResult signedDivide(long operand1, long operand2) {
        if (operand2 == 0) {
            // Xử lý chia cho 0. Trả về 0 và bật cờ overflow (V) là một cách tiếp cận hợp lý.
            // Hoặc có thể ném một ngoại lệ ArithmeticException.
            // throw new ArithmeticException("Division by zero");
            System.err.println("Warning: Signed division by zero.");
            return new ALUResult(0, false, false, false, true); // Đặt cờ V để báo lỗi
        }
        long result = operand1 / operand2;
        // Phép chia thường không cập nhật cờ trong nhiều kiến trúc
        return new ALUResult(result, false, false, false, false);
    }
    
    public ALUResult unsignedDivide(long operand1, long operand2) {
        if (operand2 == 0) {
            System.err.println("Warning: Unsigned division by zero.");
            return new ALUResult(0, false, false, false, true); // Đặt cờ V để báo lỗi
        }
        // Sử dụng phương thức chia không dấu của lớp Long
        long result = Long.divideUnsigned(operand1, operand2);
        return new ALUResult(result, false, false, false, false);
    }

    // --- Phép toán luận lý ---

    public ALUResult and(long operand1, long operand2) {
        long result = operand1 & operand2;
        boolean n = result < 0;
        boolean z = result == 0;
        // ANDS chỉ cập nhật N và Z. C và V không đổi.
        return new ALUResult(result, n, z, false, false);
    }
    
    public ALUResult or(long operand1, long operand2) {
        long result = operand1 | operand2;
        // ORR không cập nhật cờ
        return new ALUResult(result, false, false, false, false);
    }
    
    public ALUResult xor(long operand1, long operand2) {
        long result = operand1 ^ operand2;
        // EOR không cập nhật cờ
        return new ALUResult(result, false, false, false, false);
    }

    // --- Phép toán dịch bit ---

    public ALUResult logicalShiftLeft(long operand, int shiftAmount) {
        long result = operand << shiftAmount;
        // Phép dịch bit không cập nhật cờ
        return new ALUResult(result, false, false, false, false);
    }

    public ALUResult logicalShiftRight(long operand, int shiftAmount) {
        // Dịch phải logic (>>>) điền số 0 vào các bit cao
        long result = operand >>> shiftAmount;
        return new ALUResult(result, false, false, false, false);
    }

    public ALUResult arithmeticShiftRight(long operand, int shiftAmount) {
        // Dịch phải số học (>>) giữ lại bit dấu
        long result = operand >> shiftAmount;
        return new ALUResult(result, false, false, false, false);
    }
}