// src/main/java/com/yourdomain/legv8simulator/utils/BitUtils.java
package main.java.com.mydomain.legv8simulator.utils;

public final class BitUtils {

    // Ngăn không cho tạo instance của lớp tiện ích này
    private BitUtils() {
    }

    /**
     * Trích xuất một chuỗi các bit từ một giá trị integer.
     *
     * @param value    Giá trị integer chứa các bit cần trích xuất.
     * @param startBit Vị trí bit bắt đầu (0-indexed, LSB là 0).
     * @param endBit   Vị trí bit kết thúc (bao gồm).
     * @return Giá trị integer chứa các bit đã được trích xuất, được dịch chuyển về LSB.
     * @throws IllegalArgumentException nếu startBit < 0, endBit > 31, hoặc startBit > endBit.
     */
    public static int extractBits(int value, int startBit, int endBit) {
        if (startBit < 0 || endBit > 31 || startBit > endBit) {
            throw new IllegalArgumentException(String.format("Invalid bit range: start=%d, end=%d. Must be 0 <= start <= end <= 31.", startBit, endBit));
        }
        int numBits = endBit - startBit + 1;
        int mask = (numBits == 32) ? -1 : ((1 << numBits) - 1); // Tạo mask gồm numBits số 1
        return (value >>> startBit) & mask; // Dịch phải logic rồi áp dụng mask
    }

    /**
     * Trích xuất một chuỗi các bit từ một giá trị long.
     *
     * @param value    Giá trị long chứa các bit cần trích xuất.
     * @param startBit Vị trí bit bắt đầu (0-indexed, LSB là 0).
     * @param endBit   Vị trí bit kết thúc (bao gồm).
     * @return Giá trị long chứa các bit đã được trích xuất, được dịch chuyển về LSB.
     * @throws IllegalArgumentException nếu startBit < 0, endBit > 63, hoặc startBit > endBit.
     */
    public static long extractBits(long value, int startBit, int endBit) {
        if (startBit < 0 || endBit > 31 || startBit > endBit) {
            throw new IllegalArgumentException(String.format("Invalid bit range for long: start=%d, end=%d. Must be 0 <= start <= end <= 63.", startBit, endBit));
        }
        int numBits = endBit - startBit + 1;
        long mask = (numBits == 32) ? -1 : ((1 << numBits) - 1); // Tạo mask gồm numBits số 1
        return (value >>> startBit) & mask; // Dịch phải logic rồi áp dụng mask
    }

    /**
     * Mở rộng dấu (sign-extend) một giá trị N-bit thành một giá trị integer 32-bit.
     *
     * @param value   Giá trị cần mở rộng dấu. Bit cao nhất của N-bit này sẽ được dùng làm bit dấu.
     * @param numBits Số lượng bit gốc của giá trị (ví dụ: 9 cho DT_address, 12 cho immediate I-format).
     * @return Giá trị integer 32-bit đã được mở rộng dấu.
     * @throws IllegalArgumentException nếu numBits <= 0 hoặc numBits >= 32.
     */
    public static int signExtend32(int value, int numBits) {
        if (numBits <= 0 || numBits >= 32) {
            // Hoặc trả về value, hoặc ném lỗi nếu numBits không hợp lệ cho sign extension
            // throw new IllegalArgumentException("numBits for signExtend32 must be > 0 and < 32. Got: " + numBits);
            return value; // Nếu numBits là 32, không cần extend. Nếu <=0, không hợp lý.
        }
        int shift = 32 - numBits;
        // Dịch trái để bit dấu lên MSB của int, sau đó dịch phải số học để mở rộng dấu
        return (value << shift) >> shift;
    }

