// src/main/java/com/yourdomain/legv8simulator/instruction/InstructionDecoder.java
package main.java.com.mydomain.legv8simulator.instruction;

import main.java.com.mydomain.legv8simulator.utils.BitUtils;

import java.util.HashMap;
import java.util.Map;

public class InstructionDecoder {

    // Inner class to store Opcode details from the table (page 86, Bài 05)
    private static class OpcodeInfo {
        final String mnemonic;
        final InstructionFormat format;
        final int opcodeValue; // The actual opcode bits used for matching
        final int opcodeSize;  // Number of bits in this specific opcode

        public OpcodeInfo(String mnemonic, InstructionFormat format, int opcodeValue, int opcodeSize) {
            this.mnemonic = mnemonic;
            this.format = format;
            this.opcodeValue = opcodeValue;
            this.opcodeSize = opcodeSize;
        }
    }

    // Lookup table for opcodes.
    // Key: Opcode value (masked and shifted). Value: OpcodeInfo object.
    // This table needs to be populated based on the "Giải mã lệnh LEGv8" slide.
    // The key generation will depend on how you decide to differentiate opcodes of different lengths.
    // A common approach is to use the longest possible opcode match or use ranges.
    // For simplicity here, we'll assume a way to get a unique identifier for each opcode entry.
    private static final Map<Integer, OpcodeInfo> OPCODE_TABLE = new HashMap<>();

