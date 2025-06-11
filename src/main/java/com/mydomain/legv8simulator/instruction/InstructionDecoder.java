// src/main/java/com/yourdomain/legv8simulator/instruction/InstructionDecoder.java
package main.java.com.mydomain.legv8simulator.instruction;

import main.java.com.mydomain.legv8simulator.utils.BitUtils; // Giả sử bạn có lớp này

import java.util.HashMap;
import java.util.Map;

/**
 * Lớp InstructionDecoder hoạt động như một Factory, chịu trách nhiệm giải mã mã máy
 * thành các đối tượng Instruction. Sử dụng nhiều HashMap để tra cứu hiệu quả
 * các opcode có độ dài khác nhau.
 */
public class InstructionDecoder {

    private static class OpcodeInfo {
        final String mnemonic; // Tên lệnh cơ sở, ví dụ: "ADD", "SUB"
        final InstructionFormat format;
        final boolean hasSetFlagsVariant; // Lệnh này có biến thể 'S' không?

        OpcodeInfo(String baseMnemonic, InstructionFormat format, boolean hasSetFlagsVariant) {
            this.mnemonic = baseMnemonic;
            this.format = format;
            this.hasSetFlagsVariant = hasSetFlagsVariant;
    }
}

    // Tạo các bảng tra cứu riêng cho từng độ dài opcode
    private static final Map<Integer, OpcodeInfo> TABLE_OPCODE_6_BIT = new HashMap<>();
    private static final Map<Integer, OpcodeInfo> TABLE_OPCODE_8_BIT = new HashMap<>();
    private static final Map<Integer, OpcodeInfo> TABLE_OPCODE_9_BIT = new HashMap<>();
    private static final Map<Integer, OpcodeInfo> TABLE_OPCODE_10_BIT = new HashMap<>();
    private static final Map<Integer, OpcodeInfo> TABLE_OPCODE_11_BIT = new HashMap<>();

