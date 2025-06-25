package main.java.com.mydomain.legv8simulator.assembler;

import java.util.Arrays;

/**
 * Lớp chứa dữ liệu của một lệnh đã được phân tích cú pháp.
 * Bao gồm tên gợi nhớ (mnemonic) và một mảng các toán hạng (dưới dạng String).
 */
public class ParsedInstruction {
    private final String mnemonic;
    private final String[] operands;

    public ParsedInstruction(String mnemonic, String[] operands) {
        this.mnemonic = mnemonic.toUpperCase(); // Luôn chuẩn hóa mnemonic
        this.operands = operands;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String[] getOperands() {
        return operands;
    }

    @Override
    public String toString() {
        return "Mnemonic: " + mnemonic + ", Operands: " + Arrays.toString(operands);
    }
}