    /**
     * Mở rộng dấu (sign-extend) một giá trị N-bit thành một giá trị long 64-bit.
     *
     * @param value   Giá trị cần mở rộng dấu. Bit cao nhất của N-bit này sẽ được dùng làm bit dấu.
     * @param numBits Số lượng bit gốc của giá trị (ví dụ: 26 cho BR_address).
     * @return Giá trị long 64-bit đã được mở rộng dấu.
     * @throws IllegalArgumentException nếu numBits <= 0 hoặc numBits >= 64.
     */
    public static long signExtend64(long value, int numBits) {
        if (numBits <= 0 || numBits >= 64) {
            // throw new IllegalArgumentException("numBits for signExtend64 must be > 0 and < 64. Got: " + numBits);
            return value;
        }
        int shift = 64 - numBits;
        return (value << shift) >> shift;
    }

    /**
     * Mở rộng bằng số 0 (zero-extend) một giá trị N-bit thành một giá trị integer 32-bit.
     * Các bit cao hơn sẽ được điền bằng 0.
     *
     * @param value   Giá trị cần mở rộng.
     * @param numBits Số lượng bit gốc của giá trị.
     * @return Giá trị integer 32-bit đã được mở rộng bằng 0.
     * @throws IllegalArgumentException nếu numBits <= 0 hoặc numBits > 32.
     */
    public static int zeroExtend32(int value, int numBits) {
        if (numBits <= 0 || numBits > 32) {
            throw new IllegalArgumentException("numBits for zeroExtend32 must be > 0 and <= 32. Got: " + numBits);
        }
        if (numBits == 32) return value;
        int mask = (1 << numBits) - 1;
        return value & mask; // Đảm bảo chỉ các bit thấp được giữ lại, các bit cao hơn là 0
    }

    /**
     * Mở rộng bằng số 0 (zero-extend) một giá trị N-bit thành một giá trị long 64-bit.
     * Các bit cao hơn sẽ được điền bằng 0.
     *
     * @param value   Giá trị cần mở rộng.
     * @param numBits Số lượng bit gốc của giá trị.
     * @return Giá trị long 64-bit đã được mở rộng bằng 0.
     * @throws IllegalArgumentException nếu numBits <= 0 hoặc numBits > 64.
     */
    public static long zeroExtend64(long value, int numBits) {
        if (numBits <= 0 || numBits > 64) {
            throw new IllegalArgumentException("numBits for zeroExtend64 must be > 0 and <= 64. Got: " + numBits);
        }
        if (numBits == 64) return value;
        long mask = (1L << numBits) - 1L;
        return value & mask;
    }

    /**
     * Lấy giá trị của một bit cụ thể.
     * @param value Giá trị integer.
     * @param bitPosition Vị trí bit (0-indexed).
     * @return 1 nếu bit được set, 0 nếu không.
     * @throws IllegalArgumentException nếu bitPosition không hợp lệ.
     */
    public static int getBit(int value, int bitPosition) {
        if (bitPosition < 0 || bitPosition > 31) {
            throw new IllegalArgumentException("Bit position must be between 0 and 31.");
        }
        return (value >> bitPosition) & 1;
    }

    /**
     * Lấy giá trị của một bit cụ thể từ một giá trị long.
     * @param value Giá trị long.
     * @param bitPosition Vị trí bit (0-indexed).
     * @return 1 nếu bit được set, 0 nếu không.
     * @throws IllegalArgumentException nếu bitPosition không hợp lệ.
     */
    public static int getBit(long value, int bitPosition) {
        if (bitPosition < 0 || bitPosition > 63) {
            throw new IllegalArgumentException("Bit position for long must be between 0 and 63.");
        }
        return (int) ((value >> bitPosition) & 1L);
    }

    /**
     * Đặt (set) một bit cụ thể thành 1.
     * @param value Giá trị integer.
     * @param bitPosition Vị trí bit (0-indexed).
     * @return Giá trị mới với bit đã được đặt.
     */
    public static int setBit(int value, int bitPosition) {
        if (bitPosition < 0 || bitPosition > 31) {
            throw new IllegalArgumentException("Bit position must be between 0 and 31.");
        }
        return value | (1 << bitPosition);
    }

