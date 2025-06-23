package main.java.com.mydomain.legv8simulator.assembler;

import main.java.com.mydomain.legv8simulator.instruction.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Lớp OpcodeTable hoạt động như một Singleton, cung cấp một nguồn chân lý duy nhất
 * cho thông tin về tất cả các lệnh LEGv8 được hỗ trợ.
 *
 * Cả Assembler và InstructionDecoder sẽ sử dụng lớp này để tra cứu thông tin
 * như mã opcode, định dạng lệnh, và các thuộc tính khác.
 */
public final class OpcodeTable {

    // --- Lớp nội bộ để lưu trữ thông tin chi tiết về một Mnemonic ---
    public static class MnemonicInfo {
        private final String mnemonic;
        private final InstructionFormat format;
        private final int opcode; // Mã opcode đầy đủ

        public MnemonicInfo(String mnemonic, InstructionFormat format, int opcode) {
            this.mnemonic = mnemonic;
            this.format = format;
            this.opcode = opcode;
        }

        public String getMnemonic() { return mnemonic; }
        public InstructionFormat getFormat() { return format; }
        public int getOpcode() { return opcode; }
    }
    
    // Bảng tra cứu chính: Key là mnemonic (String), Value là thông tin chi tiết
    private final Map<String, MnemonicInfo> mnemonicTable;

    // --- Cấu trúc Singleton ---
    private static final OpcodeTable INSTANCE = new OpcodeTable();

    public static OpcodeTable getInstance() {
        return INSTANCE;
    }

    /**
     * Constructor private để ngăn việc tạo instance từ bên ngoài.
     * Dữ liệu opcode sẽ được nạp ở đây.
     */
    private OpcodeTable() {
        mnemonicTable = new HashMap<>();
        
        // =========================================================================
        // ĐIỀN DỮ LIỆU TẬP LỆNH VÀO ĐÂY
        // =========================================================================

        // --- R-FORMAT ---
        add("ADD",    InstructionFormat.R_FORMAT, 0b10001011000);
        add("ADDS",   InstructionFormat.R_FORMAT, 0b10001011001);
        add("SUB",    InstructionFormat.R_FORMAT, 0b11001011000);
        add("SUBS",   InstructionFormat.R_FORMAT, 0b11001011001);
        add("AND",    InstructionFormat.R_FORMAT, 0b10001010000);
        add("ANDS",   InstructionFormat.R_FORMAT, 0b11101010000);
        add("ORR",    InstructionFormat.R_FORMAT, 0b10101010000);
        add("EOR",    InstructionFormat.R_FORMAT, 0b11001010000);
        add("MUL",    InstructionFormat.R_FORMAT, 0b10011011000);
        add("SDIV",   InstructionFormat.R_FORMAT, 0b10011010110);
        add("UDIV",   InstructionFormat.R_FORMAT, 0b10011010111);
        add("LSL",    InstructionFormat.R_FORMAT, 0b11010011011);
        add("LSR",    InstructionFormat.R_FORMAT, 0b11010011010);
        add("ASR",    InstructionFormat.R_FORMAT, 0b11010011100);
        add("BR",     InstructionFormat.R_FORMAT, 0b11010110000);

        // --- I-FORMAT ---
        add("ADDI",   InstructionFormat.I_FORMAT, 0b1001000100);
        add("ADDIS",  InstructionFormat.I_FORMAT, 0b1001000101);
        add("SUBI",   InstructionFormat.I_FORMAT, 0b1101000100);
        add("SUBIS",  InstructionFormat.I_FORMAT, 0b1101000101);
        add("ANDI",   InstructionFormat.I_FORMAT, 0b1001001000);
        add("ORRI",   InstructionFormat.I_FORMAT, 0b1011001000);
        add("EORI",   InstructionFormat.I_FORMAT, 0b1101001000);
        
        // --- D-FORMAT ---
        add("LDUR",   InstructionFormat.D_FORMAT, 0b11111000010);
        add("STUR",   InstructionFormat.D_FORMAT, 0b11111000000);
        add("LDURSW", InstructionFormat.D_FORMAT, 0b10111000100);
        add("STURW",  InstructionFormat.D_FORMAT, 0b10111000000);
        add("LDURH",  InstructionFormat.D_FORMAT, 0b01111000010);
        add("STURH",  InstructionFormat.D_FORMAT, 0b01111000000);
        add("LDURB",  InstructionFormat.D_FORMAT, 0b00111000010);
        add("STURB",  InstructionFormat.D_FORMAT, 0b00111000000);
        
        // --- B-FORMAT ---
        add("B",      InstructionFormat.B_FORMAT, 0b000101);
        add("BL",     InstructionFormat.B_FORMAT, 0b100101);

        // --- CB-FORMAT ---
        add("CBZ",    InstructionFormat.CB_FORMAT, 0b10110100);
        add("CBNZ",   InstructionFormat.CB_FORMAT, 0b10110101);
        // B.cond là một họ lệnh, xử lý riêng
        add("B.cond", InstructionFormat.CB_FORMAT, 0b01010100);

        // --- IM-FORMAT ---
        add("MOVZ",   InstructionFormat.IM_FORMAT, 0b110100101);
        add("MOVK",   InstructionFormat.IM_FORMAT, 0b111100101);
    }

    /**
     * Hàm nội bộ để thêm một entry vào bảng.
     */
    private void add(String mnemonic, InstructionFormat format, int opcode) {
        mnemonicTable.put(mnemonic.toUpperCase(), new MnemonicInfo(mnemonic, format, opcode));
    }

    /**
     * Lấy thông tin chi tiết của một lệnh dựa trên mnemonic.
     * @param mnemonic Tên lệnh, ví dụ: "ADD", "ADDI".
     * @return một Optional chứa MnemonicInfo nếu tìm thấy, ngược lại là Optional rỗng.
     */
    public Optional<MnemonicInfo> getInfo(String mnemonic) {
        return Optional.ofNullable(mnemonicTable.get(mnemonic.toUpperCase()));
    }
}