    static {
        // Populate the OPCODE_TABLE based on "Giải mã lệnh LEGv8" (page 86, Bài 05)
        // This is a crucial and detailed step.
        // Note: The 'opcodeValue' here is the specific bit pattern for that opcode
        // and 'opcodeSize' is its length. The key for the map might need
        // to be a combination or a specific extraction that uniquely identifies it.

        // --- B-FORMAT ---
        // B: 000101 (6 bits), range 160-191 (if opcode is bits 31-26)
        addOpcode("B", InstructionFormat.B_FORMAT, 0b000101, 6, 26, 31);
        // BL: 100101 (6 bits), range 1184-1215
        addOpcode("BL", InstructionFormat.B_FORMAT, 0b100101, 6, 26, 31);

        // --- CB-FORMAT ---
        // CBZ: 10110100 (8 bits), range 1440-1447
        addOpcode("CBZ", InstructionFormat.CB_FORMAT, 0b10110100, 8, 24, 31);
        // CBNZ: 10110101 (8 bits), range 1448-1455
        addOpcode("CBNZ", InstructionFormat.CB_FORMAT, 0b10110101, 8, 24, 31);
        // B.cond: 01010100 (8 bits for cond), range 672-679 (This is simplified for B.cond, real ARMv8 is different)
        // For B.cond, the 'cond' field is in Rt (bits 0-4) of CB format.
        // The opcode here represents the B.cond family.
        addOpcode("B.cond", InstructionFormat.CB_FORMAT, 0b01010100, 8, 24, 31);


        // --- I-FORMAT --- (Opcode is 10 bits)
        // ADDI: 1001000100 (10 bits), range 1160-1161
        addOpcode("ADDI", InstructionFormat.I_FORMAT, 0b1001000100, 10, 22, 31);
        // ADDIS: 1011000100 (10 bits), range 1416-1417
        addOpcode("ADDIS", InstructionFormat.I_FORMAT, 0b1011000100, 10, 22, 31);
        // SUBI: 1101000100 (10 bits), range 1672-1673
        addOpcode("SUBI", InstructionFormat.I_FORMAT, 0b1101000100, 10, 22, 31);
        // SUBIS: 1111000100 (10 bits), range 1928-1929
        addOpcode("SUBIS", InstructionFormat.I_FORMAT, 0b1111000100, 10, 22, 31);
        // ANDI: 1001001000 (10 bits)
        addOpcode("ANDI", InstructionFormat.I_FORMAT, 0b1001001000, 10, 22, 31);
        // ORRI: 1011001000 (10 bits)
        addOpcode("ORRI", InstructionFormat.I_FORMAT, 0b1011001000, 10, 22, 31);
        // EORI: 1101001000 (10 bits)
        addOpcode("EORI", InstructionFormat.I_FORMAT, 0b1101001000, 10, 22, 31);


        // --- D-FORMAT --- (Opcode is 11 bits)
        // LDUR: 11111000010 (11 bits), range 1986
        addOpcode("LDUR", InstructionFormat.D_FORMAT, 0b11111000010, 11, 21, 31);
        // STUR: 11111000000 (11 bits), range 1984
        addOpcode("STUR", InstructionFormat.D_FORMAT, 0b11111000000, 11, 21, 31);
        // LDURSW: 10111000100 (11 bits)
        addOpcode("LDURSW", InstructionFormat.D_FORMAT, 0b10111000100, 11, 21, 31);
        // STURW: 10111000000 (11 bits)
        addOpcode("STURW", InstructionFormat.D_FORMAT, 0b10111000000, 11, 21, 31);
        // LDURH: 01111000010 (11 bits)
        addOpcode("LDURH", InstructionFormat.D_FORMAT, 0b01111000010, 11, 21, 31);
        // STURH: 01111000000 (11 bits)
        addOpcode("STURH", InstructionFormat.D_FORMAT, 0b01111000000, 11, 21, 31);
        // LDURB: 00111000010 (11 bits)
        addOpcode("LDURB", InstructionFormat.D_FORMAT, 0b00111000010, 11, 21, 31);
        // STURB: 00111000000 (11 bits)
        addOpcode("STURB", InstructionFormat.D_FORMAT, 0b00111000000, 11, 21, 31);
        // LDXR: 11001000010 (Load Exclusive - Assuming D for simplicity, real ARMv8 might be different)
        addOpcode("LDXR", InstructionFormat.D_FORMAT, 0b11001000010, 11, 21, 31);
        // STXR: 11001000000 (Store Exclusive)
        addOpcode("STXR", InstructionFormat.D_FORMAT, 0b11001000000, 11, 21, 31);


        // --- R-FORMAT --- (Opcode is 11 bits)
        // ADD: 10001011000 (11 bits), range 1112
        addOpcode("ADD", InstructionFormat.R_FORMAT, 0b10001011000, 11, 21, 31);
        // ADDS: 10101011000
        addOpcode("ADDS", InstructionFormat.R_FORMAT, 0b10101011000, 11, 21, 31);
        // SUB: 11001011000
        addOpcode("SUB", InstructionFormat.R_FORMAT, 0b11001011000, 11, 21, 31);
        // SUBS: 11101011000
        addOpcode("SUBS", InstructionFormat.R_FORMAT, 0b11101011000, 11, 21, 31);
        // AND: 10001010000
        addOpcode("AND", InstructionFormat.R_FORMAT, 0b10001010000, 11, 21, 31);
        // ANDS: 11101010000
        addOpcode("ANDS", InstructionFormat.R_FORMAT, 0b11101010000, 11, 21, 31);
        // ORR: 10101010000
        addOpcode("ORR", InstructionFormat.R_FORMAT, 0b10101010000, 11, 21, 31);
        // EOR: 11001010000
        addOpcode("EOR", InstructionFormat.R_FORMAT, 0b11001010000, 11, 21, 31);
        // LSL: 11010011011 (shamt is in Rm, instruction[21] is 0)
        addOpcode("LSL", InstructionFormat.R_FORMAT, 0b11010011011, 11, 21, 31); // Actually checks bits 21-31, shamt is used.
        // LSR: 11010011010
        addOpcode("LSR", InstructionFormat.R_FORMAT, 0b11010011010, 11, 21, 31);
        // ASR: 11010011000 (Not on slide, added for completeness)
        // BR: 11010110000
        addOpcode("BR", InstructionFormat.R_FORMAT, 0b11010110000, 11, 21, 31);

        // --- IM-FORMAT --- (Opcode is 9 bits, specific range)
        // MOVZ: 110100101 (9 bits), range 1684-1687 (bits 31-23 for opcode)
        addOpcode("MOVZ", InstructionFormat.IM_FORMAT, 0b110100101, 9, 23, 31);
        // MOVK: 111100101 (9 bits), range 1940-1943
        addOpcode("MOVK", InstructionFormat.IM_FORMAT, 0b111100101, 9, 23, 31);
    }

