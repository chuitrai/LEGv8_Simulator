package main.java.com.mydomain.legv8simulator.instruction;

public class IMFormatInstruction implements Instruction {
    private final int machineCode;
    private final String opcodeMnemonic; // e.g., "MOVZ", "MOVK"
    private final int opcode;     // 9 bits
    private final int hw;         // 2 bits (determines shift: 00->0, 01->16, 10->32, 11->48)
    private final int immediate;  // 16 bits
    private final int rd;         // 5 bits (destination register)

    public IMFormatInstruction(int machineCode, String opcodeMnemonic, int opcode, int hw, int immediate, int rd) {
        this.machineCode = machineCode;
        this.opcodeMnemonic = opcodeMnemonic;
        this.opcode = opcode;
        this.hw = hw;
        this.immediate = immediate;
        this.rd = rd;
    }

    @Override
    public int getMachineCode() {
        return machineCode;
    }

    @Override
    public InstructionFormat getFormat() {
        return InstructionFormat.IM_FORMAT;
    }

    @Override
    public String getOpcodeMnemonic() {
        return opcodeMnemonic;
    }

    public int getOpcode() {
        return opcode;
    }

    /**
     * Gets the hw bits (00, 01, 10, 11) which determine the shift amount.
     */
    public int getHw() {
        return hw;
    }

    /**
     * Gets the 16-bit immediate value.
     */
    public int getImmediate() {
        return immediate;
    }

    public int getRd() {
        return rd;
    }

    public int getShiftAmount() {
        return hw * 16; // 00 -> 0, 01 -> 16, 10 -> 32, 11 -> 48
    }

    @Override
    public String toString() {
        return String.format("%s X%d, #%d, LSL #%d", opcodeMnemonic, rd, immediate, getShiftAmount());
    }
}
