package main.java.com.mydomain.legv8simulator.utils;

public final class Constants {
    private Constants() {
        // Ngăn chặn khởi tạo lớp
    }

    public static final int DEFAULT_MEMORY_SIZE = 1024 * 1024; // 1MB bộ nhớ

    public static final int REGISTER_COUNT = 32; // Số lượng thanh ghi trong LEGv8
    public static final int XZR_REGISTER_INDEX = 31; // Chỉ số của thanh ghi XZR (Zero Register)
    public static final int LR_REGISTER_INDEX = 30; // Chỉ số của thanh ghi LR (Link Register)
    public static final int PC_REGISTER_INDEX = 32; // Chỉ số của thanh ghi PC (Program Counter)
    public static final int  SP_REGISTER_INDEX = 28; // Chỉ số của thanh ghi SP (Stack Pointer)

    public static final int PSTATE_N_BIT = 3; // Negative flag bit index
    public static final int PSTATE_Z_BIT = 2; // Zero flag bit index
    public static final int PSTATE_C_BIT = 1; // Carry flag bit index
    public static final int PSTATE_V_BIT = 0; // Overflow flag bit index

    // Sử dựng mask để thao tác với flag clearly hơn
    public static final int PSTATE_N_MASK = 1 << PSTATE_N_BIT; // 0b0000_1000
    public static final int PSTATE_Z_MASK = 1 << PSTATE_Z_BIT; // 0b0000_0100
    public static final int PSTATE_C_MASK = 1 << PSTATE_C_BIT; // 0b0000_0010
    public static final int PSTATE_V_MASK = 1 << PSTATE_V_BIT; // 0b0000_0001
    public static final int PSTATE_DEFAULT = 0; // Giá trị mặc định của PSTATE (tất cả cờ đều 0)
}
