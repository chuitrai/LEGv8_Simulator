// src/main/java/com/yourdomain/legv8simulator/memory/Memory.java
package main.java.com.mydomain.legv8simulator.core;

import main.java.com.mydomain.legv8simulator.utils.BitUtils; // Để chuyển đổi byte <-> int/long

public class Memory {
    private final byte[] data; // Mô phỏng bộ nhớ bằng mảng byte
    private final long size;

    public Memory(long sizeBytes) {
        this.size = sizeBytes;
        this.data = new byte[(int) sizeBytes]; // Chú ý: Java array max size là Integer.MAX_VALUE
        // Khởi tạo bộ nhớ với 0
        for (int i = 0; i < data.length; i++) {
            data[i] = 0;
        }
    }

    public long getSize() {
        return size;
    }

    // Kiểm tra địa chỉ hợp lệ
    private void checkAddress(long address, int numBytes) {
        if (address < 0 || address + numBytes > size) {
            throw new IndexOutOfBoundsException(
                "Memory access violation: address 0x" + String.format("%X", address) +
                ", size " + numBytes + " bytes. Memory size: " + size + " bytes.");
        }
    }

    // Load / Store Word (32-bit for instructions)
    public int loadWord(long address) {
        checkAddress(address, 4);
        int value = 0;
        // Giả sử Little-Endian hoặc Big-Endian tùy thuộc vào thiết kế giả lập của bạn
        // LEGv8 thường hỗ trợ cả hai, nhưng mặc định thường là Little-Endian cho data.
        // Đối với instructions, thường là Little-Endian.
        // Ví dụ: Little-Endian (byte thấp nhất ở địa chỉ thấp nhất)
        value |= (data[(int) address] & 0xFF);
        value |= (data[(int) address + 1] & 0xFF) << 8;
        value |= (data[(int) address + 2] & 0xFF) << 16;
        value |= (data[(int) address + 3] & 0xFF) << 24;
        return value;
    }

    public void storeWord(long address, long value) {
        checkAddress(address, 4);
        data[(int) address] = (byte) (value & 0xFF);
        data[(int) address + 1] = (byte) ((value >> 8) & 0xFF);
        data[(int) address + 2] = (byte) ((value >> 16) & 0xFF);
        data[(int) address + 3] = (byte) ((value >> 24) & 0xFF);
    }

    // Load / Store Double Word (64-bit for register values)
    public long loadDoubleWord(long address) {
        checkAddress(address, 8);
        long value = 0;
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

    // Bạn có thể thêm loadByte, storeByte, loadHalfWord, storeHalfWord nếu các lệnh D-format yêu cầu.
}