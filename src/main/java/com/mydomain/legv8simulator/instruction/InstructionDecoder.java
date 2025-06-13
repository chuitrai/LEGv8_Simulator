package main.java.com.mydomain.legv8simulator.instruction;

import main.java.com.mydomain.legv8simulator.utils.BitUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * Lớp InstructionDecoder hoạt động như một Factory, chịu trách nhiệm giải mã mã máy
 * thành các đối tượng Instruction. Sử dụng nhiều HashMap để tra cứu hiệu quả
 * các opcode có độ dài khác nhau.
 *
 * Cách tiếp cận: Định nghĩa tất cả các opcode hợp lệ một cách tường minh trong các
 * bảng tra cứu. Phương thức decode() sẽ thử tra cứu tuần tự từ opcode dài nhất
 * đến ngắn nhất để đảm bảo nhận dạng chính xác.
 */
public class InstructionDecoder {

    // Lớp nội bộ để lưu trữ thông tin về một opcode
    private static class OpcodeInfo {
        final String mnemonic; // Tên gợi nhớ ĐẦY ĐỦ, ví dụ: "ADDI", "ADDS"
        final InstructionFormat format;

        OpcodeInfo(String mnemonic, InstructionFormat format) {
            this.mnemonic = mnemonic;
            this.format = format;
        }
    }

    // Các bảng tra cứu riêng cho từng độ dài opcode
    private static final Map<Integer, OpcodeInfo> TABLE_OPCODE_6_BIT = new HashMap<>();
    private static final Map<Integer, OpcodeInfo> TABLE_OPCODE_8_BIT = new HashMap<>();
    private static final Map<Integer, OpcodeInfo> TABLE_OPCODE_9_BIT = new HashMap<>();
    private static final Map<Integer, OpcodeInfo> TABLE_OPCODE_10_BIT = new HashMap<>();
    private static final Map<Integer, OpcodeInfo> TABLE_OPCODE_11_BIT = new HashMap<>();

