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

            // II. Lệnh Logical
            0x8A010022, // AND X2, X1, X1 (Giả sử X1=0b1010, X2=0b1010 sau lệnh này)
            0x8A210022, // ANDS X2, X1, X1 (Giả sử X1=0b0000, X2=0b0000 và cờ Z được đặt)
            0x92001421, // ANDI X1, X1, #5 (Giả sử X1=0b1111, X1=0b0101 sau lệnh này)
            0xAA010022, // ORR X2, X1, X1 (Giả sử X1=0b1010, X2=0b1010 sau lệnh này)
            0xF2001421, // ORRI X1, X1, #5 (Giả sử X1=0b0011, X1=0b0111 sau lệnh này)
            0xCA010022, // EOR X2, X1, X1 (Giả sử X1=0b1010, X2=0b0000 sau lệnh này)
            0xD2001421, // EORI X1, X1, #5 (Giả sử X1=0b1100, X1=0b1001 sau lệnh này)
            0xD3400000, // LSR X0, X0, #0 (Không có tác dụng, nhưng là một mẫu hợp lệ)
            0xD3400400, // LSR X0, X0, #1 (Giả sử X0=1, X0=2 sau lệnh này)
            0xD3600000, // LSL X0, X0, #0 (Không có tác dụng)
            0xD3600400, // LSL X0, X0, #1 (Giả sử X0=2, X0=1 sau lệnh này)
            0xD3800000, // ASR X0, X0, #0 (Không có tác dụng)
            0xD3800400, // ASR X0, X0, #1 (Giả sử X0=-2 (0xFFFFFFFFFFFFFFFE), X0=-1 (0xFFFFFFFFFFFFFFFF) sau lệnh này)

            // III. Lệnh truyền dữ liệu
            0xF8400003, // LDUR X3, [X0, #0]      (Tải 64-bit từ địa chỉ X0+0 vào X3)
            0xF8000003, // STUR X3, [X0, #0]      (Lưu 64-bit từ X3 vào địa chỉ X0+0)
            0xB8800004, // LDURSW X4, [X0, #0]    (Tải 32-bit có dấu từ X0+0 vào X4)
            0xB8000004, // STURW X4, [X0, #0]     (Lưu 32-bit thấp của X4 vào X0+0)
            0x78400005, // LDURH W5, [X0, #0]     (Tải 16-bit từ X0+0 vào W5, zero-extend)
            0x78000005, // STURH W5, [X0, #0]     (Lưu 16-bit từ W5 vào X0+0)
            0x38400006, // LDURB W6, [X0, #0]     (Tải 8-bit từ X0+0 vào W6, zero-extend)
            0x38000006, // STURB W6, [X0, #0]     (Lưu 8-bit từ W6 vào X0+0)
            // 0x885F7C02, // LDXR X2, [X0]
            // 0xC8007C01, // STXR W1, W3, [X0]


            // IV. Lệnh rẽ nhánh có điều kiện
            0xB4000000, // CBZ X0, #0     (Nhảy nếu X0 == 0, offset = 0)
            0xB5000000, // CBNZ X0, #0    (Nhảy nếu X0 != 0, offset = 0)
            0x54000000, // B.EQ  (Z == 1)
            0x54000001, // B.NE  (Z == 0)
            0x54000002, // B.HS  (C == 1) aka B.CS
            0x54000003, // B.LO  (C == 0) aka B.CC
            0x54000004, // B.MI  (N == 1)
            0x54000005, // B.PL  (N == 0)
            0x54000006, // B.VS  (V == 1)
            0x54000007, // B.VC  (V == 0)
            0x54000008, // B.HI  (C == 1 && Z == 0)
            0x54000009, // B.LS  (C == 0 || Z == 1)
            0x5400000A, // B.GE  (N == V)
            0x5400000B, // B.LT  (N != V)
            0x5400000C, // B.GT  (Z == 0 && N == V)
            0x5400000D, // B.LE  (Z == 1 || N != V)

            // Có thể thêm các điều kiện khác như B.GT (Z=0 & N=V), B.LT (N!=V), ...

            // V. Lệnh rẽ nhánh không điều kiện
            0x14000000, // B #0           (Nhảy vô điều kiện, offset = 0)
            0x94000000, // BL #0          (Nhảy vô điều kiện, lưu PC+4 vào X30 (LR))
            0xD64003C0, // BR X30         (Quay về địa chỉ trong X30 — thường dùng sau BL)

            // VI. Lệnh Move Wide (Zero và Keep)
            0xD2800000, // MOVZ X0, #0, LSL #0         (X0 = 0)
            0xD2800021, // MOVZ X1, #1, LSL #0         (X1 = 1)
            0xD2A00021, // MOVZ X1, #1, LSL #16        (X1 = 0x00010000)
            0xF2800000, // MOVK X0, #0, LSL #0         (ghi đè 16 bit thấp với 0)
            0xF2A00021, // MOVK X1, #1, LSL #16        (ghi đè bit 16–31 bằng 1)

            0x00000000 // Một lệnh dừng hoặc NOP ở cuối
        };
        
        simulator.loadProgram(0x00, program);
        simulator.run(100); // Chạy tối đa 50 lệnh
    }
}