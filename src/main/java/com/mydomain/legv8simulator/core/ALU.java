package main.java.com.mydomain.legv8simulator.core;

/**
 * Lớp ALU (Arithmetic Logic Unit) mô phỏng đơn vị số học và logic của CPU.
 * Nó nhận vào các toán hạng và một mã điều khiển (ALUOperation) để thực hiện
 * phép toán tương ứng và trả về một đối tượng ALUResult.
 * Lớp này không có trạng thái (stateless), mỗi lần gọi là độc lập.
 */
public class ALU {

    /**
     * Phương thức thực thi chính của ALU.
     *
     * @param operation Phép toán cần thực hiện (từ enum ALUOperation).
     * @param operand1 Toán hạng A (64-bit).
     * @param operand2 Toán hạng B (64-bit). Trong các phép dịch chuyển, đây là lượng dịch chuyển (shift amount).
     * @return Một đối tượng ALUResult chứa kết quả và các cờ trạng thái.
     * @throws ArithmeticException nếu thực hiện phép chia cho 0.
     * @throws UnsupportedOperationException nếu phép toán không được hỗ trợ.
     */
    public ALUResult execute(ALUOperation operation, long operand1, long operand2) {
        long result;
        boolean n, z, c = false, v = false; // Khởi tạo cờ C và V là false

        switch (operation) {
            case ADD:
                result = operand1 + operand2;
                n = result < 0;
                z = result == 0;
                // Carry for addition (unsigned overflow)
                c = Long.compareUnsigned(result, operand1) < 0;
                // Overflow for addition (signed overflow)
                v = ((operand1 > 0 && operand2 > 0 && result < 0) ||
                     (operand1 < 0 && operand2 < 0 && result >= 0));
                break;

            case SUBTRACT:
                result = operand1 - operand2;
                n = result < 0;
                z = result == 0;
                // Carry for subtraction (no borrow): op1 >= op2 (unsigned)
                c = Long.compareUnsigned(operand1, operand2) >= 0;
                // Overflow for subtraction (signed overflow)
                v = ((operand1 > 0 && operand2 < 0 && result < 0) ||
                     (operand1 < 0 && operand2 > 0 && result >= 0));
                break;

            case AND:
                result = operand1 & operand2;
                n = result < 0;
                z = result == 0;
                // c và v không bị ảnh hưởng bởi phép toán AND
                break;
            
            case OR:
                result = operand1 | operand2;
                n = result < 0;
                z = result == 0;
                // c và v không bị ảnh hưởng bởi phép toán OR
                break;
            
            case XOR:
                result = operand1 ^ operand2;
                n = result < 0;
                z = result == 0;
                // c và v không bị ảnh hưởng bởi phép toán XOR
                break;

            case LOGICAL_SHIFT_LEFT:
                // operand2 ở đây là shift amount (lượng dịch chuyển)
                result = operand1 << operand2;
                n = result < 0;
                z = result == 0;
                // Các phép dịch chuyển thường không cập nhật C, V trong mô hình đơn giản
                break;

            case LOGICAL_SHIFT_RIGHT:
                // Dịch phải logic (>>>) luôn điền số 0 vào các bit cao
                result = operand1 >>> operand2;
                n = result < 0;
                z = result == 0;
                break;

            case ARITHMETIC_SHIFT_RIGHT:
                // Dịch phải số học (>>) giữ lại bit dấu (sao chép bit cao nhất)
                result = operand1 >> operand2;
                n = result < 0;
                z = result == 0;
                break;

            case SIGNED_DIVIDE:
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                result = operand1 / operand2;
                // Phép chia thường không cập nhật cờ
                n = result < 0;
                z = result == 0;
                break;

            case UNSIGNED_DIVIDE:
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                result = Long.divideUnsigned(operand1, operand2);
                n = result < 0; // Vẫn có thể tính cờ N, Z cho kết quả
                z = result == 0;
                break;
                
            case PASS_THROUGH_B:
                // Phép toán đặc biệt này chỉ đơn giản là chuyển tiếp toán hạng B
                // Hữu ích cho các lệnh như MOV Rd, #imm, ALU sẽ nhận 0 và imm,
                // và chỉ cần chuyển tiếp imm.
                result = operand2;
                n = result < 0;
                z = result == 0;
                break;

            default:
                throw new UnsupportedOperationException("ALU operation not supported: " + operation);
        }

        return new ALUResult(result, n, z, c, v);
    }
}