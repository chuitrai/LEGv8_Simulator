package main.java.com.mydomain.legv8simulator;

import main.java.com.mydomain.legv8simulator.core.CPU;
import main.java.com.mydomain.legv8simulator.core.Memory;
import main.java.com.mydomain.legv8simulator.simulator.Simulator;
import main.java.com.mydomain.legv8simulator.UI.SimulatorApp;

import javafx.stage.Stage;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import main.java.com.mydomain.legv8simulator.assembler.*;

public class Main {

    public static void main(String[] args) throws Exception {
        // 1. Khởi tạo các thành phần phần cứng
        Memory memory = new Memory(); // Dùng kích thước mặc định
        CPU cpu = new CPU();

        // 2. Khởi tạo trình giả lập
        Simulator simulator = new Simulator(cpu, memory);

        // 3. Nạp chương trình từ file assembly
        Assembler assembler = new Assembler();

        List<String> assemblyLines = Files.readAllLines(Paths.get("resources/assembly_examples/example_2.s"));

        // Hợp dịch
        try {
            int[] machineCode = assembler.assemble(assemblyLines);
            
            // Nạp và chạy
            simulator.loadProgram(0x00, machineCode);
            simulator.run(5);

        } catch (AssemblyException e) {
            System.err.println("Assembly Failed: " + e.getMessage());
        }


        // 3. Tạo một chương trình mẫu
       
        // int[] program = {
        //     0xF08001CE, 
        //     0x00000000,
        // };
        
        // simulator.loadProgram(0x00, machineCode);
        // simulator.run(100); // Chạy tối đa 50 lệnh
    }
}