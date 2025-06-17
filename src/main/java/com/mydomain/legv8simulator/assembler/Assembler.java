package main.java.com.mydomain.legv8simulator.assembler;

import main.java.com.mydomain.legv8simulator.instruction.InstructionFormat;
import main.java.com.mydomain.legv8simulator.utils.BitUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lớp Assembler chịu trách nhiệm dịch mã nguồn assembly LEGv8 thành mã máy.
 * Nó sử dụng quy trình hợp dịch hai lượt (two-pass) để xử lý các nhãn (labels).
 */
public class Assembler {

    private final Parser parser;
    private final SymbolTable symbolTable;

    // Regex để phân tích các toán hạng thanh ghi (ví dụ: "X1", "XZR")
    private static final Pattern REGISTER_PATTERN = Pattern.compile("X(\\d+)|(XZR)", Pattern.CASE_INSENSITIVE);
    // Regex để phân tích các toán hạng tức thời (ví dụ: "#100", "#-50")
    private static final Pattern IMMEDIATE_PATTERN = Pattern.compile("#(-?\\d+)");
    // Regex để phân tích toán hạng địa chỉ của D-Format (ví dụ: "[X1, #8]")
    private static final Pattern D_ADDRESS_PATTERN = Pattern.compile("\\[X(\\d+)(?:,\\s*#(-?\\d+))?\\]", Pattern.CASE_INSENSITIVE);


    public Assembler() {
        this.parser = new Parser();
        this.symbolTable = new SymbolTable();
    }

    /**
     * Phương thức chính để hợp dịch một danh sách các dòng code assembly.
     * @param lines Danh sách các dòng code từ file .s.
     * @return Một mảng int chứa mã máy 32-bit.
     * @throws AssemblyException nếu có lỗi cú pháp hoặc logic trong quá trình hợp dịch.
     */
    public int[] assemble(List<String> lines) throws AssemblyException {
        // Reset SymbolTable cho mỗi lần hợp dịch mới
        symbolTable.clear();

        // Lượt 1: Xây dựng bảng ký hiệu (Symbol Table)
        firstPass(lines);

        // Lượt 2: Dịch lệnh và tạo mã máy
        return secondPass(lines);
    }

    /**
     * Thực hiện lượt đầu tiên: quét qua code để tìm tất cả các nhãn và lưu địa chỉ của chúng.
     * @param lines Danh sách các dòng code.
     * @throws AssemblyException nếu có nhãn bị trùng lặp.
     */
    private void firstPass(List<String> lines) throws AssemblyException {
        int address = 0;
        for (int i = 0; i < lines.size(); i++) {

            int finalIndex = i; // Biến final để sử dụng trong lambda

            ParseResult result = parser.parse(lines.get(finalIndex), finalIndex + 1);
            if (result == null) {
                continue; // Bỏ qua dòng trống hoặc comment
            }
            
            final int finalAddress = address; // Biến final để sử dụng trong lambda
            // Nếu dòng có nhãn, thêm nó vào SymbolTable với địa chỉ hiện tại
            result.getLabel().ifPresent(label -> {
                try {
                    symbolTable.addSymbol(label, finalAddress); 
                } catch (AssemblyException e) {
                    // Ném lại lỗi với thông tin số dòng
                    throw new RuntimeException(new AssemblyException(e.getMessage(), finalIndex + 1));
                }
            });

            // Nếu dòng có lệnh, tăng địa chỉ lên 4 cho lệnh tiếp theo
            if (result.getInstruction().isPresent()) {
                address += 4;
            }
        }
    }

    /**
     * Thực hiện lượt thứ hai: dịch từng lệnh thành mã máy 32-bit.
     * @param lines Danh sách các dòng code.
     * @return Mảng int chứa mã máy.
     * @throws AssemblyException nếu có lỗi cú pháp, toán hạng không hợp lệ, hoặc nhãn không xác định.
     */
    private int[] secondPass(List<String> lines) throws AssemblyException {
        List<Integer> machineCodeList = new ArrayList<>();
        int currentAddress = 0;

        for (int i = 0; i < lines.size(); i++) {
            ParseResult result = parser.parse(lines.get(i), i + 1);
            if (result == null || result.getInstruction().isEmpty()) {
                continue; // Chỉ xử lý các dòng có lệnh
            }

            ParsedInstruction pInstr = result.getInstruction().get();
            int encodedInstruction = encodeInstruction(pInstr, currentAddress);
            // in ra thông tin mã máy đã được mã hóa
            System.out.println("Encoded instruction: " + pInstr.getMnemonic() + " -> " + encodedInstruction + " at address 0x" + Integer.toHexString(currentAddress).toUpperCase());
            machineCodeList.add(encodedInstruction);
            currentAddress += 4;
        }

        // Chuyển List<Integer> thành int[]
        return machineCodeList.stream().mapToInt(Integer::intValue).toArray();
    }
    