    /**
     * Helper method to populate the opcode table.
     * The key in OPCODE_TABLE is the extracted opcode value itself.
     * This assumes that opcodes of different lengths won't collide after extraction.
     * For a more robust system, you might need a more complex key or multiple tables.
     */
    private static void addOpcode(String mnemonic, InstructionFormat format, int opcodePattern, int opcodeSize, int startBit, int endBit) {
        // The key could be the pattern itself if sizes are handled carefully, or a range.
        // For this example, we'll use the pattern directly.
        // This simplistic approach assumes opcodes of different lengths are distinct enough
        // or handled by the order of checks in the decode method.
        OPCODE_TABLE.put(opcodePattern, new OpcodeInfo(mnemonic, format, opcodePattern, opcodeSize));
    }


    public Instruction decode(int machineCode) {
        // Try to match opcodes starting from the most specific (longest) to most general (shortest)
        // or by fixed bit positions if the LEGv8 table (p86) implies fixed positions for opcodes of certain lengths.

        OpcodeInfo info;

        // Check for 9-bit IM-Format opcodes (MOVZ, MOVK)
        int op9 = BitUtils.extractBits(machineCode, 23, 31); // bits 31-23
        info = OPCODE_TABLE.get(op9);
        if (info != null && info.format == InstructionFormat.IM_FORMAT) {
            return decodeIMFormat(machineCode, info);
        }

        // Check for 11-bit R-Format and D-Format opcodes
        int op11 = BitUtils.extractBits(machineCode, 21, 31); // bits 31-21
        info = OPCODE_TABLE.get(op11);
        if (info != null) {
            if (info.format == InstructionFormat.R_FORMAT) {
                return decodeRFormat(machineCode, info);
            } else if (info.format == InstructionFormat.D_FORMAT) {
                return decodeDFormat(machineCode, info);
            }
        }

        // Check for 10-bit I-Format opcodes
        int op10 = BitUtils.extractBits(machineCode, 22, 31); // bits 31-22
        info = OPCODE_TABLE.get(op10);
        if (info != null && info.format == InstructionFormat.I_FORMAT) {
            return decodeIFormat(machineCode, info);
        }

        // Check for 8-bit CB-Format opcodes
        int op8 = BitUtils.extractBits(machineCode, 24, 31); // bits 31-24
        info = OPCODE_TABLE.get(op8);
        if (info != null && info.format == InstructionFormat.CB_FORMAT) {
            return decodeCBFormat(machineCode, info);
        }

        // Check for 6-bit B-Format opcodes
        int op6 = BitUtils.extractBits(machineCode, 26, 31); // bits 31-26
        info = OPCODE_TABLE.get(op6);
        if (info != null && info.format == InstructionFormat.B_FORMAT) {
            return decodeBFormat(machineCode, info);
        }

        // If no match found
        System.err.println("Unknown instruction: " + Integer.toBinaryString(machineCode) + " (Op11: " + Integer.toBinaryString(op11) + ")");
        // You should return a specific "UnknownInstruction" object or throw an exception
        return new UnknownInstruction(machineCode);
    }

    private RFormatInstruction decodeRFormat(int machineCode, OpcodeInfo info) {
        int rm = BitUtils.extractBits(machineCode, 16, 20);
        int shamt = BitUtils.extractBits(machineCode, 10, 15);
        int rn = BitUtils.extractBits(machineCode, 5, 9);
        int rd = BitUtils.extractBits(machineCode, 0, 4);
        System.out.println("Decoded R-format: " + info.mnemonic + " rm=" + rm + ", shamt=" + shamt + ", rn=" + rn + ", rd=" + rd);
        return new RFormatInstruction(machineCode, info.mnemonic, info.opcodeValue, rd, rn, rm, shamt);
    }

    private IFormatInstruction decodeIFormat(int machineCode, OpcodeInfo info) {
        int immediate = BitUtils.extractBits(machineCode, 10, 21); // 12 bits
        int rd = BitUtils.extractBits(machineCode, 5, 9);
        int rn = BitUtils.extractBits(machineCode, 0, 4);
        // I-format immediate is signed
        return new IFormatInstruction(machineCode, info.mnemonic, info.opcodeValue, BitUtils.signExtend32(immediate, 12), rn, rd);
    }

