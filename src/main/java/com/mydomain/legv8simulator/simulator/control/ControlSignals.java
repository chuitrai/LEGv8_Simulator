package main.java.com.mydomain.legv8simulator.simulator.control;

import main.java.com.mydomain.legv8simulator.core.ALUOperation;

/**
 * Lớp DTO (Data Transfer Object) bất biến, chứa một tập hợp đầy đủ các tín hiệu
 * điều khiển được tạo ra bởi ControlUnit cho một lệnh cụ thể, dựa trên sơ đồ datapath.
 *
 * <p>Mỗi trường trong lớp này tương ứng với một "dây dẫn" tín hiệu, quyết định
 * hành vi của các thành phần như MUX, ALU, và các đơn vị bộ nhớ.
 */
public final class ControlSignals {

    // --- Tín hiệu cho MUX và đơn vị thanh ghi ---

    /**
     * Reg2Loc: Tín hiệu chọn thanh ghi ghi (Write Register) cho Register File.
     * <ul>
     *   <li>{@code false}: Thanh ghi đích là Rd (bits 4-0). Dùng cho lệnh R-Format và I-Format.</li>
     *   <li>{@code true}: Thanh ghi đích là Rt (bits 20-16). Dùng cho lệnh Load trong một số thiết kế.
     *       (Lưu ý: LEGv8 thường dùng Rt ở bits 4-0 cho cả Load, nhưng chúng ta sẽ mô phỏng theo sơ đồ).</li>
     * </ul>
     */
    public final boolean reg2Loc;

    /**
     * RegWrite: Tín hiệu cho phép ghi kết quả ngược trở lại Register File.
     * Bật cho hầu hết các lệnh (R-Format, I-Format, Load), tắt cho Store và Branch.
     */
    public final boolean regWrite;


    // --- Tín hiệu cho ALU ---

    /**
     * ALUSrc: Tín hiệu chọn nguồn thứ hai cho ALU (đầu vào B).
     * <ul>
     *   <li>{@code false}: Đầu vào là giá trị từ Register File (Read data 2). Dùng cho lệnh R-Format.</li>
     *   <li>{@code true}: Đầu vào là giá trị immediate đã được mở rộng dấu. Dùng cho lệnh I-Format và D-Format.</li>
     * </ul>
     */
    public final boolean aluSrc;

    /**
     * ALUOp: Tín hiệu 2-bit từ Control Unit chính đến ALU Control.
     * Giúp ALU Control quyết định phép toán cuối cùng.
     * Ví dụ: 00 (L/S), 01 (Branch), 10 (R-Type).
     */
     public final ALUOperation aluOperation;


    // --- Tín hiệu cho Memory ---

    /**
     * MemRead: Tín hiệu cho phép đọc từ Data Memory.
     * Chỉ được bật (true) cho các lệnh Load (ví dụ: LDUR).
     */
    public final boolean memRead;

    /**
     * MemWrite: Tín hiệu cho phép ghi vào Data Memory.
     * Chỉ được bật (true) cho các lệnh Store (ví dụ: STUR).
     */
    public final boolean memWrite;

    /**
     * MemToReg: Tín hiệu chọn dữ liệu để ghi vào thanh ghi (điều khiển MUX cuối cùng).
     * <ul>
     *   <li>{@code false}: Dữ liệu từ kết quả của ALU. Dùng cho lệnh R-Format, I-Format.</li>
     *   <li>{@code true}: Dữ liệu từ Data Memory. Dùng cho lệnh Load.</li>
     * </ul>
     */
    public final boolean memToReg;


    // --- Tín hiệu cho việc rẽ nhánh (Branching) ---

    /**
     * UncondBranch: Tín hiệu cho biết đây là một lệnh rẽ nhánh không điều kiện (B, BL).
     */
    public final boolean uncondBranch;
    
    /**
     * FlagBranch: Tín hiệu cho biết đây là một lệnh rẽ nhánh dựa trên cờ (B.cond).
     */
    public final boolean flagBranch;
    
    /**
     * ZeroBranch: Tín hiệu cho biết đây là một lệnh rẽ nhánh dựa vào kết quả bằng 0 (CBZ, CBNZ).
     * Sơ đồ của bạn có thể gộp nó vào FlagBranch, nhưng tách ra sẽ rõ ràng hơn.
     */
    public final boolean zeroBranch;
    
    /**
     * FlagWrite: Tín hiệu cho phép cập nhật thanh ghi cờ (PSTATE).
     * Chỉ bật cho các lệnh có đuôi 'S' (ADDS, SUBS, ...).
     */
    public final boolean flagWrite;


    /**
     * Constructor để tạo một bộ tín hiệu điều khiển đầy đủ.
     * Sử dụng Builder Pattern được khuyến khích để khởi tạo.
     */
    public ControlSignals(boolean reg2Loc, boolean regWrite, boolean aluSrc,  ALUOperation aluOperation,
                          boolean memRead, boolean memWrite, boolean memToReg,
                          boolean uncondBranch, boolean flagBranch, boolean zeroBranch, boolean flagWrite) {
        this.reg2Loc = reg2Loc;
        this.regWrite = regWrite;
        this.aluSrc = aluSrc;
        this.aluOperation = aluOperation;
        this.memRead = memRead;
        this.memWrite = memWrite;
        this.memToReg = memToReg;
        this.uncondBranch = uncondBranch;
        this.flagBranch = flagBranch;
        this.zeroBranch = zeroBranch;
        this.flagWrite = flagWrite;
    }

    /**
     * Builder Pattern để tạo đối tượng ControlSignals một cách dễ đọc và an toàn.
     */
    public static class Builder {
        private ALUOperation aluOperation = ALUOperation.ADD;
        private boolean reg2Loc, regWrite, aluSrc, memRead, memWrite, memToReg;
        private boolean uncondBranch, flagBranch, zeroBranch, flagWrite;

        // Thiết lập các giá trị mặc định (tương ứng với lệnh NOP)
        public Builder() {
            this.reg2Loc = false;
            this.regWrite = false;
            this.aluSrc = false;
            this.aluOperation = ALUOperation.ADD; // Mặc định là ADD, có thể thay đổi sau
            this.memRead = false;
            this.memWrite = false;
            this.memToReg = false;
            this.uncondBranch = false;
            this.flagBranch = false;
            this.zeroBranch = false;
            this.flagWrite = false;
        }

        public Builder reg2Loc(boolean val) { this.reg2Loc = val; return this; }
        public Builder regWrite(boolean val) { this.regWrite = val; return this; }
        public Builder aluSrc(boolean val) { this.aluSrc = val; return this; }
         public Builder aluOperation(ALUOperation val) { this.aluOperation = val; return this; }
        public Builder memRead(boolean val) { this.memRead = val; return this; }
        public Builder memWrite(boolean val) { this.memWrite = val; return this; }
        public Builder memToReg(boolean val) { this.memToReg = val; return this; }
        public Builder uncondBranch(boolean val) { this.uncondBranch = val; return this; }
        public Builder flagBranch(boolean val) { this.flagBranch = val; return this; }
        public Builder zeroBranch(boolean val) { this.zeroBranch = val; return this; }
        public Builder flagWrite(boolean val) { this.flagWrite = val; return this; }

        public ControlSignals build() {
            return new ControlSignals(reg2Loc, regWrite, aluSrc, aluOperation, memRead,
                                      memWrite, memToReg, uncondBranch, flagBranch,
                                      zeroBranch, flagWrite);
        }
    }
}