    // Khối static để điền dữ liệu vào các bảng tra cứu một lần duy nhất
    static {
        // --- B-FORMAT (6-bit opcode, bits 31-26) ---
        TABLE_OPCODE_6_BIT.put(0b000101, new OpcodeInfo("B", InstructionFormat.B_FORMAT));
        TABLE_OPCODE_6_BIT.put(0b100101, new OpcodeInfo("BL", InstructionFormat.B_FORMAT));

        // --- CB-FORMAT (8-bit opcode, bits 31-24) ---
        TABLE_OPCODE_8_BIT.put(0b10110100, new OpcodeInfo("CBZ", InstructionFormat.CB_FORMAT));
        TABLE_OPCODE_8_BIT.put(0b10110101, new OpcodeInfo("CBNZ", InstructionFormat.CB_FORMAT));
        TABLE_OPCODE_8_BIT.put(0b01010100, new OpcodeInfo("B.cond", InstructionFormat.CB_FORMAT));

        // --- IM-FORMAT (9-bit opcode, bits 31-23) ---
        TABLE_OPCODE_9_BIT.put(0b110100101, new OpcodeInfo("MOVZ", InstructionFormat.IM_FORMAT));
        TABLE_OPCODE_9_BIT.put(0b111100101, new OpcodeInfo("MOVK", InstructionFormat.IM_FORMAT));

        // --- I-FORMAT (10-bit opcode, bits 31-22) ---
        TABLE_OPCODE_10_BIT.put(0b1001000100, new OpcodeInfo("ADDI", InstructionFormat.I_FORMAT));
        TABLE_OPCODE_10_BIT.put(0b1101000100, new OpcodeInfo("SUBI", InstructionFormat.I_FORMAT));
        TABLE_OPCODE_10_BIT.put(0b1001001000, new OpcodeInfo("ANDI", InstructionFormat.I_FORMAT));
        TABLE_OPCODE_10_BIT.put(0b1111001000, new OpcodeInfo("ORRI", InstructionFormat.I_FORMAT));
        TABLE_OPCODE_10_BIT.put(0b1101001000, new OpcodeInfo("EORI", InstructionFormat.I_FORMAT));

        // --- R-FORMAT (11-bit opcode, bits 31-21) ---
        TABLE_OPCODE_11_BIT.put(0b11010001001, new OpcodeInfo("SUBIS", InstructionFormat.I_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b10010001001, new OpcodeInfo("ADDIS", InstructionFormat.I_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b10001011000, new OpcodeInfo("ADD", InstructionFormat.R_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b10001011001, new OpcodeInfo("ADDS", InstructionFormat.R_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b11001011000, new OpcodeInfo("SUB", InstructionFormat.R_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b11001011001, new OpcodeInfo("SUBS", InstructionFormat.R_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b10001010000, new OpcodeInfo("AND", InstructionFormat.R_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b10001010001, new OpcodeInfo("ANDS", InstructionFormat.R_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b10101010000, new OpcodeInfo("ORR", InstructionFormat.R_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b10111010000, new OpcodeInfo("ORRS", InstructionFormat.R_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b11001010000, new OpcodeInfo("EOR", InstructionFormat.R_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b10011011000, new OpcodeInfo("MUL", InstructionFormat.R_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b11010011011, new OpcodeInfo("LSL", InstructionFormat.R_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b11010011010, new OpcodeInfo("LSR", InstructionFormat.R_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b11010011100, new OpcodeInfo("ASR", InstructionFormat.R_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b11010110010, new OpcodeInfo("BR", InstructionFormat.R_FORMAT));

        // D-FORMAT
        TABLE_OPCODE_11_BIT.put(0b11111000010, new OpcodeInfo("LDUR", InstructionFormat.D_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b11111000000, new OpcodeInfo("STUR", InstructionFormat.D_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b10111000100, new OpcodeInfo("LDURSW", InstructionFormat.D_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b10111000000, new OpcodeInfo("STURW", InstructionFormat.D_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b01111000010, new OpcodeInfo("LDURH", InstructionFormat.D_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b01111000000, new OpcodeInfo("STURH", InstructionFormat.D_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b00111000010, new OpcodeInfo("LDURB", InstructionFormat.D_FORMAT));
        TABLE_OPCODE_11_BIT.put(0b00111000000, new OpcodeInfo("STURB", InstructionFormat.D_FORMAT));
        // TABLE_OPCODE_11_BIT.put(0b11001000000, new OpcodeInfo("STXR", InstructionFormat.D_FORMAT));
        // TABLE_OPCODE_11_BIT.put(0b10001000010, new OpcodeInfo("LDXR", InstructionFormat.D_FORMAT));
    }

    public Instruction decode(int machineCode) {
        OpcodeInfo info;

        System.out.println("--->" + BitUtils.toBinaryString32(machineCode));

        // Ưu tiên 1: Opcode 11-bit (R, D)

        int op11 = BitUtils.extractBits(machineCode, 21, 31);
        info = TABLE_OPCODE_11_BIT.get(op11);
        if (info != null) {
            if (info.format == InstructionFormat.R_FORMAT) return decodeRFormat(machineCode, info);
            if (info.format == InstructionFormat.I_FORMAT) return decodeIFormat(machineCode, info);
            if (info.format == InstructionFormat.D_FORMAT) return decodeDFormat(machineCode, info);
        }

        // Ưu tiên 2: Opcode 10-bit (I)
        int op10 = BitUtils.extractBits(machineCode, 22, 31);
        info = TABLE_OPCODE_10_BIT.get(op10);
        if (info != null) {
            return decodeIFormat(machineCode, info);
        }

        // Ưu tiên 3: Opcode 9-bit (IM)
        int op9 = BitUtils.extractBits(machineCode, 23, 31);
        info = TABLE_OPCODE_9_BIT.get(op9);
        if (info != null) {
            return decodeIMFormat(machineCode, info);
        }

        // Ưu tiên 4: Opcode 8-bit (CB)
        int op8 = BitUtils.extractBits(machineCode, 24, 31);
        info = TABLE_OPCODE_8_BIT.get(op8);
        if (info != null) {
            return decodeCBFormat(machineCode, info);
        }

        // Ưu tiên 5: Opcode 6-bit (B)
        int op6 = BitUtils.extractBits(machineCode, 26, 31);
        info = TABLE_OPCODE_6_BIT.get(op6);
        if (info != null) {
            return decodeBFormat(machineCode, info);
        }

        // Nếu không tìm thấy, trả về lệnh không xác định
        return new UnknownInstruction(machineCode);
    }
    
    // =========================================================================
    // Các hàm giải mã cho từng định dạng
    // =========================================================================
    
    private RFormatInstruction decodeRFormat(int mc, OpcodeInfo info) {
        int opcode = BitUtils.extractBits(mc, 21, 31);
        int rm = BitUtils.extractBits(mc, 16, 20);
        int shamt = BitUtils.extractBits(mc, 10, 15);
        int rn = BitUtils.extractBits(mc, 5, 9);
        int rd = BitUtils.extractBits(mc, 0, 4);
        return new RFormatInstruction(mc, info.mnemonic, opcode, rd, rn, rm, shamt);
    }

    private IFormatInstruction decodeIFormat(int mc, OpcodeInfo info) {
        int opcode = BitUtils.extractBits(mc, 22, 31);
        int immediate = BitUtils.extractBits(mc, 10, 20);
        int rn = BitUtils.extractBits(mc, 5, 9);
        int rd = BitUtils.extractBits(mc, 0, 4);
        return new IFormatInstruction(mc, info.mnemonic, opcode, BitUtils.signExtend32(immediate, 12), rn, rd);
    }
    
    private DFormatInstruction decodeDFormat(int mc, OpcodeInfo info) {
        int opcode = BitUtils.extractBits(mc, 21, 31);
        int dtAddress = BitUtils.extractBits(mc, 12, 20);
        int op2 = BitUtils.extractBits(mc, 10, 11);
        int rn = BitUtils.extractBits(mc, 5, 9);
        int rt = BitUtils.extractBits(mc, 0, 4);
        return new DFormatInstruction(mc, info.mnemonic, opcode, BitUtils.signExtend32(dtAddress, 9), op2, rn, rt);
    }
    
    private BFormatInstruction decodeBFormat(int mc, OpcodeInfo info) {
        int opcode = BitUtils.extractBits(mc, 26, 31);
        int brAddress = BitUtils.extractBits(mc, 0, 25);
        return new BFormatInstruction(mc, info.mnemonic, opcode, BitUtils.signExtend32(brAddress, 26));
    }

    private CBFormatInstruction decodeCBFormat(int mc, OpcodeInfo info) {
        int opcode = BitUtils.extractBits(mc, 24, 31);
        String finalMnemonic = info.mnemonic;
        int rtOrCond = BitUtils.extractBits(mc, 0, 9);
        System.out.println("CBFormat: " + finalMnemonic + ", rtOrCond: " + rtOrCond);

        if ("B.cond".equals(finalMnemonic)) {
            finalMnemonic = getBranchConditionMnemonic(rtOrCond);
        }

        int condBrAddress = BitUtils.extractBits(mc, 5, 23);
        return new CBFormatInstruction(mc, finalMnemonic, opcode, BitUtils.signExtend32(condBrAddress, 19), rtOrCond);
    }
    
    private IMFormatInstruction decodeIMFormat(int mc, OpcodeInfo info) {
        int opcode = BitUtils.extractBits(mc, 23, 31);
        int hw = BitUtils.extractBits(mc, 21, 22);
        int immediate = BitUtils.extractBits(mc, 5, 20);
        int rd = BitUtils.extractBits(mc, 0, 4);
        return new IMFormatInstruction(mc, info.mnemonic, opcode, hw, immediate, rd);
    }
    
    private String getBranchConditionMnemonic(int condField) {
        switch (condField) {
            case 0b0000: return "B.EQ";
            case 0b0001: return "B.NE";
            case 0b0010: return "B.HS";
            case 0b0011: return "B.LO";
            case 0b0100: return "B.MI";
            case 0b0101: return "B.PL";
            case 0b0110: return "B.VS";
            case 0b0111: return "B.VC";
            case 0b1000: return "B.HI";
            case 0b1001: return "B.LS";
            case 0b1010: return "B.GE";
            case 0b1011: return "B.LT";
            case 0b1100: return "B.GT";
            case 0b1101: return "B.LE";
            default: return "B.cond_UNKNOWN";
        }
    }
}