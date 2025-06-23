package main.java.com.mydomain.legv8simulator.assembler;

import java.util.HashMap;
import java.util.Map;
import main.java.com.mydomain.legv8simulator.assembler.AssemblyException;

/**
 * Lớp SymbolTable quản lý một bảng các ký hiệu (nhãn) và địa chỉ tương ứng của chúng.
 * Nó được sử dụng trong quá trình hợp dịch 2 lượt (two-pass assembly):
 * - Lượt 1: Điền vào bảng tất cả các nhãn và địa chỉ của chúng.
 * - Lượt 2: Tra cứu địa chỉ của các nhãn khi dịch các lệnh rẽ nhánh.
 */
public class SymbolTable {

    // Sử dụng HashMap để lưu trữ ánh xạ từ tên nhãn (String) tới địa chỉ (Integer).
    private final Map<String, Integer> table;

    /**
     * Constructor khởi tạo một SymbolTable rỗng.
     */
    public SymbolTable() {
        this.table = new HashMap<>();
    }

    /**
     * Thêm một ký hiệu (nhãn) mới vào bảng.
     * Sẽ ném ra một ngoại lệ nếu nhãn đã tồn tại để tránh định nghĩa lại.
     *
     * @param symbol Tên của nhãn (ví dụ: "LOOP").
     * @param address Địa chỉ bộ nhớ tương ứng với nhãn.
     * @throws AssemblyException nếu nhãn đã tồn tại trong bảng.
     */
    public void addSymbol(String symbol, final int address) throws AssemblyException {
        if (contains(symbol)) {
            // Ném lỗi nếu người dùng cố gắng định nghĩa một nhãn đã có
            throw new AssemblyException("Duplicate label definition: '" + symbol + "'");
        }
        table.put(symbol, address); // OKe thêm nhãn mới vào bảng
        System.out.println("Added symbol: " + symbol + " at address 0x" + Integer.toHexString(address).toUpperCase());
    }

    /**
     * Lấy địa chỉ của một ký hiệu (nhãn) đã được định nghĩa.
     * Sẽ ném ra một ngoại lệ nếu nhãn không tồn tại.
     *
     * @param symbol Tên của nhãn cần tra cứu.
     * @return Địa chỉ bộ nhớ (int) của nhãn.
     * @throws AssemblyException nếu nhãn không được tìm thấy trong bảng.
     */
    public int getAddress(String symbol) throws AssemblyException {
        Integer address = table.get(symbol);
        if (address == null) {
            // Ném lỗi nếu một lệnh cố gắng nhảy tới một nhãn không tồn tại
            throw new AssemblyException("Undefined label: '" + symbol + "'");
        }
        return address;
    }

    /**
     * Kiểm tra xem một ký hiệu (nhãn) đã tồn tại trong bảng hay chưa.
     *
     * @param symbol Tên của nhãn cần kiểm tra.
     * @return true nếu nhãn tồn tại, ngược lại là false.
     */
    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }

    /**
     * Xóa tất cả các ký hiệu khỏi bảng.
     * Được gọi để chuẩn bị cho một lần hợp dịch mới.
     */
    public void clear() {
        table.clear();
    }
    
    /**
     * Cung cấp một biểu diễn chuỗi của bảng ký hiệu để debug.
     * @return Một chuỗi liệt kê tất cả các nhãn và địa chỉ của chúng.
     */
    @Override
    public String toString() {
        if (table.isEmpty()) {
            return "SymbolTable is empty.";
        }
        StringBuilder sb = new StringBuilder("--- Symbol Table ---\n");
        for (Map.Entry<String, Integer> entry : table.entrySet()) {
            sb.append(String.format("%-20s : 0x%08X\n", entry.getKey(), entry.getValue()));
        }
        sb.append("--------------------");
        return sb.toString();
    }
}