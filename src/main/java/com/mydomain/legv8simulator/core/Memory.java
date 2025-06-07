// src/main/java/com/yourdomain/legv8simulator/memory/Memory.java
package main.java.com.mydomain.legv8simulator.core;


public class Memory {
    private final byte[] data; // Mô phỏng bộ nhớ bằng mảng byte
    private final long size;   // Kích thước bộ nhớ tính bằng byte

    /**
     * Constructor để tạo một đối tượng Memory.
     * @param sizeBytes Kích thước của bộ nhớ tính bằng byte.
     * @throws IllegalArgumentException nếu sizeBytes quá lớn cho mảng Java hoặc âm.
     */
    public Memory(long sizeBytes) {
        if (sizeBytes < 0) {
            throw new IllegalArgumentException("Memory size cannot be negative.");
        }
        if (sizeBytes > Integer.MAX_VALUE) {
            // Mảng Java được đánh chỉ số bằng int, nên kích thước tối đa bị giới hạn
            // Đối với trình mô phỏng, kích thước nhỏ hơn (ví dụ vài MB) thường là đủ
            System.out.println("Warning: Requested memory size (" + sizeBytes +
                               " bytes) exceeds practical Java array limit. " +
                               "Consider a smaller memory size for simulation.");
            // Bạn có thể ném lỗi hoặc giới hạn kích thước ở đây
            // throw new IllegalArgumentException("Memory size exceeds Integer.MAX_VALUE.");
            this.size = Integer.MAX_VALUE; // Hoặc một giới hạn thực tế hơn
        } else {
            this.size = sizeBytes;
        }
        this.data = new byte[(int) this.size];
        // Khởi tạo bộ nhớ với 0 (mảng byte trong Java mặc định là 0)
        // for (int i = 0; i < data.length; i++) {
        //     data[i] = 0;
        // }
    }

    public long getSize() {
        return size;
    }

    /**
     * Kiểm tra xem địa chỉ truy cập có hợp lệ không.
     * @param address Địa chỉ bắt đầu truy cập.
     * @param numBytes Số byte cần truy cập.
     * @throws IndexOutOfBoundsException nếu truy cập ra ngoài vùng nhớ hợp lệ.
     */
    private void checkAddress(long address, int numBytes) {
        if (address < 0 || (address + numBytes -1) >= size || address + numBytes < address /* overflow check */) {
            throw new IndexOutOfBoundsException(
                "Memory access violation: address 0x" + String.format("%X", address) +
                ", attempting to access " + numBytes + " byte(s). Memory size: " + size + " bytes.");
        }
    }

    // --- Load operations (Little-Endian assumed) ---

    /**
     * Nạp một byte (8-bit) từ bộ nhớ.
     * @param address Địa chỉ của byte cần nạp.
     * @return Giá trị byte (dưới dạng short để tránh vấn đề dấu khi ép kiểu lên int).
     */
    public byte loadByte(long address) {
        checkAddress(address, 1);
        return data[(int) address];
    }

    /**
     * Nạp một half-word (16-bit) từ bộ nhớ.
     * Giá trị trả về là short (16-bit).
     * @param address Địa chỉ bắt đầu của half-word (byte thấp).
     * @return Giá trị half-word.
     */
    public short loadHalfWord(long address) {
        checkAddress(address, 2);
        short value = 0;
        // Little-Endian: byte thấp ở địa chỉ thấp
        value |= (data[(int) address] & 0xFF);          // Byte thấp
        value |= (data[(int) address + 1] & 0xFF) << 8; // Byte cao
        return value;
    }

    /**
     * Nạp một word (32-bit) từ bộ nhớ.
     * Giá trị trả về là int (32-bit).
     * @param address Địa chỉ bắt đầu của word (byte thấp nhất).
     * @return Giá trị word.
     */
    public int loadWord(long address) {
        checkAddress(address, 4);
        int value = 0;
        // Little-Endian
        value |= (data[(int) address] & 0xFF);
        value |= (data[(int) address + 1] & 0xFF) << 8;
        value |= (data[(int) address + 2] & 0xFF) << 16;
        value |= (data[(int) address + 3] & 0xFF) << 24;
        return value;
    }

