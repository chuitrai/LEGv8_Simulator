package main.java.com.mydomain.legv8simulator.core;

import main.java.com.mydomain.legv8simulator.utils.Constants;

/**
 * Lớp CPU chính, đóng vai trò một "container" và bộ điều phối cho các thành phần
 * phần cứng cốt lõi như RegisterFile, PC, và FlagsRegister.
 * Nó chỉ chứa trạng thái, không chứa logic thực thi lệnh.
 */
public class CPU {

    // --- CÁC THÀNH PHẦN PHẦN CỨNG CON CỦA CPU ---
    private final RegisterFile registerFile;
    private final Register programCounter;
    private final FlagsRegister flagsRegister;

    /**
     * Lớp nội để quản lý các cờ trạng thái (PSTATE).
     * Được đóng gói bên trong CPU để đảm bảo tính toàn vẹn.
     */
    public static class FlagsRegister {
        private boolean N, Z, C, V; // Negative, Zero, Carry, Overflow

        public void reset() {
            N = false;
            Z = true; // Theo quy ước, Z=1 khi reset
            C = false;
            V = false;
        }
        
        // --- Getters for flags ---
        public boolean isN() { return N; }
        public boolean isZ() { return Z; }
        public boolean isC() { return C; }
        public boolean isV() { return V; }
        
        // --- Các phương thức cập nhật cờ ---
        // Sẽ được gọi bởi InstructionExecutor

        public void updateNZ(long result) {
            this.N = (result < 0);
            this.Z = (result == 0);
        }

        public void updateCVForAdd(long operand1, long operand2, long result) {
            // Carry for addition (unsigned overflow)
            this.C = (Long.compareUnsigned(result, operand1) < 0);
            // Overflow for addition (signed overflow)
            this.V = ((operand1 > 0 && operand2 > 0 && result < 0) ||
                      (operand1 < 0 && operand2 < 0 && result >= 0));
        }

        public void updateCVForSub(long operand1, long operand2, long result) {
            // Carry for subtraction (no borrow)
            this.C = (Long.compareUnsigned(operand1, operand2) >= 0);
            // Overflow for subtraction (signed overflow)
            this.V = ((operand1 > 0 && operand2 < 0 && result < 0) ||
                      (operand1 < 0 && operand2 > 0 && result >= 0));
        }
        
        @Override
        public String toString() {
            return String.format("N=%d Z=%d C=%d V=%d", N?1:0, Z?1:0, C?1:0, V?1:0);
        }
    }

    // --- CONSTRUCTOR CỦA CPU ---
    public CPU() {
        this.registerFile = new RegisterFile(Constants.REGISTER_COUNT);
        this.programCounter = new Register("PC", RegisterType.PROGRAM_COUNTER);
        this.flagsRegister = new FlagsRegister();
        reset();
    }

    /**
     * Đặt lại (reset) toàn bộ CPU về trạng thái ban đầu.
     * Ủy quyền việc reset cho các thành phần con.
     */
    public void reset() {
        this.registerFile.reset();
        this.programCounter.reset();
        this.flagsRegister.reset();
    }

    // --- CUNG CẤP CÁC PHƯƠNG THỨC TRUY CẬP (GETTERS) ĐẾN CÁC THÀNH PHẦN CON ---
    // Các lớp khác (Simulator, Executor) sẽ dùng các getter này để tương tác với CPU.
    
    public RegisterFile getRegisterFile() {
        return registerFile;
    }

    public Register getPC() {
        return programCounter;
    }

    public FlagsRegister getFlagsRegister() {
        return flagsRegister;
    }
    
    /**
     * In trạng thái hiện tại của CPU ra console.
     * Hữu ích cho việc gỡ lỗi.
     */
    public void printState() {
        System.out.println("============== CPU State ==============");
        long[] regs = registerFile.getAllRegisters();
        for (int i = 0; i < Constants.REGISTER_COUNT; i++) {
             // Lấy giá trị trực tiếp từ mảng để in
            System.out.printf("X%-2d: %-20d (0x%016X)\n", i, regs[i], regs[i]);
        }
        System.out.println(programCounter.toString());
        System.out.println("Flags: " + flagsRegister.toString());
        System.out.println("=======================================");
    }
}