package main.java.com.mydomain.legv8simulator.instruction;

public class CBFormatInstruction implements Instruction {
    private final int machineCode;
    private final String opcodeMnemonic; // e.g., "CBZ", "CBNZ", "B.cond"
    private final int opcode;     // 8 bits
    private final int condBrAddress; // 19 bits (signed, PC-relative offset, word-aligned)
    private final int rt;         // 5 bits (register to test for CBZ/CBNZ, or condition for B.cond)

    public CBFormatInstruction(int machineCode, String opcodeMnemonic, int opcode, int condBrAddress, int rt) {
        this.machineCode = machineCode;
        this.opcodeMnemonic = opcodeMnemonic;
        this.opcode = opcode;
        this.condBrAddress = condBrAddress;
        this.rt = rt;
    }

    @Override
    public int getMachineCode() {
        return machineCode;
    }

    @Override
    public InstructionFormat getFormat() {
        return InstructionFormat.CB_FORMAT;
    }

    @Override
    public String getOpcodeMnemonic() {
        return opcodeMnemonic;
    }

    public int getOpcode() {
        return opcode;
    }

    /**
     * Gets the 19-bit conditional branch address offset.
     * This needs to be sign-extended and multiplied by 4 for PC-relative addressing.
     */
    public int getCondBrAddress() {
        return condBrAddress;
    }

    /**
     * Gets the register to test (for CBZ/CBNZ) or the condition code (for B.cond).
     */
    public int getRtOrCond() {
        return rt;
    }

    @Override
    public String toString() {
        // Label resolution and condition decoding will be handled by assembler/disassembler
        if (opcodeMnemonic.startsWith("B.")) {
            return String.format("%s offset_0x%X (cond: %d)", opcodeMnemonic, condBrAddress, rt);
        } else {
            return String.format("%s X%d, offset_0x%X", opcodeMnemonic, rt, condBrAddress);
        }
    }
}
