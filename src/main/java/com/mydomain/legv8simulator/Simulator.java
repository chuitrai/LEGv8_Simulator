package main.java.com.mydomain.legv8simulator;

import main.java.com.mydomain.legv8simulator.core.CPU;
import main.java.com.mydomain.legv8simulator.core.Memory;
import main.java.com.mydomain.legv8simulator.instruction.Instruction;
import main.java.com.mydomain.legv8simulator.instruction.InstructionDecoder;
import main.java.com.mydomain.legv8simulator.instruction.InstructionExecutor;
import main.java.com.mydomain.legv8simulator.instruction.InstructionFormat;
import main.java.com.mydomain.legv8simulator.gui.CycleState;

/**
 * Lớp Simulator là trái tim của trình giả lập.
 * Nó quản lý vòng lặp thực thi chính (Fetch-Decode-Execute) và điều phối
 * các thành phần cốt lõi như CPU, Memory, Decoder, và Executor.
 */
public class Simulator {

    private final CPU cpu;
    private final Memory memory;
    private final InstructionDecoder decoder;
    private final InstructionExecutor executor;
    private final CycleState cycleState;

    private boolean isRunning = false;
    private int instructionCount = 0;

    /**
     * Constructor để tạo một trình giả lập mới.
     * @param cpu Đối tượng CPU đã được khởi tạo.
     * @param memory Đối tượng Memory đã được khởi tạo.
     */
    public Simulator(CPU cpu, Memory memory) {
        this.cpu = cpu;
        this.memory = memory;
        this.decoder = new InstructionDecoder();
        this.executor = new InstructionExecutor(cpu, memory);
        this.cycleState = new CycleState(); // Khởi tạo trạng thái chu trình
    }

    /**
     * Nạp một chương trình (dưới dạng mảng mã máy) vào bộ nhớ tại một địa chỉ cụ thể.
     * @param startAddress Địa chỉ bắt đầu trong bộ nhớ để nạp chương trình.
     * @param machineCode Mảng các số nguyên 32-bit đại diện cho mã máy.
     */
    public void loadProgram(long startAddress, int[] machineCode) {
        for (int i = 0; i < machineCode.length; i++) {
            // Mỗi lệnh cách nhau 4 byte
            System.out.println("Loading instruction " + i + ": 0x" + Integer.toHexString(machineCode[i]) + " at address 0x" + Long.toHexString(startAddress + (long) i * 4));
            memory.storeWord(startAddress + (long) i * 4, machineCode[i]);
        }
        // Đặt PC của CPU tới địa chỉ bắt đầu của chương trình
        cpu.getPC().setValue(startAddress);
        System.out.println("Program loaded. " + machineCode.length + " instructions at address 0x" + Long.toHexString(startAddress));
    }

    /**
     * Chạy chương trình đã được nạp cho đến khi gặp lệnh HALT hoặc đạt đến số lệnh tối đa.
     * @param maxInstructions Số lệnh tối đa để thực thi, để ngăn vòng lặp vô hạn.
     */
    public void run(int maxInstructions) {
        System.out.println("\n--- Starting Simulation ---");
        cpu.reset(); // Đảm bảo CPU ở trạng thái sạch trước khi chạy
        isRunning = true;
        instructionCount = 0;

        while (isRunning && instructionCount < maxInstructions) {
            System.out.println("\n--- Step " + (instructionCount + 1) + " ---");
            System.out.println("Current PC: 0x" + Long.toHexString(cpu.getPC().getValue()));

            long currentPC = cpu.getPC().getValue();

            // Kiểm tra PC có hợp lệ không
            if (currentPC < 0 || currentPC >= memory.getSize()) {
                System.err.println("Error: Program Counter (0x" + Long.toHexString(currentPC) + ") is out of memory bounds. Halting.");
                isRunning = false;
                break;
            }

            step(); // Thực thi một bước (một lệnh)

            if (!isRunning) { // Kiểm tra lại cờ isRunning sau khi step()
                 System.out.println("Simulation halted by an instruction.");
            }
        }

        if (instructionCount >= maxInstructions) {
            System.out.println("Simulation stopped after reaching maximum instruction limit (" + maxInstructions + ").");
        }
        
        System.out.println("\n--- Simulation Finished ---");
        System.out.println("Total instructions executed: " + instructionCount);
        cpu.printState();
        // memory.dump(0, 256, 16); // Tùy chọn: in ra một phần bộ nhớ để kiểm tra
    }