    private DFormatInstruction decodeDFormat(int machineCode, OpcodeInfo info) {
        int dtAddress = BitUtils.extractBits(machineCode, 12, 20); // 9 bits, DT_address
        int op2 = BitUtils.extractBits(machineCode, 10, 11);
        int rn = BitUtils.extractBits(machineCode, 5, 9);
        int rt = BitUtils.extractBits(machineCode, 0, 4);
        // D-format address (offset) is signed
        return new DFormatInstruction(machineCode, info.mnemonic, info.opcodeValue, BitUtils.signExtend32(dtAddress, 9), op2, rn, rt);
    }

    private BFormatInstruction decodeBFormat(int machineCode, OpcodeInfo info) {
        int brAddress = BitUtils.extractBits(machineCode, 0, 25); // 26 bits
        // B-format address is signed and word-aligned (implicitly *4)
        return new BFormatInstruction(machineCode, info.mnemonic, info.opcodeValue, BitUtils.signExtend32(brAddress, 26));
    }

    private CBFormatInstruction decodeCBFormat(int machineCode, OpcodeInfo info) {
        int condBrAddress = BitUtils.extractBits(machineCode, 5, 23); // 19 bits
        int rtOrCond = BitUtils.extractBits(machineCode, 0, 4); // Register for CBZ/CBNZ, Cond for B.cond
        // CB-format address is signed and word-aligned (implicitly *4)
        String mnemonic = info.mnemonic;
        if (mnemonic.equals("B.cond")) { // Adjust mnemonic for specific B.cond
            mnemonic = getBranchConditionMnemonic(rtOrCond);
        }
        return new CBFormatInstruction(machineCode, mnemonic, info.opcodeValue, BitUtils.signExtend32(condBrAddress, 19), rtOrCond);
    }

    private IMFormatInstruction decodeIMFormat(int machineCode, OpcodeInfo info) {
        int hw = BitUtils.extractBits(machineCode, 21, 22); // 2 bits
        int immediate = BitUtils.extractBits(machineCode, 5, 20); // 16 bits
        int rd = BitUtils.extractBits(machineCode, 0, 4);
        // IM-format immediate is unsigned
        return new IMFormatInstruction(machineCode, info.mnemonic, info.opcodeValue, hw, immediate, rd);
    }

    // Helper to get B.cond mnemonic (simplified)
    private String getBranchConditionMnemonic(int condField) {
        switch (condField) {
            case 0b0000: return "B.EQ"; // Z=1
            case 0b0001: return "B.NE"; // Z=0
            case 0b0010: return "B.HS"; // C=1 (CS)
            case 0b0011: return "B.LO"; // C=0 (CC)
            case 0b0100: return "B.MI"; // N=1
            case 0b0101: return "B.PL"; // N=0
            case 0b0110: return "B.VS"; // V=1
            case 0b0111: return "B.VC"; // V=0
            case 0b1000: return "B.HI"; // C=1 && Z=0
            case 0b1001: return "B.LS"; // C=0 || Z=1
            case 0b1010: return "B.GE"; // N == V
            case 0b1011: return "B.LT"; // N != V
            case 0b1100: return "B.GT"; // Z=0 && N == V
            case 0b1101: return "B.LE"; // Z=1 || N != V
            // case 0b1110: return "B.AL"; (Always - though B instruction is preferred)
            // case 0b1111: return "B.NV"; (Never - rarely used)
            default: return "B.cond_UNKNOWN";
        }
    }

    // Placeholder for unrecognized instructions
    public static class UnknownInstruction implements Instruction {
        private final int machineCode;
        public UnknownInstruction(int machineCode) { this.machineCode = machineCode; }
        @Override public int getMachineCode() { return machineCode; }
        @Override public InstructionFormat getFormat() { return InstructionFormat.UNKNOWN; }
        @Override public String getOpcodeMnemonic() { return "UNKNOWN"; }
        @Override public String toString() { return "UNKNOWN Instruction: " + Integer.toHexString(machineCode); }
    }
}