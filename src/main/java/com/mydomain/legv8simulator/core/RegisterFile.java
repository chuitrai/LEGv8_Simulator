package main.java.com.mydomain.legv8simulator.core;

import main.java.com.mydomain.legv8simulator.utils.*;

import java.util.Arrays;

/**
 * Lớp RegisterFile mô phỏng tập thanh ghi (register file) của CPU LEGv8.
 * Nó quản lý một mảng các thanh ghi mục đích chung và thực thi các quy tắc
 * đặc biệt, chẳng hạn như thanh ghi zero (XZR) luôn là 0.
 */
public class RegisterFile {
    private final long[] registers; // Mảng chứa giá trị của các thanh ghi


    /**
     * Constructor khởi tạo tập tin thanh ghi.
     * @param numRegisters Số lượng thanh ghi, thường được cung cấp từ Constants.
     */
    public RegisterFile(int numRegisters) {
        if (numRegisters <= 0) {
            throw new IllegalArgumentException("Number of registers must be positive.");
        }
        this.registers = new long[numRegisters];
        reset();
    }

    /**
     * Đặt lại (reset) tất cả các thanh ghi về giá trị 0.
     */
    public void reset() {
        Arrays.fill(registers, 0L);
    }

    /**
     * Đọc giá trị từ một thanh ghi được chỉ định.
     * Phương thức này thực thi quy tắc XZR: đọc từ thanh ghi 31 luôn trả về 0.
     *
     * @param index Chỉ số của thanh ghi cần đọc (0-31).
     * @return Giá trị 64-bit của thanh ghi.
     * @throws ArrayIndexOutOfBoundsException nếu chỉ số không hợp lệ.
     */
    public long read(int index) {
        checkIndex(index);
        // Quy tắc đặc biệt cho thanh ghi Zero (XZR)
        if (index == Constants.XZR_REGISTER_INDEX) {
            return 0L;
        }
        return registers[index];
    }

    /**
     * Ghi một giá trị vào một thanh ghi được chỉ định.
     * Phương thức này thực thi quy tắc XZR: không cho phép ghi vào thanh ghi 31.
     *
     * @param index Chỉ số của thanh ghi cần ghi (0-31).
     * @param value Giá trị 64-bit cần ghi vào.
     * @throws ArrayIndexOutOfBoundsException nếu chỉ số không hợp lệ.
     */
    public void write(int index, long value) {
        checkIndex(index);
        // Quy tắc đặc biệt cho thanh ghi Zero (XZR): việc ghi sẽ bị bỏ qua
        if (index != Constants.XZR_REGISTER_INDEX) {
            registers[index] = value;
        }
    }

    /**
     * Trả về một bản sao của mảng thanh ghi để hiển thị hoặc debug.
     * Trả về một bản sao để ngăn chặn việc thay đổi trực tiếp mảng từ bên ngoài.
     *
     * @return Một mảng long chứa giá trị của tất cả các thanh ghi.
     */
    public long[] getAllRegisters() {
        return Arrays.copyOf(registers, registers.length);
    }

    /**
     * Phương thức nội bộ để kiểm tra xem chỉ số thanh ghi có hợp lệ không.
     * @param index Chỉ số cần kiểm tra.
     */
    private void checkIndex(int index) {
        if (index < 0 || index >= registers.length) {
            throw new ArrayIndexOutOfBoundsException(
                "Invalid register index: " + index + ". Must be between 0 and " + (registers.length - 1) + "."
            );
        }
    }

    /**
     * In trạng thái của tất cả các thanh ghi ra console để debug.
     */
    public void dump() {
        System.out.println("--- Register File Dump ---");
        for (int i = 0; i < registers.length; i++) {
            System.out.printf("X%-2d: %-20d (0x%016X)\n", i, registers[i], registers[i]);
        }
        System.out.println("--------------------------");
    }
}