    /**
     * Thực thi một chu trình Fetch-Decode-Execute duy nhất.
     * Hữu ích cho việc chạy từng bước (step-by-step).
     */
    public void step() {
        if (!isRunning && instructionCount > 0) {
            System.out.println("Simulation is not running.");
            return;
        }

        long currentPC = cpu.getPC().getValue();
        
        try {
            // 1. FETCH
            // int machineCode = memory.loadWord(currentPC);
            fetch();

            // Xử lý HALT một cách đặc biệt
            // Trong một hệ thống thực, đây có thể là một lệnh supervisor call (SVC).
            // if (machineCode == 0x0000000) { // Dừng
            //     isRunning = false;
            //     return;
            // }

            // 2. DECODE
            // Instruction instruction = decoder.decode(machineCode);
            decode();
            
            // In thông tin để debug
            // System.out.printf("PC: 0x%04X | MC: 0x%08X | Decoded: %s\n", currentPC, machineCode, instruction.toString());

            // 3. EXECUTE
            // executor.execute(instruction, currentPC);
            execute();

            instructionCount++;
            // Cập nhật PC sau khi thực thi lệnh

        } catch (Exception e) {
            System.err.println("FATAL ERROR at PC=0x" + Long.toHexString(currentPC) + ": " + e.getMessage());
            // In stack trace để dễ debug hơn
            e.printStackTrace();
            isRunning = false;
        }
    }

    /**
     * Thực hiện bước FETCH để lấy mã máy từ bộ nhớ.
     */
    public void fetch() {
        long currentPC = cpu.getPC().getValue();
        cycleState.setPcAtFetch(currentPC);
        
        if (currentPC < 0 || currentPC >= memory.getSize()) {
            throw new RuntimeException("Program Counter out of memory bounds.");
        }
        
        int machineCode = memory.loadWord(currentPC);
        cycleState.setFetchedMachineCode(machineCode);
        
        System.out.printf("FETCHED from PC 0x%04X: 0x%08X\n", currentPC, machineCode);
    }

    /**
     * Giai đoạn DECODE:
     * - Lấy mã máy từ CycleState.
     * - Xử lý các trường hợp đặc biệt như HALT.
     * - Gọi Decoder để giải mã thành đối tượng Instruction.
     * - Lưu đối tượng Instruction vào CycleState.
     */
    public void decode() {
        int machineCode = cycleState.getFetchedMachineCode();

        // Xử lý HALT
        if (machineCode == 0x0000000) {
            System.out.println("DECODED: HALT instruction. Halting simulation.");
            this.isRunning = false;
            // Tạo một lệnh "ảo" để Executor không làm gì cả
            cycleState.setDecodedInstruction(new HaltInstruction());
            return;
        }

        Instruction instruction = decoder.decode(machineCode);
        cycleState.setDecodedInstruction(instruction);
        
        System.out.println("DECODED: " + instruction);
    }

    /**
     * Giai đoạn EXECUTE:
     * - Lấy đối tượng Instruction và PC từ CycleState.
     * - Gọi Executor để thực thi lệnh.
     * - Executor sẽ tự cập nhật PC, thanh ghi và cờ.
     */
    public void execute() {
        Instruction instruction = cycleState.getDecodedInstruction();
        long pcBeforeExecute = cycleState.getPcAtFetch();

        // Nếu là lệnh HALT hoặc không có lệnh (do lỗi decode), không làm gì cả
        if (instruction == null || !this.isRunning) {
            return;
        }

        executor.execute(instruction, pcBeforeExecute);
        
        System.out.println("EXECUTED: " + instruction.getOpcodeMnemonic());
    }

    // Lớp nội bộ nhỏ để đại diện cho lệnh HALT, giúp Executor xử lý dễ hơn.
    private static class HaltInstruction implements Instruction {
        private final int machineCode;
        private final String mnemonic;

        public HaltInstruction() {
            this.machineCode = 0xD4400000;
            this.mnemonic = "HALT";
        }

        @Override
        public int getMachineCode() {
            return machineCode;
        }

        @Override
        public String getOpcodeMnemonic() {
            return mnemonic;
        }

        @Override
        public InstructionFormat getFormat() {
            return InstructionFormat.UNKNOWN;
        }

        @Override
        public String toString() {
            return mnemonic;
        }
    }

    /**
     * Dừng trình giả lập.
     */
    public void stop() {
        this.isRunning = false;
    }

    /**
     * Đặt lại toàn bộ hệ thống (CPU và bộ nhớ).
     * @param clearMemory true nếu muốn xóa cả bộ nhớ, false nếu chỉ reset CPU.
     */
    public void reset(boolean clearMemory) {
        cpu.reset();
        if (clearMemory) {
            // Tạo lại đối tượng Memory để xóa hoàn toàn
            // this.memory = new Memory(); // Cần xem xét lại nếu memory là final
        }
        instructionCount = 0;
        isRunning = false;
        System.out.println("System reset.");
    }
}