    // Khối static để điền dữ liệu vào các bảng tra cứu
    static {
    // --- B-FORMAT (6-bit opcode, bits 31-26) ---
    // Các lệnh này không có biến thể 'S'
    TABLE_OPCODE_6_BIT.put(0b000101, new OpcodeInfo("B", InstructionFormat.B_FORMAT, false));
    TABLE_OPCODE_6_BIT.put(0b100101, new OpcodeInfo("BL", InstructionFormat.B_FORMAT, false));

    // --- CB-FORMAT (8-bit opcode, bits 31-24) ---
    // Các lệnh này không có biến thể 'S'
    TABLE_OPCODE_8_BIT.put(0b10110100, new OpcodeInfo("CBZ", InstructionFormat.CB_FORMAT, false));
    TABLE_OPCODE_8_BIT.put(0b10110101, new OpcodeInfo("CBNZ", InstructionFormat.CB_FORMAT, false));
    // B.cond dùng opcode này như một "họ lệnh"
    TABLE_OPCODE_8_BIT.put(0b01010100, new OpcodeInfo("B.cond", InstructionFormat.CB_FORMAT, false));

    // --- IM-FORMAT (9-bit opcode, bits 31-23) ---
    // MOVZ/MOVK không cập nhật cờ
    TABLE_OPCODE_9_BIT.put(0b110100101, new OpcodeInfo("MOVZ", InstructionFormat.IM_FORMAT, false));
    TABLE_OPCODE_9_BIT.put(0b111100101, new OpcodeInfo("MOVK", InstructionFormat.IM_FORMAT, false));

    // --- I-FORMAT (10-bit opcode, bits 31-22) ---
    // Lưu ý: Key là opcode đã được dịch phải 1 bit (loại bỏ bit S) để gộp ADDI và ADDIS
    int addi_base_op = 0b1001000100 >> 1; // 0b100100010
    int subi_base_op = 0b1101000100 >> 1; // 0b110100010
    int andi_base_op = 0b1001001000 >> 1; // 0b100100100
    
    // Đã sửa lại key và thêm 'hasSetFlagsVariant'
    TABLE_OPCODE_10_BIT.put(addi_base_op, new OpcodeInfo("ADD", InstructionFormat.I_FORMAT, true));  // ADD(I/IS)
    TABLE_OPCODE_10_BIT.put(subi_base_op, new OpcodeInfo("SUB", InstructionFormat.I_FORMAT, true));  // SUB(I/IS)
    TABLE_OPCODE_10_BIT.put(andi_base_op, new OpcodeInfo("AND", InstructionFormat.I_FORMAT, true));  // AND(I/IS)

    // Các lệnh này không có biến thể 'S', giữ nguyên key 10 bit
    TABLE_OPCODE_10_BIT.put(0b1011001000, new OpcodeInfo("ORR", InstructionFormat.I_FORMAT, false)); // ORRI
    TABLE_OPCODE_10_BIT.put(0b1101001000, new OpcodeInfo("EOR", InstructionFormat.I_FORMAT, false)); // EORI

    // --- R-FORMAT & D-FORMAT (11-bit opcode, bits 31-21) ---
    
    // R-FORMAT
    TABLE_OPCODE_11_BIT.put(0b10001011000, new OpcodeInfo("ADD", InstructionFormat.R_FORMAT, true));
    TABLE_OPCODE_11_BIT.put(0b11001011000, new OpcodeInfo("SUB", InstructionFormat.R_FORMAT, true));
    TABLE_OPCODE_11_BIT.put(0b10001010000, new OpcodeInfo("AND", InstructionFormat.R_FORMAT, true));
    TABLE_OPCODE_11_BIT.put(0b10101010000, new OpcodeInfo("ORR", InstructionFormat.R_FORMAT, false)); // ORR không có 'S' trong bảng của bạn
    TABLE_OPCODE_11_BIT.put(0b11001010000, new OpcodeInfo("EOR", InstructionFormat.R_FORMAT, false));
    TABLE_OPCODE_11_BIT.put(0b10011011000, new OpcodeInfo("MUL", InstructionFormat.R_FORMAT, false));
    TABLE_OPCODE_11_BIT.put(0b11010011011, new OpcodeInfo("LSL", InstructionFormat.R_FORMAT, false));
    TABLE_OPCODE_11_BIT.put(0b11010011010, new OpcodeInfo("LSR", InstructionFormat.R_FORMAT, false));
    TABLE_OPCODE_11_BIT.put(0b11010011000, new OpcodeInfo("ASR", InstructionFormat.R_FORMAT, false));
    TABLE_OPCODE_11_BIT.put(0b11010110000, new OpcodeInfo("BR", InstructionFormat.R_FORMAT, false));

    // D-FORMAT (không có biến thể 'S')
    TABLE_OPCODE_11_BIT.put(0b11111000010, new OpcodeInfo("LDUR", InstructionFormat.D_FORMAT, false));
    TABLE_OPCODE_11_BIT.put(0b11111000000, new OpcodeInfo("STUR", InstructionFormat.D_FORMAT, false));
    TABLE_OPCODE_11_BIT.put(0b10111000100, new OpcodeInfo("LDURSW", InstructionFormat.D_FORMAT, false));
    TABLE_OPCODE_11_BIT.put(0b10111000000, new OpcodeInfo("STURW", InstructionFormat.D_FORMAT, false));
    TABLE_OPCODE_11_BIT.put(0b01111000010, new OpcodeInfo("LDURH", InstructionFormat.D_FORMAT, false));
    TABLE_OPCODE_11_BIT.put(0b01111000000, new OpcodeInfo("STURH", InstructionFormat.D_FORMAT, false));
    TABLE_OPCODE_11_BIT.put(0b00111000010, new OpcodeInfo("LDURB", InstructionFormat.D_FORMAT, false));
    TABLE_OPCODE_11_BIT.put(0b00111000000, new OpcodeInfo("STURB", InstructionFormat.D_FORMAT, false));
}

