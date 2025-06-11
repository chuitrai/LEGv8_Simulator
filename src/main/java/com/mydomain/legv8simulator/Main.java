package main.java.com.mydomain.legv8simulator;

import main.java.com.mydomain.legv8simulator.core.CPU;
import main.java.com.mydomain.legv8simulator.core.Memory;

public class Main {

    public static void main(String[] args) {
        // 1. Khởi tạo các thành phần phần cứng
        Memory memory = new Memory(); // Dùng kích thước mặc định
        CPU cpu = new CPU();

        // 2. Khởi tạo trình giả lập
        Simulator simulator = new Simulator(cpu, memory);

        // 3. Tạo một chương trình mẫu
        // Ví dụ:
        // 0x00: ADDI X1, XZR, #100    ; X1 = 100
        // 0x04: ADDI X2, XZR, #50     ; X2 = 50
        // 0x08: SUBS XZR, X1, X2      ; CMP X1, X2 -> 100 - 50. Cờ N=0, Z=0.
        // 0x0C: B.GT 0x14             ; Vì 100 > 50 (signed), (Z=0 AND N==V) -> true, nhảy tới 0x14
        // 0x10: ADD X3, XZR, XZR      ; Lệnh này sẽ bị bỏ qua
        // 0x14: STUR X1, [XZR, #200]  ; Lưu X1 (100) vào địa chỉ 200
        // 0x18: HALT
        int[] program = {
            0x8B010022, // ADD X2, X1, X1 (Giả sử X1 chứa giá trị, ví dụ X1=10, X2=20 sau lệnh này)
            0x8B210022, // ADDS X2, X1, X1 (Giả sử X1=0x7FFFFFFFFFFFFFFF, X2 sẽ tràn và các cờ sẽ được đặt)
            0x91001421, // ADDI X1, X1, #5 (Giả sử X1=10, X1=15 sau lệnh này)
            0x91201421, // ADDIS X1, X1, #5 (Giả sử X1=0x7FFFFFFFFFFFFFFF, X1 sẽ tràn và các cờ sẽ được đặt)
            0xCB010022, // SUB X2, X1, X1 (Giả sử X1=10, X2=0 sau lệnh này)
            0xCB210022, // SUBS X2, X1, X1 (Giả sử X1=10, X2=0 và cờ Z được đặt)
            0xD1001421, // SUBI X1, X1, #5 (Giả sử X1=10, X1=5 sau lệnh này)
            0xD1201421, // SUBIS X1, X1, #5 (Giả sử X1=5, X1=0 và cờ Z được đặt)
            0x9B010022, // MUL X2, X1, X1 (Giả sử X1=5, X2=25 sau lệnh này)

            // // II. Lệnh Logical
            // 0x8A010022, // AND X2, X1, X1 (Giả sử X1=0b1010, X2=0b1010 sau lệnh này)
            // 0x8A210022, // ANDS X2, X1, X1 (Giả sử X1=0b0000, X2=0b0000 và cờ Z được đặt)
            // 0x92001421, // ANDI X1, X1, #5 (Giả sử X1=0b1111, X1=0b0101 sau lệnh này)
            // 0xBA010022, // ORR X2, X1, X1 (Giả sử X1=0b1010, X2=0b1010 sau lệnh này)
            // 0xF2001421, // ORRI X1, X1, #5 (Giả sử X1=0b0011, X1=0b0111 sau lệnh này)
            // 0xCA010022, // EOR X2, X1, X1 (Giả sử X1=0b1010, X2=0b0000 sau lệnh này)
            // 0xD2001421, // EORI X1, X1, #5 (Giả sử X1=0b1100, X1=0b1001 sau lệnh này)
            // 0xD3400000, // LSL X0, X0, #0 (Không có tác dụng, nhưng là một mẫu hợp lệ)
            // 0xD3410000, // LSL X0, X0, #1 (Giả sử X0=1, X0=2 sau lệnh này)
            // 0xD3600000, // LSR X0, X0, #0 (Không có tác dụng)
            // 0xD3610000, // LSR X0, X0, #1 (Giả sử X0=2, X0=1 sau lệnh này)
            // 0xD3800000, // ASR X0, X0, #0 (Không có tác dụng)
            // 0xD3810000, // ASR X0, X0, #1 (Giả sử X0=-2 (0xFFFFFFFFFFFFFFFE), X0=-1 (0xFFFFFFFFFFFFFFFF) sau lệnh này)

            // // III. Lệnh truyền dữ liệu
            // 0xF8407C03, // LDUR X3, [X0, #0] (Tải giá trị từ địa chỉ X0+0 vào X3)
            // 0xF8007C03, // STUR X3, [X0, #0] (Lưu giá trị của X3 vào địa chỉ X0+0)
            // 0xF8407C04, // LDURSW X4, [X0, #0] (Tải 32-bit từ X0+0, mở rộng dấu vào X4)
            // 0xF8007C04, // STURW X4, [X0, #0] (Lưu 32-bit thấp của X4 vào X0+0)
            // 0xF8407C05, // LDURH X5, [X0, #0] (Tải 16-bit từ X0+0, mở rộng 0 vào X5)
            // 0xF8007C05, // STURH X5, [X0, #0] (Lưu 16-bit thấp của X5 vào X0+0)
            // 0xF8407C06, // LDURB X6, [X0, #0] (Tải 8-bit từ X0+0, mở rộng 0 vào X6)
            // 0xF8007C06, // STURB X6, [X0, #0] (Lưu 8-bit thấp của X6 vào X0+0)

            // // IV. Lệnh rẽ nhánh có điều kiện
            // 0xB4000000, // CBZ X0, label (Lệnh này sẽ rẽ nhánh nếu X0 = 0, offset = 0x0)
            // 0xB5000000, // CBNZ X0, label (Lệnh này sẽ rẽ nhánh nếu X0 != 0, offset = 0x0)
            // 0x54000000, // B.EQ label (Lệnh này sẽ rẽ nhánh nếu cờ Z=1, offset = 0x0)
            // 0x54000040, // B.NE label (Lệnh này sẽ rẽ nhánh nếu cờ Z=0, offset = 0x0)
            // // ... và các điều kiện khác cho B.cond ...

            // // V. Lệnh rẽ nhánh không điều kiện
            // 0x14000000, // B label (Offset 0x0, rẽ nhánh đến địa chỉ hiện tại + 0)
            // 0x94000000, // BL label (Offset 0x0, lưu PC+4 vào LR và rẽ nhánh)
            // 0xD61F0000, // BR XZR (Không có tác dụng, nhưng là một mẫu hợp lệ; thường dùng BR LR)

            // // VI. Lệnh Move Wide
            // 0xD2800000, // MOVZ X0, #0, LSL #0 (X0 = 0)
            // 0xD2800020, // MOVZ X0, #1, LSL #0 (X0 = 1)
            // 0xD2800021, // MOVZ X1, #1, LSL #16 (X1 = 0x00010000)
            // 0xF2800020, // MOVK X0, #1, LSL #0 (Giả sử X0 đã có giá trị, chỉ thay đổi 16 bit thấp nhất thành 1)
            // 0xF2800021, // MOVK X1, #1, LSL #16 (Giả sử X1 đã có giá trị, chỉ thay đổi 16 bit từ bit 16 đến 31 thành 1)

            // // VII. Lệnh hệ thống và giả lệnh
            // 0xAC000000, // NOP (ví dụ opcode của NOP)
            // 0xAA000020, // MOV X0, X1 (chuyển X1 vào X0)
            // 0xEB00001F, // CMP XZR, X0 (so sánh X0 với 0, đặt cờ)
            // 0xF100001F, // CMPI XZR, #0 (so sánh 0 với 0, đặt cờ)
            // 0xCB0003E0, // NEG X0, X0 (X0 = -X0)
            // 0xFFFFFFFF, // HALT (đây là một giá trị giả định cho HALT, thực tế tùy thuộc vào simulator của bạn)

            0x00000000 // Một lệnh dừng hoặc NOP ở cuối
        };
        
        simulator.loadProgram(0x00, program);
        simulator.run(8); // Chạy tối đa 50 lệnh
    }
}