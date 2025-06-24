package main.java.com.mydomain.legv8simulator.instruction;

public class BFormatInstruction implements Instruction {
    private final int machineCode;
    private final String opcodeMnemonic; // e.g., "B", "BL"
    private final int opcode;    // 6 bits
    private final int brAddress; // 26 bits (signed, PC-relative offset, word-aligned)

    public BFormatInstruction(int machineCode, String opcodeMnemonic, int opcode, int brAddress) {
        this.machineCode = machineCode;
        this.opcodeMnemonic = opcodeMnemonic;
        this.opcode = opcode;
        this.brAddress = brAddress;
    }

    @Override
    public int getMachineCode() {
        return machineCode;
    }

    @Override
    public InstructionFormat getFormat() {
        return InstructionFormat.B_FORMAT;
    }

    @Override
    public String getOpcodeMnemonic() {
        return opcodeMnemonic;
    }

    public int getOpcode() {
        return opcode;
    }

    /**
     * Gets the 26-bit branch address offset.
     * This needs to be sign-extended and multiplied by 4 for PC-relative addressing.
     */
    public int getBrAddress() {
        return brAddress;
    }

    @Override
    public String toString() {
        // Label resolution will be handled by assembler/disassembler
        return String.format("%s offset_0x%X", opcodeMnemonic, brAddress);
    }
}
