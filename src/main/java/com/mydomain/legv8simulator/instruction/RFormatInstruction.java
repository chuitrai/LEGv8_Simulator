package main.java.com.mydomain.legv8simulator.instruction;

public class RFormatInstruction implements Instruction {
    private final int machineCode; // 32 bits
    private final String opCodeMnemonic; // "ADD", "SUB", "AND" etc. 
    private final int opCode; // 11 bits    private final int rd; // des register 5 bits
    private final int rd;
    private final int rn; // src register 1 5 bits
    private final int rm; // src register 2 5 bits
    private final int shamt; // shift amount 6 bits XZR
    
    public RFormatInstruction(int machineCode, String opCodeMnemonic, int opCode, int rd, int rn, int rm, int shamt) {
        this.machineCode = machineCode;
        this.opCodeMnemonic = opCodeMnemonic;
        this.opCode = opCode;
        this.rd = rd;
        this.rn = rn;
        this.rm = rm;
        this.shamt = shamt;
    }

    @Override
    public int getMachineCode() {
        return machineCode;
    }
    @Override
    public InstructionFormat getFormat() {
        return InstructionFormat.R_FORMAT;
    }
    @Override
    
    public String getOpcodeMnemonic() {
        return opCodeMnemonic;
    }
    public int getOpCode() {
        return opCode;
    }
    public int getRd() {
        return rd;
    }
    public int getRn() {
        return rn;
    }
    public int getRm() {
        return rm;
    }
    public int getShamt() {
        return shamt;
    }

    @Override
    public String toString() {
        return String.format("%s X%d, X%d, %s%s",
                opCodeMnemonic, rd, rn,
                (opCodeMnemonic.equals("LSL") || opCodeMnemonic.equals("LSR") || 
                opCodeMnemonic.equals("ASR")) ? "#" + shamt : "X" + rm,
                (opCodeMnemonic.equals("BR")) ? "" : "" // Adjust for BR if needed
        );
    }
}
