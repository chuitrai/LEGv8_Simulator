package main.java.com.mydomain.legv8simulator.simulator;

import main.java.com.mydomain.legv8simulator.core.*;
import main.java.com.mydomain.legv8simulator.gui.DatapathSnapshot;
import main.java.com.mydomain.legv8simulator.instruction.*;
import main.java.com.mydomain.legv8simulator.simulator.pipeline.*;
import main.java.com.mydomain.legv8simulator.utils.BitUtils;
import main.java.com.mydomain.legv8simulator.simulator.control.*;
import main.java.com.mydomain.legv8simulator.common.*;


/**
 * Lớp Simulator điều khiển quá trình mô phỏng pipeline 5 giai đoạn.
 * Nó quản lý các chốt (latch) và điều phối các thành phần phần cứng
 * trong từng giai đoạn của chu trình lệnh.
 */
public class Simulator extends Observable_demo {

    public final CPU cpu;
    public final Memory memory;
    public final InstructionDecoder decoder;
    public final ControlUnit controlUnit;
    public final ALU alu;
    public DatapathSnapshot snapshot;

    // Các chốt (latches) của pipeline
    public IF_ID_Latch if_id_latch = new IF_ID_Latch(0, 0); // Khởi tạo NOP
    public ID_EX_Latch id_ex_latch;
    public EX_MEM_Latch ex_mem_latch;
    public MEM_WB_Latch mem_wb_latch;

    public boolean isRunning = false;
    private int clockCycleCount = 0;

    public Simulator(CPU cpu, Memory memory) {
        this.cpu = cpu;
        System.out.println("Get value of CPU: " + cpu.getPC().getValue());
        this.memory = memory;
        this.decoder = new InstructionDecoder();
        this.controlUnit = new ControlUnit();
        this.alu = new ALU();
    }

    // --- Các phương thức điều khiển chính ---

    public void loadProgram(long startAddress, int[] machineCode) {
        for (int i = 0; i < machineCode.length; i++) {
            memory.storeWord(startAddress + (long) i * 4, machineCode[i]);
        }
        cpu.getPC().setValue(startAddress);
    }

    public boolean step(int step){
        if (!isRunning) {
            System.out.println("Simulation is not running. Please start the simulation first.");
            return false;
        }
        if (step <= 0) {
            System.out.println("Invalid step count. Please provide a positive integer.");
            return false;
        }
        // System.out.println("\n//================== STEP " + step + " START ==================//");
        switch (step) {
            case 1:
                doFetchStage();
                // System.out.println("IF Stage completed.");
                // if (if_id_latch != null) {
                //     System.out.println("IF_ID_Latch: " + if_id_latch);
                // } else {
                //     System.out.println("IF_ID_Latch is NOP.");
                // }
                break;
            case 2:
                doDecodeStage();
                break;
            case 3:
                doExecuteStage();
                break;
            case 4:
                doMemoryStage();
                break;
            case 5:
                doWriteBackStage();
                break;
            default:
                break;
        }
        snapshot = createSnapshot();
        return true;
    }

    public void run(int maxCycles) {
        isRunning = true;
        while (isRunning && clockCycleCount < maxCycles) {
            doFullClockCycle();
        }
        // isRunning = true;
        if (clockCycleCount >= maxCycles && isRunning) {
            System.out.println("Reached maximum cycle limit of " + maxCycles + ". Stopping simulation.");
            System.out.println("Please enter to continue running or Ctrl+C to stop.");
            // String input;
            // while (true && isRunning) {
            //     try {
            //         input = System.console().readLine();
            //         if (input != null && !input.trim().isEmpty()) {
            //             break; // Nhấn Enter để tiếp tục
            //         }
            //         doFullClockCycle(); // Tiếp tục chạy nếu không có input
            //     } catch (Exception e) {
            //         System.out.println("Error reading input. Please try again.");
            //     }
            // }
        }
    }

    public void reset() {
        cpu.reset();
        if_id_latch = new IF_ID_Latch(0, 0); // NOP
        id_ex_latch = null;
        ex_mem_latch = null;
        mem_wb_latch = null;
        System.out.println("***************** RESETTING SIMULATOR ****************");
        clockCycleCount = 0;
        isRunning = false;
        snapshot = createSnapshot();
        System.out.println("Simulator reset. All latches cleared and CPU state reset.");
    }

    /**
     * Thực hiện một chu kỳ đồng hồ hoàn chỉnh, trong đó mỗi giai đoạn
     * của pipeline được thực thi.
     * Lưu ý: Các giai đoạn được thực thi từ cuối về đầu để mô phỏng
     * đúng cách dữ liệu chảy qua pipeline trong một chu kỳ.
     */
    public void doFullClockCycle() {

        System.out.println("\n//================== CLOCK CYCLE " + (clockCycleCount + 1) + " START ==================//");
        
        doFetchStage();
        doDecodeStage();
        doExecuteStage();
        doMemoryStage();
        doWriteBackStage();
        
        System.out.println("//================== CLOCK CYCLE " + (clockCycleCount + 1) + " END ==================//");
        cpu.printState();

        clockCycleCount++;
        snapshot = createSnapshot();
    }


