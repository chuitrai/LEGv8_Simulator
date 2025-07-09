package main.java.com.mydomain.legv8simulator.core;

import main.java.com.mydomain.legv8simulator.assembler.Assembler;
import main.java.com.mydomain.legv8simulator.simulator.Simulator;
import java.util.List;
import java.util.ArrayList;

/**
 * Singleton class để quản lý trạng thái global của simulation
 * Tất cả các UI components đều có thể truy cập vào đây
 */
public class SimulationManager {
    private static SimulationManager instance;
    
    // Các thành phần chính
    private Memory memory;
    private CPU cpu;
    private Simulator simulator;
    private Assembler assembler;
    
    // Dữ liệu program
    private int[] machineCode;
    public List<String> assemblyLines;
    private String currentFileName;
    
    // Trạng thái simulation
    private boolean isAssembled;
    private boolean isLoaded;
    private boolean isRunning;
    private int currentPC;
    
    // Private constructor để implement Singleton
    private SimulationManager() {
        initialize();
    }
    
    // Lấy instance duy nhất
    public static SimulationManager getInstance() {
        if (instance == null) {
            instance = new SimulationManager();
        }
        return instance;
    }
    
    // Khởi tạo các thành phần
    private void initialize() {
        memory = new Memory();
        cpu = new CPU();
        simulator = new Simulator(cpu, memory);
        assembler = new Assembler();
        assemblyLines = new ArrayList<>();
        isAssembled = false;
        isLoaded = false;
        isRunning = false;
        currentPC = 0;
    }
    
    // Reset toàn bộ hệ thống
    public void reset() {
        simulator.reset();
    }
    
    // =============== GETTERS ===============
    public Memory getMemory() { return memory; }
    public CPU getCpu() { return cpu; }
    public Simulator getSimulator() { return simulator; }
    public Assembler getAssembler() { return assembler; }
    public int[] getMachineCode() { return machineCode; }
    public List<String> getAssemblyLines() { return assemblyLines; }
    public String getCurrentFileName() { return currentFileName; }
    public boolean isAssembled() { return isAssembled; }
    public boolean isLoaded() { return isLoaded; }
    public boolean isRunning() { return isRunning; }
    public int getCurrentPC() { return currentPC; }
    
    // =============== SETTERS ===============
    public void setMachineCode(int[] machineCode) {
        this.machineCode = machineCode;
        this.isAssembled = (machineCode != null);
    }
    
    public void setAssemblyLines(List<String> assemblyLines) {
        this.assemblyLines = new ArrayList<>(assemblyLines);
    }
    
    public void setCurrentFileName(String fileName) {
        this.currentFileName = fileName;
    }
    
    public void setCurrentPC(int pc) {
        this.currentPC = pc;
    }
    
    // =============== OPERATIONS ===============
    
    /**
     * Assemble code từ assembly lines
     */
    public boolean assembleCode(List<String> lines) {
        try {
            this.assemblyLines = new ArrayList<>(lines);
            this.machineCode = assembler.assemble(lines);
            this.isAssembled = true;
            this.isLoaded = false; // Cần load lại vào simulator
            return true;
        } catch (Exception e) {
            this.isAssembled = false;
            this.machineCode = null;
            System.err.println("Assembly failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load program vào simulator
     */
    public boolean loadProgram(int startAddress) {
        if (!isAssembled || machineCode == null) {
            return false;
        }
        
        try {
            simulator.loadProgram(startAddress, machineCode);
            this.isLoaded = true;
            this.currentPC = startAddress;
            return true;
        } catch (Exception e) {
            this.isLoaded = false;
            System.err.println("Load program failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Chạy simulation với số bước giới hạn
     */
    public boolean runSimulation(int maxSteps) {
        // if (!isLoaded) {
        //     return false;
        // }
        System.out.println(" runSimulation was Run");
        
        try {
            this.isRunning = true;
            simulator.run(maxSteps);
            this.isRunning = false;
            return true;
        } catch (Exception e) {
            this.isRunning = false;
            System.err.println("Simulation failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Chạy một bước duy nhất
     */
    public boolean stepSimulation(int step) {
        // if (!isLoaded) {
        //     return false;
        // }
        System.out.println(" stepSimulation was Run");
        
        try {
            this.simulator.isRunning = true;
            boolean result = simulator.step(step); // Giả sử có method step()
            this.simulator.isRunning = false;
            return result;
        } catch (Exception e) {
            this.isRunning = false;
            System.err.println("Step simulation failed: " + e.getMessage());
            return false;
        }
    }

    public void cycleSimulation() {
        System.out.println(" cycleSimulation was Run");
        try {
            this.simulator.isRunning = true;
            simulator.doFullClockCycle(); 
            this.simulator.isRunning = false;
        } catch (Exception e) {
            this.isRunning = false;
            System.err.println("Step simulation failed: " + e.getMessage());
        }
    }
    
    /**
     * Lấy thông tin register
     */
    public long getRegisterValue(int registerIndex) {
        return cpu.getRegister(registerIndex);
    }
    
    /**
     * Lấy thông tin memory
     */
    public int getMemoryValue(int address) {
        return memory.loadWord(address);
    }
    
    /**
     * Lấy instruction hiện tại
     */
    public String getCurrentInstruction() {
        if (assemblyLines == null || assemblyLines.isEmpty()) {
            return "";
        }
        
        int lineIndex = currentPC / 4; // Giả sử mỗi instruction 4 bytes
        if (lineIndex >= 0 && lineIndex < assemblyLines.size()) {
            return assemblyLines.get(lineIndex);
        }
        return "";
    }
    
    /**
     * Kiểm tra trạng thái sẵn sàng để chạy
     */
    public boolean isReadyToRun() {
        return isAssembled && isLoaded && !isRunning;
    }
    
    /**
     * Lấy thông tin trạng thái dưới dạng string
     */
    public String getStatusString() {
        if (isRunning) return "Running...";
        if (isLoaded) return "Ready to run";
        if (isAssembled) return "Assembled, ready to load";
        return "No program loaded";
    }
}