    /**
     * Xóa (clear) một bit cụ thể thành 0.
     * @param value Giá trị integer.
     * @param bitPosition Vị trí bit (0-indexed).
     * @return Giá trị mới với bit đã được xóa.
     */
    public static int clearBit(int value, int bitPosition) {
        if (bitPosition < 0 || bitPosition > 31) {
            throw new IllegalArgumentException("Bit position must be between 0 and 31.");
        }
        return value & ~(1 << bitPosition);
    }

    /**
     * Chuyển đổi một mảng byte (big-endian) thành một giá trị integer.
     * @param bytes Mảng byte (tối đa 4 byte).
     * @return Giá trị integer.
     * @throws IllegalArgumentException nếu mảng byte dài hơn 4.
     */
    public static int bytesToIntBigEndian(byte[] bytes) {
        if (bytes.length > 4) {
            throw new IllegalArgumentException("Byte array too long to fit in an int (max 4 bytes).");
        }
        int val = 0;
        for (byte b : bytes) {
            val = (val << 8) | (b & 0xFF);
        }
        return val;
    }

    /**
     * Chuyển đổi một mảng byte (little-endian) thành một giá trị integer.
     * @param bytes Mảng byte (tối đa 4 byte).
     * @return Giá trị integer.
     * @throws IllegalArgumentException nếu mảng byte dài hơn 4.
     */
    public static int bytesToIntLittleEndian(byte[] bytes) {
        if (bytes.length > 4) {
            throw new IllegalArgumentException("Byte array too long to fit in an int (max 4 bytes).");
        }
        int val = 0;
        for (int i = bytes.length - 1; i >= 0; i--) {
            val = (val << 8) | (bytes[i] & 0xFF);
        }
        return val;
    }

    /**
     * Chuyển đổi một giá trị integer thành một mảng byte (big-endian).
     * @param value Giá trị integer.
     * @param numBytes Số byte mong muốn trong mảng kết quả (1 đến 4).
     * @return Mảng byte.
     */
    public static byte[] intToBytesBigEndian(int value, int numBytes) {
        if (numBytes < 1 || numBytes > 4) {
            throw new IllegalArgumentException("Number of bytes must be between 1 and 4.");
        }
        byte[] result = new byte[numBytes];
        for (int i = 0; i < numBytes; i++) {
            result[numBytes - 1 - i] = (byte) (value >> (i * 8));
        }
        return result;
    }

     /**
     * Chuyển đổi một giá trị long thành một mảng byte (big-endian).
     * @param value Giá trị long.
     * @param numBytes Số byte mong muốn trong mảng kết quả (1 đến 8).
     * @return Mảng byte.
     */
    public static byte[] longToBytesBigEndian(long value, int numBytes) {
        if (numBytes < 1 || numBytes > 8) {
            throw new IllegalArgumentException("Number of bytes must be between 1 and 8 for long.");
        }
        byte[] result = new byte[numBytes];
        for (int i = 0; i < numBytes; i++) {
            result[numBytes - 1 - i] = (byte) (value >> (i * 8));
        }
        return result;
    }

    // Bạn có thể thêm các hàm tương tự cho little-endian nếu cần thiết cho project.

    /**
     * In ra biểu diễn nhị phân của một số nguyên, với padding cho đủ 32 bit.
     * @param value Giá trị integer.
     * @return Chuỗi nhị phân 32-bit.
     */
    public static String toBinaryString32(int value) {
        // Ghi thanh nhóm 8 bit, mỗi nhóm cách nhau một dấu cách
        String binaryString = String.format("%32s", Integer.toBinaryString(value)).replace(' ', '0');
        StringBuilder formatted = new StringBuilder();
        return formatted.append(binaryString).insert(8, ' ').insert(17, ' ').insert(26, ' ').toString();
    }