    /**
     * Nạp một double-word (64-bit) từ bộ nhớ.
     * Giá trị trả về là long (64-bit).
     * @param address Địa chỉ bắt đầu của double-word (byte thấp nhất).
     * @return Giá trị double-word.
     */
    public long loadDoubleWord(long address) {
        checkAddress(address, 8);
        long value = 0;
        // Little-Endian
        value |= ((long) data[(int) address] & 0xFF);
        value |= ((long) data[(int) address + 1] & 0xFF) << 8;
        value |= ((long) data[(int) address + 2] & 0xFF) << 16;
        value |= ((long) data[(int) address + 3] & 0xFF) << 24;
        value |= ((long) data[(int) address + 4] & 0xFF) << 32;
        value |= ((long) data[(int) address + 5] & 0xFF) << 40;
        value |= ((long) data[(int) address + 6] & 0xFF) << 48;
        value |= ((long) data[(int) address + 7] & 0xFF) << 56;
        return value;
    }

    // --- Store operations (Little-Endian assumed) ---

    /**
     * Lưu một byte (8-bit) vào bộ nhớ.
     * @param address Địa chỉ để lưu byte.
     * @param value Giá trị byte cần lưu.
     */
    public void storeByte(long address, byte value) {
        checkAddress(address, 1);
        data[(int) address] = value;
    }

    /**
     * Lưu một half-word (16-bit) vào bộ nhớ.
     * @param address Địa chỉ bắt đầu để lưu half-word (byte thấp).
     * @param value Giá trị short (16-bit) cần lưu.
     */
    public void storeHalfWord(long address, short value) {
        checkAddress(address, 2);
        // Little-Endian
        data[(int) address] = (byte) (value & 0xFF);          // Byte thấp
        data[(int) address + 1] = (byte) ((value >> 8) & 0xFF); // Byte cao
    }

    /**
     * Lưu một word (32-bit) vào bộ nhớ.
     * @param address Địa chỉ bắt đầu để lưu word (byte thấp nhất).
     * @param value Giá trị int (32-bit) cần lưu.
     */
    public void storeWord(long address, int value) { // Đổi tham số value thành int
        checkAddress(address, 4);
        // Little-Endian
        data[(int) address] = (byte) (value & 0xFF);
        data[(int) address + 1] = (byte) ((value >> 8) & 0xFF);
        data[(int) address + 2] = (byte) ((value >> 16) & 0xFF);
        data[(int) address + 3] = (byte) ((value >> 24) & 0xFF);
    }

    /**
     * Lưu một double-word (64-bit) vào bộ nhớ.
     * @param address Địa chỉ bắt đầu để lưu double-word (byte thấp nhất).
     * @param value Giá trị long (64-bit) cần lưu.
     */
    public void storeDoubleWord(long address, long value) {
        checkAddress(address, 8);
        // Little-Endian
        data[(int) address] = (byte) (value & 0xFF);
        data[(int) address + 1] = (byte) ((value >> 8) & 0xFF);
        data[(int) address + 2] = (byte) ((value >> 16) & 0xFF);
        data[(int) address + 3] = (byte) ((value >> 24) & 0xFF);
        data[(int) address + 4] = (byte) ((value >> 32) & 0xFF);
        data[(int) address + 5] = (byte) ((value >> 40) & 0xFF);
        data[(int) address + 6] = (byte) ((value >> 48) & 0xFF);
        data[(int) address + 7] = (byte) ((value >> 56) & 0xFF);
    }

    // --- Tiện ích khác (tùy chọn) ---

    /**
     * In một phần của bộ nhớ ra console (hữu ích cho gỡ lỗi).
     * @param startAddress Địa chỉ bắt đầu.
     * @param numBytes Số byte cần in.
     * @param bytesPerRow Số byte trên mỗi dòng khi in.
     */
    public void dump(long startAddress, int numBytes, int bytesPerRow) {
        checkAddress(startAddress, numBytes);
        System.out.println("\n--- Memory Dump (from 0x" + String.format("%X", startAddress) + ", " + numBytes + " bytes) ---");
        for (long addr = startAddress; addr < startAddress + numBytes; ) {
            System.out.printf("0x%04X: ", addr); // Địa chỉ
            for (int i = 0; i < bytesPerRow; i++) {
                if (addr + i < startAddress + numBytes && addr + i < size) {
                    System.out.printf("%02X ", data[(int) (addr + i)]);
                } else {
                    System.out.print("   "); // In khoảng trắng nếu ra ngoài vùng dump hoặc cuối bộ nhớ
                }
            }
            System.out.print(" | ");
            for (int i = 0; i < bytesPerRow; i++) {
                if (addr + i < startAddress + numBytes && addr + i < size) {
                    byte b = data[(int) (addr + i)];
                    char c = (b >= 32 && b <= 126) ? (char) b : '.'; // In ký tự ASCII hoặc '.'
                    System.out.print(c);
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
            addr += bytesPerRow;
        }
        System.out.println("----------------------------------------------------");
    }
}