    /**
     * Phương thức chính, giải mã mã máy.
     * Thử tra cứu tuần tự từ opcode dài nhất đến ngắn nhất để tránh trùng lặp.
     * Ví dụ: Một opcode 11-bit có thể chứa một mẫu 6-bit bên trong nó.
     */
    public Instruction decode(int machineCode) {
        OpcodeInfo info;

        // Ưu tiên 1: Opcode 11-bit (R, D)
        int op11 = BitUtils.extractBits(machineCode, 21, 31);
        info = TABLE_OPCODE_11_BIT.get(op11);
        if (info != null) {
            switch (info.format) {
                case R_FORMAT: return decodeRFormat(machineCode, info);
                case D_FORMAT: return decodeDFormat(machineCode, info);
            }
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
    
    // ----- Các hàm giải mã cho từng định dạng -----
    private static final int S_BIT_POSITION = 29; // Vị trí bit S trong R-format (bit 29)

    private RFormatInstruction decodeRFormat(int mc, OpcodeInfo info) {
        String mnemonic = info.mnemonic;
        // Kiểm tra bit S để phân biệt, ví dụ, ADD và ADDS
        if (info.hasSetFlagsVariant) {
            // Kiểm tra bit 29
            if (BitUtils.isBitSet(mc, S_BIT_POSITION)) {
                mnemonic += "S";
            }
        }
        
        int opcode = BitUtils.extractBits(mc, 21, 31);
        int rm = BitUtils.extractBits(mc, 16, 20);
        int shamt = BitUtils.extractBits(mc, 10, 15);
        int rn = BitUtils.extractBits(mc, 5, 9);
        int rd = BitUtils.extractBits(mc, 0, 4);
        
        return new RFormatInstruction(mc, mnemonic, opcode, rd, rn, rm, shamt);
    }

    private IFormatInstruction decodeIFormat(int mc, OpcodeInfo info) {
        String mnemonic = info.mnemonic;
        
        // Đối với I-format, bit 'S' là bit thấp nhất của opcode 10-bit (bit 22)
        if (info.hasSetFlagsVariant) {
            if (BitUtils.isBitSet(mc, 22)) { // Giả sử bit 22 là bit S cho I-Format
                mnemonic += "IS";
            } else {
                mnemonic += "I";
            }
        } else {
            // Nếu không có biến thể 'S', chỉ cần thêm 'I' (ví dụ ORRI, EORI)
            mnemonic += "I";
        }

        int opcode = BitUtils.extractBits(mc, 22, 31);
        int immediate = BitUtils.extractBits(mc, 10, 21);
        int rn = BitUtils.extractBits(mc, 5, 9);
        int rd = BitUtils.extractBits(mc, 0, 4);
        
        return new IFormatInstruction(mc, mnemonic, opcode, rd, rn, BitUtils.signExtend32(immediate, 12));
    }
    // private RFormatInstruction decodeRFormat(int mc, OpcodeInfo info) {
    //     return new RFormatInstruction(mc, info.mnemonic, BitUtils.extractBits(mc, 21, 31),
    //             BitUtils.extractBits(mc, 0, 4), BitUtils.extractBits(mc, 5, 9),
    //             BitUtils.extractBits(mc, 16, 20), BitUtils.extractBits(mc, 10, 15));
    // }
    
    // // Lưu ý sửa lỗi nhỏ: IFormatInstruction(mc, mnemonic, opcode, rd, rn, immediate)
    // private IFormatInstruction decodeIFormat(int mc, OpcodeInfo info) {
    //     int immediate = BitUtils.extractBits(mc, 10, 21);
    //     return new IFormatInstruction(mc, info.mnemonic, BitUtils.extractBits(mc, 22, 31),
    //             BitUtils.extractBits(mc, 0, 4), BitUtils.extractBits(mc, 5, 9),
    //             BitUtils.signExtend32(immediate, 12));
    // }

    private DFormatInstruction decodeDFormat(int mc, OpcodeInfo info) {
        int dtAddress = BitUtils.extractBits(mc, 12, 20);
        return new DFormatInstruction(mc, info.mnemonic, BitUtils.extractBits(mc, 21, 31),
                BitUtils.signExtend32(dtAddress, 9),
                BitUtils.extractBits(mc, 10, 11),
                BitUtils.extractBits(mc, 5, 9),
                BitUtils.extractBits(mc, 0, 4));
    }

    // Các hàm decodeBFormat, decodeCBFormat, decodeIMFormat tương tự...
    private BFormatInstruction decodeBFormat(int mc, OpcodeInfo info) {
        int offset = BitUtils.extractBits(mc, 0, 19);
        return new BFormatInstruction(mc, info.mnemonic, BitUtils.extractBits(mc, 26, 31),
                BitUtils.signExtend32(offset, 19));
    }

    private CBFormatInstruction decodeCBFormat(int mc, OpcodeInfo info) {
        int offset = BitUtils.extractBits(mc, 0, 19);
        return new CBFormatInstruction(mc, info.mnemonic, BitUtils.extractBits(mc, 24, 31),
                BitUtils.signExtend32(offset, 19), BitUtils.extractBits(mc, 0, 4));
    }

    private IMFormatInstruction decodeIMFormat(int mc, OpcodeInfo info) {
        int immediate = BitUtils.extractBits(mc, 10, 21);
        return new IMFormatInstruction(mc, info.mnemonic, BitUtils.extractBits(mc, 23, 31),
                BitUtils.extractBits(mc, 0, 4), BitUtils.extractBits(mc, 5, 9),
                BitUtils.signExtend32(immediate, 12));
    }
    
    // ----- Lớp nội bộ cho lệnh không xác định -----
    public static class UnknownInstruction implements Instruction {
        private final int machineCode;
        public UnknownInstruction(int machineCode) { this.machineCode = machineCode; }
        @Override public int getMachineCode() { return machineCode; }
        @Override public InstructionFormat getFormat() { return InstructionFormat.UNKNOWN; }
        @Override public String getOpcodeMnemonic() { return "UNKNOWN"; }
    }
}