    /**
     * In ra biểu diễn nhị phân của một số long, với padding cho đủ 64 bit.
     * @param value Giá trị long.
     * @return Chuỗi nhị phân 64-bit.
     */
    public static String toBinaryString64(long value) {
        return String.format("%64s", Long.toBinaryString(value)).replace(' ', '0');
    }

    /**
     * In ra một đoạn bit của 1 giá trị interger
     * @param startBit
     * @param endBit
     * @return Chuỗi nhị phân của đoạn bit từ startBit đến endBit
     */
    public static String toBinaryString(int value, int startBit, int endBit) {
        if (startBit < 0 || endBit > 31 || startBit > endBit) {
            throw new IllegalArgumentException(String.format("Invalid bit range: start=%d, end=%d. Must be 0 <= start <= end <= 31.", startBit, endBit));
        }
        int numBits = endBit - startBit + 1;
        int mask = (numBits == 32) ? -1 : ((1 << numBits) - 1);
        int extractedValue = (value >>> startBit) & mask;
        // Chỉ in ra số bit trong khoảng từ startBit đến endBit
        String binaryString = String.format("%" + numBits + "s", Integer.toBinaryString(extractedValue)).replace(' ', '0');
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < binaryString.length(); i++) {
            formatted.append(binaryString.charAt(i));
            if ((i + 1) % 8 == 0 && i < binaryString.length() - 1) {
                formatted.append(' '); // Thêm dấu cách sau mỗi 8 bit
            }
        }
        return formatted.toString();
    }

    /**
     * chuyển một đoạn bit của 1 giá trị binary thành số nguyên bù 2
     * @param startBit
     * @param endBit
     * @return Chuỗi nhị phân của đoạn bit từ startBit đến endBit
     */
    public static int toSignedInt(int value, int startBit, int endBit) {
        if (startBit < 0 || endBit > 31 || startBit > endBit) {
            throw new IllegalArgumentException(String.format("Invalid bit range: start=%d, end=%d. Must be 0 <= start <= end <= 31.", startBit, endBit));
        }
        int numBits = endBit - startBit + 1;
        int mask = (numBits == 32) ? -1 : ((1 << numBits) - 1);
        int extractedValue = (value >>> startBit) & mask;
        // Kiểm tra bit dấu
        if ((extractedValue & (1 << (numBits - 1))) != 0) {
            // Nếu bit dấu là 1, mở rộng dấu
            extractedValue |= ~mask; // Mở rộng dấu cho các bit cao hơn
        }
        return extractedValue;
    }

    /**
     * Kết hợp hai giá trị 32-bit thành một giá trị 64-bit.
     * @param highBits 32 bit cao.
     * @param lowBits 32 bit thấp.
     * @return Giá trị long 64-bit.
     */
    public static long combineIntsToLong(int highBits, int lowBits) {
        return ((long)highBits << 32) | (lowBits & 0xFFFFFFFFL);
    }

    /**
     * Lấy 32 bit cao từ một giá trị long.
     * @param value Giá trị long 64-bit.
     * @return 32 bit cao dưới dạng int.
     */
    public static int getHigh32Bits(long value) {
        return (int)(value >> 32);
    }

    /**
     * Lấy 32 bit thấp từ một giá trị long.
     * @param value Giá trị long 64-bit.
     * @return 32 bit thấp dưới dạng int.
     */
    public static int getLow32Bits(long value) {
        return (int)value;
    }

    /**
     * Kiểm tra xem một bit cụ thể có được bật (set to 1) hay không.
     * @param number Số cần kiểm tra.
     * @param bitPosition Vị trí của bit (tính từ 0).
     * @return true nếu bit được bật, ngược lại là false.
     */
    public static boolean isBitSet(int number, int bitPosition) {
        if (bitPosition < 0 || bitPosition > 31) {
            throw new IllegalArgumentException("Bit position must be between 0 and 31.");
        }
        return ((number >> bitPosition) & 1) == 1;
    }

}