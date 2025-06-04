// src/main/java/com/yourdomain/legv8simulator/SimulatorApp.java
package main.java.com.mydomain.legv8simulator;

import main.java.com.mydomain.legv8simulator.core.*;

public class SimulatorApp {
    public static void main(String[] args) {
        long memorySize = 1024 * 1024; // 1MB bộ nhớ
        Memory memory = new Memory(memorySize);
        CPU cpu = new CPU(memory);

        // Chương trình mẫu đơn giản (ví dụ: ADDI X1, X0, #5; ADD X2, X1, X1; LDUR X3, [X0, #0])
        // Đây là các giá trị mã máy HEX của các lệnh LEGv8
        // Bạn cần tự dịch hoặc dùng một công cụ assembler để tạo ra các giá trị này
        int[] program = {
            0x91001421, // ADDI X1, X1, #5
            0x8B010022, // ADD X2, X1, X1
            0xF8407C03, // LDUR X3, [X0, #0]
            0x00000000, // NOOP hoặc lệnh dừng
            0x00000000
        };
        // Đặt các lệnh ở địa chỉ 0 trong bộ nhớ
        cpu.loadProgram(0, program);

        // Chạy CPU
        cpu.run(100); // Chạy tối đa 100 lệnh

        // In trạng thái cuối cùng của các thanh ghi
        cpu.printRegisters();
    }
}