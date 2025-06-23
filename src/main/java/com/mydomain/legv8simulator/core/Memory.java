// Đã được refactor
package main.java.com.mydomain.legv8simulator.core;

import main.java.com.mydomain.legv8simulator.utils.*;

/**
 * Mô phỏng bộ nhớ chính (RAM) của máy tính.
 * Dữ liệu được lưu trong một mảng byte và các thao tác load/store được
 * thực hiện thủ công để mô phỏng rõ ràng kiến trúc Little-Endian.
 */
public class Memory {
    private final byte[] data;
    private final long size;

    /**
     * Khởi tạo bộ nhớ với kích thước cho trước.
     * @param sizeBytes Kích thước bộ nhớ theo byte.
     */
    public Memory(long sizeBytes) {
        if (sizeBytes < 0) {
            throw new IllegalArgumentException("Memory size cannot be negative.");
        }
        // Giới hạn kích thước tối đa của bộ nhớ mô phỏng bằng giới hạn của mảng Java
        if (sizeBytes > Integer.MAX_VALUE) {
            System.err.println("Warning: Requested memory size is too large. Capping at 2GB.");
            this.size = Integer.MAX_VALUE;
        } else {
            this.size = sizeBytes;
        }
        this.data = new byte[(int) this.size];
    }

    /**
     * Khởi tạo bộ nhớ với kích thước mặc định từ lớp Constants.
     */
    public Memory() {
        this(Constants.DEFAULT_MEMORY_SIZE); // Sử dụng hằng số
    }

    public long getSize() {
        return size;
    }

    // --- Phương thức kiểm tra địa chỉ ---
    private void checkAddress(long address, int numBytes) {
        if (address < 0 || (address + numBytes) > size) { // Điều kiện đơn giản và hiệu quả hơn
            throw new IndexOutOfBoundsException(
                String.format("Memory access violation: address 0x%X, attempting to access %d byte(s). Memory size: %d bytes.",
                              address, numBytes, size)
            );
        }
    }

    // --- Các phương thức Load/Store (giữ nguyên logic của bạn vì nó rất tốt) ---
    // (Toàn bộ các hàm loadByte, loadHalfWord, loadWord, loadDoubleWord của bạn ở đây)
    // (Toàn bộ các hàm storeByte, storeHalfWord, storeWord, storeDoubleWord của bạn ở đây)

    public byte loadByte(long address) {
        checkAddress(address, 1);
        return data[(int) address];
    }

    public short loadHalfWord(long address) {
        checkAddress(address, 2);
        // Little-Endian
        return (short) (((data[(int) address + 1] & 0xFF) << 8) | (data[(int) address] & 0xFF));
    }

    public int loadWord(long address) {
        checkAddress(address, 4);
        // Little-Endian
        return ((data[(int) address + 3] & 0xFF) << 24) |
               ((data[(int) address + 2] & 0xFF) << 16) |
               ((data[(int) address + 1] & 0xFF) << 8)  |
               ((data[(int) address] & 0xFF));
    }

    public long loadDoubleWord(long address) {
        checkAddress(address, 8);
        // Little-Endian
        return ((long)(data[(int) address + 7] & 0xFF) << 56) |
               ((long)(data[(int) address + 6] & 0xFF) << 48) |
               ((long)(data[(int) address + 5] & 0xFF) << 40) |
               ((long)(data[(int) address + 4] & 0xFF) << 32) |
               ((long)(data[(int) address + 3] & 0xFF) << 24) |
               ((long)(data[(int) address + 2] & 0xFF) << 16) |
               ((long)(data[(int) address + 1] & 0xFF) << 8)  |
               ((long)(data[(int) address] & 0xFF));
    }

    public void storeByte(long address, byte value) {
        checkAddress(address, 1);
        data[(int) address] = value;
    }

    public void storeHalfWord(long address, short value) {
        checkAddress(address, 2);
        data[(int) address] = (byte) (value & 0xFF);
        data[(int) address + 1] = (byte) ((value >> 8) & 0xFF);
    }

    public void storeWord(long address, int value) {
        checkAddress(address, 4);
        data[(int) address] = (byte) (value & 0xFF);
        data[(int) address + 1] = (byte) ((value >> 8) & 0xFF);
        data[(int) address + 2] = (byte) ((value >> 16) & 0xFF);
        data[(int) address + 3] = (byte) ((value >> 24) & 0xFF);
    }

    public void storeDoubleWord(long address, long value) {
        checkAddress(address, 8);
        data[(int) address] = (byte) (value & 0xFF);
        data[(int) address + 1] = (byte) ((value >> 8) & 0xFF);
        data[(int) address + 2] = (byte) ((value >> 16) & 0xFF);
        data[(int) address + 3] = (byte) ((value >> 24) & 0xFF);
        data[(int) address + 4] = (byte) ((value >> 32) & 0xFF);
        data[(int) address + 5] = (byte) ((value >> 40) & 0xFF);
        data[(int) address + 6] = (byte) ((value >> 48) & 0xFF);
        data[(int) address + 7] = (byte) ((value >> 56) & 0xFF);
    }

    /**
     * In ra một phần của bộ nhớ ra console dưới dạng hexa và ASCII.
     * Rất hữu ích để kiểm tra trực quan một vùng dữ liệu.
     * @param startAddress Địa chỉ bắt đầu.
     * @param numBytes Số byte cần in.
     */
    public void dump(long startAddress, int numBytes) {
        int bytesPerRow = 16; // Mặc định in 16 byte mỗi hàng
        if (startAddress < 0 || numBytes <= 0) {
            System.out.println("Invalid dump parameters.");
            return;
        }

        System.out.println("\n--- Memory Dump (from 0x" + Long.toHexString(startAddress) + ", " + numBytes + " bytes) ---");
        for (long addr = startAddress; addr < startAddress + numBytes; ) {
            System.out.printf("0x%08X: ", addr); // In địa chỉ

            // In giá trị Hexa
            for (int i = 0; i < bytesPerRow; i++) {
                if (addr + i < startAddress + numBytes && addr + i < size) {
                    System.out.printf("%02X ", data[(int) (addr + i)]);
                } else {
                    System.out.print("   ");
                }
            }
            System.out.print(" | ");

            // In giá trị ASCII
            for (int i = 0; i < bytesPerRow; i++) {
                if (addr + i < startAddress + numBytes && addr + i < size) {
                    byte b = data[(int) (addr + i)];
                    char c = (b >= 32 && b < 127) ? (char) b : '.';
                    System.out.print(c);
                }
            }
            System.out.println();
            addr += bytesPerRow;
        }
        System.out.println("---------------------------------------------------------");
    }
}