    /**
     * Mã hóa một lệnh đã được phân tích thành mã máy 32-bit.
     * Đây là nơi logic "ghép bit" chính diễn ra.
     * @param pInstr Đối tượng chứa thông tin lệnh đã phân tích.
     * @param currentAddress Địa chỉ của lệnh hiện tại, cần để tính offset cho lệnh rẽ nhánh.
     * @return Một số int 32-bit đại diện cho mã máy.
     * @throws AssemblyException nếu mnemonic hoặc toán hạng không hợp lệ.
     */
    private int encodeInstruction(ParsedInstruction pInstr, int currentAddress) throws AssemblyException {
        // Lấy mnemonic và thông tin opcode từ OpcodeTable
        String mnemonic = pInstr.getMnemonic();
        OpcodeTable.MnemonicInfo info = OpcodeTable.getInstance().getInfo(mnemonic)
                    .orElseThrow(() -> new AssemblyException("Unknown or unsupported mnemonic: '" + mnemonic + "'"));
        
        int opcode = info.getOpcode();
        String[] operands = pInstr.getOperands();

        switch (info.getFormat()) {
            case R_FORMAT:
                if (mnemonic.equals("BR")) {
                    checkOperands(pInstr, 1);
                    int rn = parseRegister(operands[0]);
                    return (opcode << 21) | (rn << 5);
                } else if (mnemonic.equals("LSL") || mnemonic.equals("LSR") || mnemonic.equals("ASR")) {
                    checkOperands(pInstr, 3);
                    int rd = parseRegister(operands[0]);
                    int rn = parseRegister(operands[1]);
                    int shamt = parseImmediate(operands[2], 6);
                    return (opcode << 21) | (shamt << 10) | (rn << 5) | rd;
                } else { // Các lệnh R-format 3 thanh ghi khác
                    checkOperands(pInstr, 3);
                    int rd = parseRegister(operands[0]);
                    int rn = parseRegister(operands[1]);
                    int rm = parseRegister(operands[2]);
                    return (opcode << 21) | (rm << 16) | (rn << 5) | rd;
                }

            case I_FORMAT:
                checkOperands(pInstr, 3);
                int rd_i = parseRegister(operands[0]);
                int rn_i = parseRegister(operands[1]);
                int imm12 = parseImmediate(operands[2], 12);
                return (opcode << 22) | ((imm12 & 0xFFF) << 10) | (rn_i << 5) | rd_i;

            case D_FORMAT:
                checkOperands(pInstr, 2);
                int rd_d = parseRegister(operands[0]);
                int[] dAddress = parseDAddress(operands[1]);
                int rn_d = dAddress[0];
                int offset = dAddress[1];
                // Kiểm tra giới hạn của offset (giả sử -4096 đến 4095)
                if (offset < -4096 || offset > 4095) {
                    throw new AssemblyException("Offset out of range: " + offset);
                }
                System.out.println("Check D Format: rd=" + rd_d + ", rn=" + rn_d + ", offset=" + offset + ", opcode=" + BitUtils.toBinaryString32(opcode) + ", MNEMONIC=" + mnemonic);
                return (opcode << 21) | ((offset & 0xFFF) << 10) | (rn_d << 5) | rd_d;
            case B_FORMAT:
                checkOperands(pInstr, 2);
                int rn_b = parseRegister(operands[0]);
                int offset_b = parseImmediate(operands[1], 19); // Offset 19 bit
                // Kiểm tra giới hạn của offset (giả sử -524288 đến 524287)
                if (offset_b < -524288 || offset_b > 524287) {
                    throw new AssemblyException("Offset out of range: " + offset_b);
                }
                return (opcode << 21) | ((offset_b & 0x3FFFF) << 5) | rn_b;
            case CB_FORMAT:
                // in ra thông tin pInstr để dễ debug
                System.out.println("Check CB Format: " + pInstr.toString());
                checkOperands(pInstr, 2);
                int rn_cb = parseRegister(operands[0]);
                
                // Lấy address của nhãn nếu có
                int offset_cb;
                if (operands[1].startsWith("0x") || operands[1].startsWith("0X")) {
                    offset_cb = parseImmediate(operands[1], 19); // Offset 19 bit
                } else {
                    // Nếu không phải là hằng số, tra cứu nhãn
                    int address = symbolTable.getAddress(operands[1]);
                    offset_cb = address - currentAddress; // Tính offset từ địa chỉ hiện tại
                    System.out.println("Label '" + operands[1] + "' resolved to address: " + address + ", offset: " + offset_cb);
                }
                // Kiểm tra giới hạn của offset (giả sử -524288 đến 524287)
                if (offset_cb < -524288 || offset_cb > 524287) {
                    throw new AssemblyException("Offset out of range: " + offset_cb);
                }
                System.out.println("----------->" + BitUtils.toBinaryString32(opcode) + "\n" + BitUtils.toBinaryString32(rn_cb) + "\n" + BitUtils.toBinaryString32(offset_cb));
                return (opcode << 24) | rn_cb | ((offset_cb & 0x7FFFF) << 5);
                
            case IM_FORMAT:
                checkOperands(pInstr, 2);
                int rd_im = parseRegister(operands[0]);
                int imm19 = parseImmediate(operands[1], 19); // Offset 19 bit
                // Kiểm tra giới hạn của immediate (giả sử -524288 đến 524287)
                if (imm19 < -524288 || imm19 > 524287) {
                    throw new AssemblyException("Immediate out of range: " + imm19);
                }
                return (opcode << 21) | ((imm19 & 0x3FFFF) << 5) | rd_im;

            default:
                throw new AssemblyException("Unknown mnemonic: '" + mnemonic + "'");
        }
    }

