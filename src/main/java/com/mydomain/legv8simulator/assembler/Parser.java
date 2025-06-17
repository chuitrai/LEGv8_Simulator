package main.java.com.mydomain.legv8simulator.assembler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

/**
 * Lớp Parser chịu trách nhiệm phân tích cú pháp từng dòng của mã assembly.
 * Nó làm sạch dòng, xác định nhãn, và bóc tách lệnh thành mnemonic và các toán hạng.
 */
public class Parser {

    // Regex để tìm một nhãn ở đầu dòng (ví dụ: "LOOP:", "MAIN:")
    private static final Pattern LABEL_PATTERN = Pattern.compile("^\\s*([a-zA-Z_][a-zA-Z0-9_]*):");

    /**
     * Phân tích một dòng duy nhất của mã assembly.
     *
     * @param line Dòng code cần phân tích.
     * @param lineNumber Số dòng hiện tại, để báo lỗi.
     * @return Một đối tượng ParseResult chứa thông tin đã được phân tích.
     *         Trả về null nếu dòng này là trống hoặc chỉ chứa comment.
     * @throws AssemblyException nếu cú pháp của dòng bị lỗi.
     */
    public ParseResult parse(String line, int lineNumber) throws AssemblyException {
        // 1. Làm sạch dòng: Xóa comment và khoảng trắng thừa
        String noCommentLine = line.split("//|;")[0]; // Xóa comment
        String cleanLine = noCommentLine.trim();

        if (cleanLine.isEmpty()) {
            return null; // Bỏ qua dòng trống hoặc chỉ chứa comment
        }

        // 2. Tìm và trích xuất nhãn (nếu có)
        Matcher labelMatcher = LABEL_PATTERN.matcher(cleanLine);
        String label = null;
        if (labelMatcher.find()) {
            label = labelMatcher.group(1);
            // Sau khi lấy được nhãn, xóa nó khỏi dòng để xử lý phần lệnh
            cleanLine = cleanLine.substring(labelMatcher.end()).trim();
        }
        
        // 3. Phân tích phần lệnh còn lại (nếu có)
        ParsedInstruction instruction = null;
        if (!cleanLine.isEmpty()) {
            // Tách mnemonic (từ đầu tiên) và phần toán hạng còn lại
            String[] parts = cleanLine.split("\\s+", 2);
            String mnemonic = parts[0];
            String[] operands = new String[0]; // Mảng rỗng nếu không có toán hạng

            if (parts.length > 1) {
                // Tách các toán hạng bằng dấu phẩy, và xóa khoảng trắng xung quanh mỗi toán hạng nếu sau đó không phải dạng ", ["
                operands = Arrays.stream(parts[1].split(","))
                                 .map(String::trim)
                                 .toArray(String[]::new);
            }
            
            // Kiểm tra operands[2] có kết thúc là dấu ngoặc vuông không thì nối với toán hạng trước đó
            if (operands.length > 0 && operands[operands.length - 1].endsWith("]")) {
                // Nếu toán hạng cuối cùng kết thúc bằng dấu ngoặc vuông, nối nó với toán hạng trước đó
                operands[operands.length - 2] += "," + operands[operands.length - 1];
                operands = Arrays.copyOf(operands, operands.length - 1); // Loại bỏ toán hạng cuối cùng
            }
            
            instruction = new ParsedInstruction(mnemonic, operands);
        }

        // Nếu không có nhãn và cũng không có lệnh, đó là một dòng trống đã bị xử lý
        if (label == null && instruction == null) {
            return null;
        }

        return new ParseResult(label, instruction, lineNumber);
    }
}