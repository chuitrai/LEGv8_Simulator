package main.java.com.mydomain.legv8simulator.instruction;

public class IFormatInstruction implements Instruction {
    private final int machineCode;
    private final String opcodeMnemonic; // e.g., "ADDI", "SUBI", "ANDI", "ORRI", "EORI"
    private final int opcode;       // 10 bits
    private final int immediate;    // 12 bits (signed)
    private final int rn;           // 5 bits (source register)
    private final int rd;           // 5 bits (destination register)

    public IFormatInstruction(int machineCode, String opcodeMnemonic, int opcode, int immediate, int rn, int rd) {
        this.machineCode = machineCode;
        this.opcodeMnemonic = opcodeMnemonic;
        this.opcode = opcode;
        this.immediate = immediate;
        this.rn = rn;
        this.rd = rd;
    }

    @Override
    public int getMachineCode() {
        return machineCode;
    }

    @Override
    public InstructionFormat getFormat() {
        return InstructionFormat.I_FORMAT;
    }

    @Override
    public String getOpcodeMnemonic() {
        return opcodeMnemonic;
    }

    public int getOpcode() {
        return opcode;
    }

    public int getImmediate() {
        return immediate;
    }

    public int getRn() {
        return rn;
    }

    public int getRd() {
        return rd;
    }

    @Override
    public String toString() {
        return String.format("%s X%d, X%d, #%d", opcodeMnemonic, rd, rn, immediate);
    }
}