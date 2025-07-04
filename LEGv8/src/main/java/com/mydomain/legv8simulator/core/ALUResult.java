package main.java.com.mydomain.legv8simulator.core;

/**
 * Lớp đóng gói kết quả trả về từ một phép toán của ALU.
 * Nó chứa cả giá trị kết quả và các cờ trạng thái tương ứng.
 */
public class ALUResult {
    public final long result;
    public final boolean flagN;
    public final boolean flagZ;
    public final boolean flagC;
    public final boolean flagV;

    public ALUResult(long result, boolean flagN, boolean flagZ, boolean flagC, boolean flagV) {
        this.result = result;
        this.flagN = flagN;
        this.flagZ = flagZ;
        this.flagC = flagC;
        this.flagV = flagV;
    }

    // Getters
    public long getResult() { return result; }
    public boolean isFlagN() { return flagN; }
    public boolean isFlagZ() { return flagZ; }
    public boolean isFlagC() { return flagC; }
    public boolean isFlagV() { return flagV; }
}