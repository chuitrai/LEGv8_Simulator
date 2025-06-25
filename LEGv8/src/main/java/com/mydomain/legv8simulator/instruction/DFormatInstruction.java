package main.java.com.mydomain.legv8simulator.instruction;

public class DFormatInstruction implements Instruction {
    private final int machineCode;
    private final String opcodeMnemonic; // e.g., "LDUR", "STUR", "LDURSW", "STURW"
    private final int opcode;    // 11 bits
    private final int address;   // 9 bits (DT_address - signed offset, unscaled)
    private final int op2;       // 2 bits (typically 00 for LDUR/STUR)
    private final int rn;        // 5 bits (base address register)
    private final int rt;        // 5 bits (source/destination register)

    public DFormatInstruction(int machineCode, String opcodeMnemonic, int opcode, int address, int op2, int rn, int rt) {
        this.machineCode = machineCode;
        this.opcodeMnemonic = opcodeMnemonic;
        this.opcode = opcode;
        this.address = address; // This is the DT_address field
        this.op2 = op2;
        this.rn = rn;
        this.rt = rt;
    }

    @Override
    public int getMachineCode() {
        return machineCode;
    }

    @Override
    public InstructionFormat getFormat() {
        return InstructionFormat.D_FORMAT;
    }

     @Override
    public String getOpcodeMnemonic() {
        return opcodeMnemonic;
    }

    public int getOpcode() {
        return opcode;
    }

    /**
     * Gets the 9-bit immediate offset (DT_address).
     * This value is typically sign-extended for address calculation.
     */
    public int getDtAddress() {
        return address;
    }

    public int getOp2() {
        return op2;
    }

    public int getRn() {
        return rn;
    }

    public int getRt() {
        return rt;
    }

    @Override
    public String toString() {
        return String.format("%s X%d, [X%d, #%d]", opcodeMnemonic, rt, rn, address);
    }
}