    // =========================================================================
    // CÁC GIAI ĐOẠN CỦA PIPELINE
    // =========================================================================

    private void doWriteBackStage() {
        System.out.print("[-] WB Stage: ");

        if (mem_wb_latch == null) {
            System.out.println("NOP");
            return;
        }

        // Logic MUX của MemToReg
        if (mem_wb_latch.controlSignals.regWrite) {
            long dataToWrite = mem_wb_latch.controlSignals.memToReg ?
                               mem_wb_latch.dataReadFromMemory :
                               mem_wb_latch.aluResult;
            int regAddr = mem_wb_latch.writeRegisterAddress;
        
            System.out.printf("Writing 0x%X to X%d\n", dataToWrite, regAddr);
            cpu.getRegisterFile().write(mem_wb_latch.writeRegisterAddress, dataToWrite);
        }
        else {
            System.out.println("No register write.");
        }
    }

    private void doMemoryStage() {
        System.out.print("[-] MEM Stage: ");
        if (ex_mem_latch == null) {
            System.out.println("NOP");
            mem_wb_latch = null; // Chuyển NOP đi tiếp
            return;
        }

        long memData = 0;
        System.out.print("ALUResult=0x" + Long.toHexString(ex_mem_latch.aluResult) + ". ");


        if (ex_mem_latch.controlSignals.memRead) {
            memData = memory.loadDoubleWord(ex_mem_latch.aluResult);
            System.out.printf("Reading 0x%X from Memory. ", memData);
        }
        if (ex_mem_latch.controlSignals.memWrite) {
            long dataToWrite = ex_mem_latch.dataToWriteToMemory;
            System.out.printf("Writing 0x%X to Memory. ", dataToWrite);
            memory.storeDoubleWord(ex_mem_latch.aluResult, dataToWrite);
            memory.dump(ex_mem_latch.aluResult, 8); // In ra vùng nhớ đã ghi
        }
        System.out.println();
        
        mem_wb_latch = new MEM_WB_Latch(ex_mem_latch.controlSignals, memData, ex_mem_latch.aluResult, ex_mem_latch.writeRegisterAddress);
    }

    private void doExecuteStage() {
        System.out.print("[-] EX Stage: \n");
        if (id_ex_latch == null) {
            System.out.println("NOP");
            ex_mem_latch = null; // Chuyển NOP đi tiếp
            return;
        }   
        
        System.out.print(id_ex_latch.instruction.getOpcodeMnemonic() + ". ");
        // Kiểm tra lệnh HALT
        if (id_ex_latch.instruction.getOpcodeMnemonic().equals("UNKNOWN")) {
            System.out.println("HALT instruction detected. Stopping execution.");
            isRunning = false;
            ex_mem_latch = null; // Chuyển NOP đi tiếp
            return;
        }
        
        long aluInput1 = id_ex_latch.readData1;
        long aluInput2 = id_ex_latch.controlSignals.aluSrc ? 
                        id_ex_latch.signExtendedImmediate : 
                        id_ex_latch.readData2;

        System.out.println("ReadData1 (Rn): 0x" + Long.toHexString(aluInput1) + 
                           ", ReadData2 (Rm): 0x" + Long.toHexString(aluInput2));
        
        ALUOperation op = id_ex_latch.controlSignals.aluOperation;
        ALUResult aluResult = alu.execute(op, aluInput1, aluInput2);
        
        System.out.printf("ALU Executing: %s(0x%X, 0x%X) -> 0x%X. ", op, aluInput1, aluInput2, aluResult.getResult());
        
        // writeRegAddr sẽ là thanh ghi đích để ghi kết quả ALU
        int writeRegAddr = id_ex_latch.controlSignals.memWrite || id_ex_latch.controlSignals.memToReg ?
                            id_ex_latch.rt : id_ex_latch.rd;
        System.out.println("Write Reg Addr: X" + writeRegAddr + ". ");
        
        System.out.println();
        
        // Update 4 cờ trạng thái nếu lệnh yêu cầu
        if (id_ex_latch.controlSignals.flagWrite) {
            System.out.println("  -> EX Stage: Updating flags.");
            cpu.getFlagsRegister().updateFlags(aluResult);
        } else {
            System.out.println("  -> EX Stage: No flag update required.");
        }


        ex_mem_latch = new EX_MEM_Latch(id_ex_latch.controlSignals, 
                                        aluResult.getResult(), 
                                        id_ex_latch.readData2, 
                                        writeRegAddr);
        
        if (id_ex_latch.controlSignals.flagWrite) {
            System.out.println("  -> EX Stage: Updating flags.");
            cpu.getFlagsRegister().updateFlags(aluResult);
        }

        System.out.println("Control Signals: ");
        id_ex_latch.controlSignals.printSignals();
    }

