package main.java.com.mydomain.legv8simulator.core;

/**
 * Lớp đại diện cho một thanh ghi (Register) đơn lẻ trong kiến trúc CPU.
 * Nó không chỉ lưu giá trị 64-bit mà còn chứa các thông tin metadata
 * như tên, chỉ số và loại, giúp cho việc debug và phát triển dễ dàng hơn.
 *
 * Việc sử dụng lớp này thay cho kiểu 'long' nguyên thủy giúp:
 * 1. Đóng gói hành vi và dữ liệu của một thanh ghi.
 * 2. Dễ dàng mở rộng trong tương lai (ví dụ: thêm cơ chế theo dõi thay đổi cho GUI).
 * 3. Làm cho code ở các lớp cấp cao hơn (như CPU) trở nên tường minh và hướng đối tượng.
 */
public class Register {

    // --- Thuộc tính của thanh ghi ---

    private final String name;          // Tên định danh, ví dụ: "X0", "X30", "PC"
    private final RegisterType type;    // Loại thanh ghi (GENERAL_PURPOSE, PROGRAM_COUNTER, ...)
    private final int index;            // Chỉ số trong RegisterFile (0-31), hoặc -1 nếu không thuộc RegisterFile

    private long value;                 // Giá trị 64-bit hiện tại của thanh ghi

    // --- Constructors ---

    /**
     * Constructor chính, dùng cho các thanh ghi có trong RegisterFile (X0-X31).
     *
     * @param name  Tên của thanh ghi (ví dụ: "X0", "XZR").
     * @param index Chỉ số của thanh ghi trong mảng (0-31).
     * @param type  Loại thanh ghi, thường là GENERAL_PURPOSE, STACK_POINTER, etc.
     */
    public Register(String name, int index, RegisterType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Register name cannot be null or empty.");
        }
        this.name = name;
        this.index = index;
        this.type = type;
        this.value = 0L; // Khởi tạo giá trị mặc định là 0
    }

    /**
     * Constructor tiện ích, dùng cho các thanh ghi đặc biệt không có chỉ số trong RegisterFile.
     * Ví dụ: PC, PSTATE.
     *
     * @param name Tên của thanh ghi (ví dụ: "PC").
     * @param type Loại thanh ghi (ví dụ: PROGRAM_COUNTER).
     */
    public Register(String name, RegisterType type) {
        // Gọi constructor chính với index = -1 để biểu thị nó không thuộc RegisterFile.
        this(name, -1, type);
    }


    // --- Các phương thức truy cập và thay đổi trạng thái (Getters/Setters) ---

    public String getName() {
        return name;
    }

    public RegisterType getType() {
        return type;
    }

    /**
     * @return Chỉ số của thanh ghi trong RegisterFile, hoặc -1 nếu không áp dụng.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Lấy giá trị 64-bit hiện tại của thanh ghi.
     * @return giá trị hiện tại.
     */
    public long getValue() {
        return value;
    }

    /**
     * Thiết lập giá trị 64-bit mới cho thanh ghi.
     * Đây là nơi có thể thêm logic "Observer" trong tương lai để thông báo cho GUI
     * mỗi khi giá trị thanh ghi thay đổi.
     *
     * @param value giá trị mới.
     */
    public void setValue(long value) {
        this.value = value;
    }

    /**
     * Tăng giá trị của thanh ghi lên một lượng cho trước.
     * Tiện ích này đặc biệt hữu ích cho PC.
     * @param amount Lượng cần tăng.
     */
    public void increment(long amount) {
        this.value += amount;
    }

    /**
     * Đặt lại (reset) giá trị của thanh ghi về 0.
     */
    public void reset() {
        this.value = 0L;
    }


    // --- Phương thức tiện ích ---

    /**
     * Cung cấp một biểu diễn chuỗi (String) dễ đọc của thanh ghi,
     * bao gồm tên, giá trị thập phân và giá trị hexa.
     * Rất hữu ích cho việc in ra console để debug.
     *
     * @return Chuỗi đại diện cho trạng thái của thanh ghi.
     */
    @Override
    public String toString() {
        // Dùng String.format để tạo chuỗi có định dạng đẹp
        // %-4s: Chuỗi, căn trái, rộng 4 ký tự
        // %-20d: Số nguyên 64-bit, căn trái, rộng 20 ký tự
        // %016X: Số hexa 64-bit, viết hoa, luôn hiển thị 16 ký tự, điền số 0 vào đầu nếu thiếu.
        return String.format("%-4s: %-20d (0x%016X)", name, value, value);
    }
}