    // =========================================================================
    // Helper Methods for Parsing Operands
    // =========================================================================

    private int parseRegister(String operand) throws AssemblyException {
        Matcher m = REGISTER_PATTERN.matcher(operand.trim());
        if (!m.matches()) {
            throw new AssemblyException("Invalid register format: '" + operand + "'");
        }
        if (m.group(2) != null) { // XZR
            return 31;
        }
        return Integer.parseInt(m.group(1));
    }

    private int parseImmediate(String operand, int bitSize) throws AssemblyException {

        Matcher m = IMMEDIATE_PATTERN.matcher(operand.trim());
        if (!m.matches()) {
            throw new AssemblyException("Invalid immediate format: '" + operand + "'");
        }
        
        // kiểm tra bitSize
        int value = Integer.parseInt(m.group(1));
        int maxValue = (1 << bitSize) - 1; // Giá trị tối đa cho bitSize
        int minValue = -(1 << (bitSize - 1)); // Giá trị tối thiểu cho bitSize
        if (value < minValue || value > maxValue) {
            throw new AssemblyException("Immediate value out of range: " + value);
        }


        return Integer.parseInt(m.group(1));
    }

    private int[] parseDAddress(String operand) throws AssemblyException {
        Matcher m = D_ADDRESS_PATTERN.matcher(operand.trim());
        if (!m.matches()) {
            throw new AssemblyException("Invalid D-format address operand: '" + operand + "'");
        }
        int rn = Integer.parseInt(m.group(1));
        int offset = (m.group(2) != null) ? Integer.parseInt(m.group(2)) : 0;
        return new int[]{rn, offset};
    }
    
    private void checkOperands(ParsedInstruction pInstr, int expectedCount) throws AssemblyException {
        if (pInstr.getOperands().length != expectedCount) {
            // In tất cả thông tin chi tiết của lệnh để dễ debug
            System.out.println("Error in instruction: " + pInstr.toString());
            
            throw new AssemblyException(
                "Mnemonic '" + pInstr.getMnemonic() + "' expects " + expectedCount + " operands, but got " + pInstr.getOperands().length
            );
        }
    }
}