    private void doDecodeStage() {
        System.out.print("[-] ID Stage: ");
        if (if_id_latch == null || if_id_latch.instructionMachineCode == 0) {
            System.out.println("NOP");
            id_ex_latch = null; // Chèn NOP vào pipeline
            return;
        }
        
        if (if_id_latch.instructionMachineCode == 0xD4400000) {
            System.out.println("HALT instruction decoded. Preparing to stop.");
            isRunning = false;
            id_ex_latch = null; // Chèn NOP
            return;
        }

        Instruction instr = decoder.decode(if_id_latch.instructionMachineCode);
        System.out.println("Binary machine code: " + BitUtils.toBinaryString32(instr.getMachineCode()));
        ControlSignals signals = controlUnit.generateSignals(instr);
        
        // Logic trích xuất toán hạng
        int rn = 0, rm = 0, rd = 0, rt = 0;
        long imm = 0;
        
        // ... bạn cần thêm logic instanceof để lấy đúng các trường này từ instr ...
        if (instr instanceof RFormatInstruction r) {
            rd = r.getRd();
            rn = r.getRn();
            rm = r.getRm();
        } else if (instr instanceof IFormatInstruction i) {
            rd = i.getRd();
            rn = i.getRn();
            imm = i.getImmediate(); // Lấy immediate từ lệnh I-Format
        } else if (instr instanceof DFormatInstruction d) {
            rt = d.getRt();
            rn = d.getRn();
            imm = d.getDtAddress(); // Lấy offset từ lệnh D-Format
        } else if (instr instanceof CBFormatInstruction cb) {
            rt = cb.getRtOrCond(); // Lấy thanh ghi cần so sánh
            imm = cb.getCondBrAddress();
        } else if (instr instanceof BFormatInstruction b) {
            imm = b.getBrAddress();
        } else if (instr instanceof IMFormatInstruction im) {
            rd = im.getRd();
            imm = im.getImmediate(); // Lấy immediate 16-bit
        }
        // ------------------------------------

        long readData1 = cpu.getRegisterFile().read(rn);
        long readData2 = cpu.getRegisterFile().read(rm);

        if (signals.reg2Loc) {
            readData2 = cpu.getRegisterFile().read(rt); // Nếu reg2Loc là true, sử dụng immediate thay vì Rm
            System.out.println("Using Rt (X" + rt + ") as ReadData2 instead of Rm (X" + rm + ").");
        }
        System.out.println(signals.reg2Loc ? "YES" : "NO");

        id_ex_latch = new ID_EX_Latch(signals, if_id_latch.pcIncremented, readData1, readData2, imm, rt, rd, instr);


        System.out.println("Decoded instruction: " + instr.getOpcodeMnemonic() + 
                           " (Rd: X" + rd + ", Rn: X" + rn + ", Rm: X" + rm + 
                           ", Rt: X" + rt + ", Imm: 0x" + Long.toHexString(imm) + ")");

        System.out.println(instr.toString());
        System.out.println("-->ReadData1: 0x" + Long.toHexString(readData1) + 
                           ", ReadData2: 0x" + Long.toHexString(readData2) + 
                           ", SignExtendedImm: 0x" + Long.toHexString(imm));
    }
    
    private void doFetchStage() {
        long currentPC = cpu.getPC().getValue();
        System.out.println("Current PC: 0x" + Long.toHexString(currentPC));
        System.out.print("[-] IF Stage: ");
        if (!isRunning || currentPC >= memory.getSize()) {
            System.out.println("Stalling or finished.");
            if_id_latch = new IF_ID_Latch(currentPC, 0); // NOP
            return;
        }

        int machineCode = memory.loadWord(currentPC);
        // memory.printMemory();
        System.out.printf("Fetching from PC 0x%X -> MC: 0x%08X\n", currentPC, machineCode);
        
        cpu.getPC().setValue(currentPC + 4);
        if_id_latch = new IF_ID_Latch(currentPC + 4, machineCode);
        System.out.println("PC incremented to 0x" + Long.toHexString(if_id_latch.pcIncremented));
        System.out.println("Instruction fetched: " + BitUtils.toBinaryString32(machineCode));
    }

    // Hàm tạo snapshot
    public DatapathSnapshot createSnapshot() {
        return new DatapathSnapshot(if_id_latch, id_ex_latch, ex_mem_latch, mem_wb_latch);
    }
}