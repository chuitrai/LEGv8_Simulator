package main.java.com.mydomain.legv8simulator.UI.Animation;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import main.java.com.mydomain.legv8simulator.UI.datapath.LEGv8Datapath;
import main.java.com.mydomain.legv8simulator.core.SimulationManager;
import main.java.com.mydomain.legv8simulator.utils.BitUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static main.java.com.mydomain.legv8simulator.UI.datapath.DatapathGraphicsFX.drawControlText;

public class TextBlockController extends StackPane {

    public LEGv8Datapath datapath;
    private List<MovingTextBlock> activeBlocks;
    private SimulationManager simManager;
    // Các biến endStage, notifyEndStage, stageCompleted không còn cần thiết
    // khi dùng callback trực tiếp.

    public TextBlockController(LEGv8Datapath datapath) {
        this.datapath = datapath;
        this.activeBlocks = new ArrayList<>();
    }

    public int getActiveBlockCount() {
        return activeBlocks.size();
    }

    // =======================================================================
    // CÁC HÀM SIMULATE ĐƯỢC TÁI CẤU TRÚC VỚI RUNNABLE ONDONE (ĐẦY ĐỦ)
    // =======================================================================

    public void simulateFetch(Runnable onDone) {
        clearAllBlocks();
        System.out.println("============== START FETCH STAGE ============");
        simManager = SimulationManager.getInstance();
        String adrr = String.format("0x%04X", simManager.getSimulator().cpu.getPC().getValue());
        System.out.println("initial Address: " + adrr);

        MovingTextBlock pcToIMBlock = MovingBlockFactory.pcToInstructionMemory(datapath, adrr);
        MovingTextBlock pcToAdd4Block = MovingBlockFactory.pcToAdd4(datapath, adrr);

<<<<<<< HEAD
        AtomicInteger completionCounter = new AtomicInteger(0);
        Runnable onSubTaskDone = () -> {
            if (completionCounter.incrementAndGet() == 2 && onDone != null) {
                onDone.run();
            }
        };

        pcToIMBlock.setOnPathCompleted(onSubTaskDone);
=======
         simManager.stepSimulation(1);
                simManager= SimulationManager.getInstance();

>>>>>>> 1b3341c828bf2b0bd623d9aa89d89e98ba6ddfae

        pcToAdd4Block.setOnPathCompleted(() -> {
            MovingTextBlock const4Block = MovingBlockFactory.constant4ToAdd4(datapath);
            const4Block.setOnPathCompleted(() -> {
<<<<<<< HEAD
                simManager.stepSimulation(1);
                simManager = SimulationManager.getInstance();
=======
>>>>>>> 1b3341c828bf2b0bd623d9aa89d89e98ba6ddfae
                String newAddr = String.format("0x%04X", simManager.getSimulator().cpu.getPC().getValue());
                MovingTextBlock resultBlock = MovingBlockFactory.add4ResultToMux(datapath, newAddr);
                resultBlock.setOnPathCompleted(onSubTaskDone);
                addAndStartBlock(resultBlock);
            });
            addAndStartBlock(const4Block);
        });

        addAndStartBlock(pcToIMBlock);
        addAndStartBlock(pcToAdd4Block);
    }

<<<<<<< HEAD
    public void simulateDecode(Runnable onDone) {
        // clearAllBlocks();
        System.out.println("============== START DECODE STAGE ============");
=======
    public void simulateDecode() {
        //this.clearAllBlocks();
        System.out.println(" ============== START DECODE STAGE ============");
>>>>>>> 1b3341c828bf2b0bd623d9aa89d89e98ba6ddfae
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(2);
        int instr = simManager.getSimulator().snapshot.if_id_latch.instructionMachineCode;

        MovingTextBlock instructionBus = MovingBlockFactory.instructionBus(datapath, BitUtils.toBinaryString32(instr));

        instructionBus.setOnPathCompleted(() -> {
<<<<<<< HEAD
            List<MovingTextBlock> decodedBlocks = List.of(
                MovingBlockFactory.opcodeToControl(BitUtils.toBinaryString(instr, 21, 31)),
                MovingBlockFactory.rnToRegisterFile(BitUtils.toBinaryString(instr, 5, 9)),
                MovingBlockFactory.rmToMuxRegInput(BitUtils.toBinaryString(instr, 16, 20)),
                MovingBlockFactory.rtToRegisterFile(BitUtils.toBinaryString(instr, 0, 4)),
                MovingBlockFactory.rdToMuxWriteReg(BitUtils.toBinaryString(instr, 0, 4)),
                MovingBlockFactory.immediateToSignExtend(BitUtils.toBinaryString32(instr))
            );
            runBlocksInParallel(decodedBlocks, onDone);
=======
            addAndStartBlock(MovingBlockFactory.opcodeToControl(BitUtils.toBinaryString(instr, 21, 31)));
            addAndStartBlock(MovingBlockFactory.rnToRegisterFile(BitUtils.toBinaryString(instr, 5, 9)));
            addAndStartBlock(MovingBlockFactory.rmToMuxRegInput(BitUtils.toBinaryString(instr, 16, 20)));
            addAndStartBlock(MovingBlockFactory.rtToRegisterFile(BitUtils.toBinaryString(instr, 0, 4)));
            addAndStartBlock(MovingBlockFactory.rdToMuxWriteReg(BitUtils.toBinaryString(instr, 0, 4)));
            addAndStartBlock(MovingBlockFactory.immediateToSignExtend(BitUtils.toBinaryString(instr, 21, 31) + "..."));
>>>>>>> 1b3341c828bf2b0bd623d9aa89d89e98ba6ddfae
        });

        addAndStartBlock(instructionBus);
    }

<<<<<<< HEAD
    public void simulateExecute(Runnable onDone) {
        // clearAllBlocks();
        System.out.println("============== START EXECUTE STAGE ============");
=======
    public void simulateExecute() {
        //this.clearAllBlocks();
        System.out.println(" ============== START EXECUTE STAGE ============");
        // Lấy id_ex_latch từ SimulationManager
>>>>>>> 1b3341c828bf2b0bd623d9aa89d89e98ba6ddfae
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(3);
        simManager = SimulationManager.getInstance();
        var id_ex_latch = simManager.getSimulator().id_ex_latch;
<<<<<<< HEAD
        drawControlText(datapath.gc, true);

        List<MovingTextBlock> blocksInStage = List.of(
            // Control Signals
            MovingBlockFactory.reg2LocSignal(datapath, id_ex_latch.controlSignals.reg2Loc ? "1" : "0"),
            MovingBlockFactory.uncondBranchSignal(datapath, id_ex_latch.controlSignals.uncondBranch ? "1" : "0"),
            MovingBlockFactory.flagBranchSignal(datapath, id_ex_latch.controlSignals.flagBranch ? "1" : "0"),
            MovingBlockFactory.zeroBranchSignal(datapath, id_ex_latch.controlSignals.zeroBranch ? "1" : "0"),
            MovingBlockFactory.memReadSignal(datapath, id_ex_latch.controlSignals.memRead ? "1" : "0"),
            MovingBlockFactory.memToRegSignal(datapath, id_ex_latch.controlSignals.memToReg ? "1" : "0"),
            MovingBlockFactory.memWriteSignal(datapath, id_ex_latch.controlSignals.memWrite ? "1" : "0"),
            MovingBlockFactory.flagWriteSignal(datapath, id_ex_latch.controlSignals.flagWrite ? "1" : "0"),
            MovingBlockFactory.aluSrcSignal(datapath, id_ex_latch.controlSignals.aluSrc ? "1" : "0"),
            MovingBlockFactory.aluOpSignal(datapath, id_ex_latch.controlSignals.aluOperation.toString()),
            MovingBlockFactory.regWriteSignal(datapath, id_ex_latch.controlSignals.regWrite ? "1" : "0"),
            
            // Data Paths
            MovingBlockFactory.opcodeToALUControl(datapath, "Instr"),
            MovingBlockFactory.readData1ToALU(datapath, "Data1"),
            MovingBlockFactory.readData2ToMux(datapath, "Data2"),
            MovingBlockFactory.signExtendToMux(datapath, "sign64"),
            MovingBlockFactory.signExtendToShiftLeft(datapath, "signShiftLeft"),
            MovingBlockFactory.pcForBranching(datapath, "toAddBranch"),
            MovingBlockFactory.muxResultToRegDest(datapath, " "),
            MovingBlockFactory.muxResultToALU(datapath, " "),
            MovingBlockFactory.shiftResultToAddBranch(datapath, " "),
            
            // And, Or, Logic
            MovingBlockFactory.aluToFlags(datapath, "..."),
            MovingBlockFactory.aluZeroToAndGate(datapath, "..."),
            MovingBlockFactory.and1ToOrGate(datapath, "..."),
            MovingBlockFactory.and2ToOrGate(datapath, "..."),
            MovingBlockFactory.flagToAndGate(datapath, "..."),
            MovingBlockFactory.orResultToMux(datapath, "..."),
            MovingBlockFactory.aluControlToALU(datapath, "ALU_Ctrl"),
            MovingBlockFactory.branchAdderToMux(datapath, "0")
        );

        runBlocksInParallel(blocksInStage, onDone);
    }
=======
        if (id_ex_latch == null) {
            System.out.println("ID_EX_Latch is null, skipping execute stage.");
            return;
        }

        // Simulate control signals
        drawControlText(datapath.gc, true);
        addAndStartBlock(MovingBlockFactory.reg2LocSignal(datapath, id_ex_latch.controlSignals.reg2Loc ? "1" : "0"));
        addAndStartBlock(MovingBlockFactory.uncondBranchSignal(datapath, id_ex_latch.controlSignals.uncondBranch ? "1" : "0"));
        addAndStartBlock(MovingBlockFactory.flagBranchSignal(datapath, id_ex_latch.controlSignals.flagBranch ? "1" : "0"));
        addAndStartBlock(MovingBlockFactory.zeroBranchSignal(datapath, id_ex_latch.controlSignals.zeroBranch ? "1" : "0"));
        addAndStartBlock(MovingBlockFactory.memReadSignal(datapath, id_ex_latch.controlSignals.memRead ? "1" : "0"));
        addAndStartBlock(MovingBlockFactory.memToRegSignal(datapath, id_ex_latch.controlSignals.memToReg ? "1" : "0"));
        addAndStartBlock(MovingBlockFactory.memWriteSignal(datapath, id_ex_latch.controlSignals.memWrite ? "1" : "0"));
        addAndStartBlock(MovingBlockFactory.flagWriteSignal(datapath, id_ex_latch.controlSignals.flagWrite ? "1" : "0"));
        addAndStartBlock(MovingBlockFactory.aluSrcSignal(datapath, id_ex_latch.controlSignals.aluSrc ? "1" : "0"));
        addAndStartBlock(MovingBlockFactory.aluOpSignal(datapath, id_ex_latch.controlSignals.aluOp > 0 ? BitUtils.toBinaryString(id_ex_latch.controlSignals.aluOp, 0, 1) : "0"));
        addAndStartBlock(MovingBlockFactory.regWriteSignal(datapath, id_ex_latch.controlSignals.regWrite ? "1" : "0"));
        
        // Simulate data paths
        int instr = simManager.getSimulator().snapshot.if_id_latch.instructionMachineCode;
        simManager.getSimulator().snapshot.id_ex_latch.print();
        addAndStartBlock(MovingBlockFactory.opcodeToALUControl(datapath, "(32bit)..." + BitUtils.toBinaryString(instr, 0, 7)));
        addAndStartBlock(MovingBlockFactory.readData1ToALU(datapath, "0x" + Long.toHexString(id_ex_latch.readData1)));
        addAndStartBlock(MovingBlockFactory.readData2ToMux(datapath, "0x" + Long.toHexString(id_ex_latch.readData2)));
        addAndStartBlock(MovingBlockFactory.signExtendToMux(datapath, "(64bit)..." +
            BitUtils.toBinaryString(BitUtils.getLow32Bits(BitUtils.signExtend64(instr, 31)), 0 , 7) ));
        addAndStartBlock(MovingBlockFactory.signExtendToShiftLeft(datapath, "(64bit)..." +
            BitUtils.toBinaryString(BitUtils.getLow32Bits(BitUtils.signExtend64(instr, 31)), 0 , 7) ));
        addAndStartBlock(MovingBlockFactory.pcForBranching(datapath, String.format("0x%04X", simManager.getSimulator().cpu.getPC().getValue())));;
        addAndStartBlock(MovingBlockFactory.muxResultToRegDest(datapath, id_ex_latch.controlSignals.reg2Loc ? "0" : "1"));
        addAndStartBlock(MovingBlockFactory.muxResultToALU(datapath, 
            id_ex_latch.controlSignals.aluSrc ? "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(BitUtils.signExtend64(instr, 31)), 0 , 7)
            : Long.toHexString(id_ex_latch.readData2)));
        addAndStartBlock(MovingBlockFactory.shiftResultToAddBranch(datapath, "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(BitUtils.signExtend64(instr, 31) << 2), 0, 7)));
        

        // Simulate And, Or, Logic

        addAndStartBlock(MovingBlockFactory.aluToFlags(datapath, 
            (simManager.getSimulator().cpu.getFlagsRegister().isN() ? "[1]" : "[0]") + 
            (simManager.getSimulator().cpu.getFlagsRegister().isZ() ? "[1]" : "[0]") +
            (simManager.getSimulator().cpu.getFlagsRegister().isC() ? "[1]" : "[0]") +
            (simManager.getSimulator().cpu.getFlagsRegister().isV() ? "[1]" : "[0]")
            ));
        addAndStartBlock(MovingBlockFactory.aluZeroToAndGate(datapath, (simManager.getSimulator().cpu.getFlagsRegister().isZ() ? "[1]" : "[0]")));
        addAndStartBlock(MovingBlockFactory.and1ToOrGate(datapath,
            (simManager.getSimulator().cpu.getFlagsRegister().isN() && id_ex_latch.controlSignals.flagBranch) ||
            (simManager.getSimulator().cpu.getFlagsRegister().isC() && id_ex_latch.controlSignals.flagBranch) ||
            (simManager.getSimulator().cpu.getFlagsRegister().isV() && id_ex_latch.controlSignals.flagBranch) ? 
            "1" : "0"
            ));
        addAndStartBlock(MovingBlockFactory.and2ToOrGate(datapath,
            (simManager.getSimulator().cpu.getFlagsRegister().isZ() && id_ex_latch.controlSignals.zeroBranch) ? "1" : "0"
            ));
        addAndStartBlock(MovingBlockFactory.flagToAndGate(datapath, 
            (simManager.getSimulator().cpu.getFlagsRegister().isN() ? "[1]" : "[0]") + 
            (simManager.getSimulator().cpu.getFlagsRegister().isC() ? "[1]" : "[0]") +
            (simManager.getSimulator().cpu.getFlagsRegister().isV() ? "[1]" : "[0]")
            ));
        addAndStartBlock(MovingBlockFactory.orResultToMux(datapath, 
            (
                (
                    (simManager.getSimulator().cpu.getFlagsRegister().isN() && id_ex_latch.controlSignals.flagBranch) ||
                    (simManager.getSimulator().cpu.getFlagsRegister().isC() && id_ex_latch.controlSignals.flagBranch) ||
                    (simManager.getSimulator().cpu.getFlagsRegister().isV() && id_ex_latch.controlSignals.flagBranch)
                ) 
                || 
                (id_ex_latch.controlSignals.uncondBranch) 
                || 
                (
                    simManager.getSimulator().cpu.getFlagsRegister().isZ() && id_ex_latch.controlSignals.zeroBranch
                )
            ) ? "1" : "0"
            ));
        addAndStartBlock(MovingBlockFactory.aluControlToALU(datapath, BitUtils.toBinaryString(simManager.getSimulator().snapshot.id_ex_latch.controlSignals.aluControl, 0, 3)));
        addAndStartBlock(MovingBlockFactory.branchAdderToMux(datapath,
        "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(BitUtils.signExtend64(instr, 31) + simManager.getSimulator().cpu.getPC().getValue()), 0, 7)));
}
>>>>>>> 1b3341c828bf2b0bd623d9aa89d89e98ba6ddfae

    public void simulateMemoryAccess(Runnable onDone) {
        // clearAllBlocks();
        System.out.println("============== START MEMORY STAGE ============");
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(4);
        var ex_mem_latch = simManager.getSimulator().ex_mem_latch;

<<<<<<< HEAD
        // Code gốc mô phỏng cả hai luồng. Ta sẽ tái tạo lại hành vi đó.
        // Trong thực tế, bạn sẽ dùng if(isStore) hoặc if(isLoad).
        
        AtomicInteger completionCounter = new AtomicInteger(0);
        final int TOTAL_TASKS = 2; // Một cho luồng STORE, một cho luồng LOAD
        Runnable onSubTaskDone = () -> {
            if (completionCounter.incrementAndGet() == TOTAL_TASKS && onDone != null) {
                onDone.run();
            }
        };
        
        // --- Luồng STORE ---
        String address_st = String.valueOf(ex_mem_latch.writeRegisterAddress);
        String data_st = String.valueOf(ex_mem_latch.dataToWriteToMemory);
        List<MovingTextBlock> storeBlocks = List.of(
            MovingBlockFactory.aluToMemoryAddress(datapath, address_st),
            MovingBlockFactory.writeDataToMemory(datapath, data_st)
        );
        runBlocksInParallel(storeBlocks, onSubTaskDone);
        
        // --- Luồng LOAD ---
        String res_ld = "0x1000"; // Lấy từ kết quả ALU
        MovingTextBlock addrToMem_ld = MovingBlockFactory.aluToMemoryAddress(datapath, res_ld);
        
        addrToMem_ld.setOnPathCompleted(() -> {
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.0), e -> {
                String dataRead = "DataFromMem";
                MovingTextBlock dataBlock = MovingBlockFactory.memoryReadToMux(datapath, dataRead);
                dataBlock.setOnPathCompleted(onSubTaskDone); // Khi luồng LOAD hoàn thành
                addAndStartBlock(dataBlock);
            }));
            delay.play();
        });
        addAndStartBlock(addrToMem_ld);
    }

    public void simulateWriteback(Runnable onDone) {
        // clearAllBlocks();
        System.out.println("============== START WRITEBACK STAGE ============");
        
        AtomicInteger completionCounter = new AtomicInteger(0);
        final int TOTAL_TASKS = 2; // Một cho luồng R-type, một cho luồng Load
        Runnable onSubTaskDone = () -> {
            if (completionCounter.incrementAndGet() == TOTAL_TASKS && onDone != null) {
                onDone.run();
            }
        };

        // --- Luồng R-type ---
        String aluResult = "ALU_Res";
        MovingTextBlock aluToMux = MovingBlockFactory.aluToMemMux(datapath, aluResult);
        aluToMux.setOnPathCompleted(() -> {
            MovingTextBlock wbBlock = MovingBlockFactory.writebackToRegisterFile(datapath, aluResult);
            wbBlock.setOnPathCompleted(onSubTaskDone);
            addAndStartBlock(wbBlock);
        });
        addAndStartBlock(aluToMux);

        // --- Luồng Load ---
        String memData = "Mem_Data";
        MovingTextBlock memToMux = MovingBlockFactory.memoryReadToMux(datapath, memData);
        memToMux.setOnPathCompleted(() -> {
            MovingTextBlock wbBlock = MovingBlockFactory.writebackToRegisterFile(datapath, memData);
            wbBlock.setOnPathCompleted(onSubTaskDone);
            addAndStartBlock(wbBlock);
=======
    
    public void simulateMemoryAccess() {
        //this.clearAllBlocks();
        System.out.println(" ============== START MEMORY STAGE ============");
        
        // Lấy id_ex_latch từ SimulationManager
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(4);
        simManager = SimulationManager.getInstance();
        
        var ex_mem_latch = simManager.getSimulator().ex_mem_latch;

        // Giả sử đây là lệnh STORE (SW)
        String address = "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(ex_mem_latch.aluResult), 0, 15); // Lấy từ kết quả ALU
        String data = "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(ex_mem_latch.dataToWriteToMemory), 0, 15); // Lấy từ thanh ghi Read data 2
        addAndStartBlock(MovingBlockFactory.aluToMemoryAddress(datapath, address));
        addAndStartBlock(MovingBlockFactory.writeDataToMemory(datapath, data));
        
        // Giả sử đây là lệnh LOAD (LW)
        // String addr = BitUtils.toBinaryString(BitUtils.getLow32Bits(ex_mem_latch.aluResult), 0, 7); // Lấy từ kết quả ALU
        // addAndStartBlock(MovingBlockFactory.aluToMemoryAddress(datapath, addr));
        // Sau khi địa chỉ đến, memory trả về dữ liệu
        // Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.0), e -> {
        //     String dataRead = "DataFromMem";
        //     addAndStartBlock(MovingBlockFactory.memoryReadToMux(datapath, dataRead));
        // }));
        // delay.play();
    }

    public void simulateWriteback() {
        // this.clearAllBlocks();
        System.out.println(" ============== START WRITE STAGE ============");
        
        // Lấy id_ex_latch từ SimulationManager
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(5);
        simManager = SimulationManager.getInstance();

        var mem_wb_latch = simManager.getSimulator().mem_wb_latch;

        // Giả sử lệnh là R-type (kết quả từ ALU)
        String aluResult ="(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(mem_wb_latch.aluResult), 0, 7);
        MovingTextBlock aluToMux = MovingBlockFactory.aluToMemMux(datapath, aluResult);
        addAndStartBlock(aluToMux);
        
        // Giả sử lệnh là LOAD (kết quả từ Memory)
        String memData = mem_wb_latch.dataReadFromMemory == 0 ? "0" : "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(mem_wb_latch.dataReadFromMemory), 0, 7);
        MovingTextBlock memToMux = MovingBlockFactory.memoryReadToMux(datapath, memData);
        memToMux.setOnPathCompleted(() -> {
            addAndStartBlock(MovingBlockFactory.writebackToRegisterFile(datapath, 
                mem_wb_latch.controlSignals.memToReg ? memData : aluResult));
>>>>>>> 1b3341c828bf2b0bd623d9aa89d89e98ba6ddfae
        });
        addAndStartBlock(memToMux);
    }
    
    public void simulateEndStage(Runnable onDone) {
        // Đây là tên gốc của hàm endStage()
        MovingTextBlock block = MovingBlockFactory.muxResultToPC(datapath, "...");
        block.setOnPathCompleted(onDone);
        addAndStartBlock(block);
    }

    // =======================================================================
    // CÁC PHƯƠNG THỨC TRỢ GIÚP VÀ QUẢN LÝ
    // =======================================================================

    private void runBlocksInParallel(List<MovingTextBlock> blocks, Runnable onAllDone) {
        if (blocks == null || blocks.isEmpty()) {
            if (onAllDone != null) onAllDone.run();
            return;
        }

        AtomicInteger completionCounter = new AtomicInteger(0);
        int totalBlocks = blocks.size();

        Runnable onSingleBlockDone = () -> {
            if (completionCounter.incrementAndGet() == totalBlocks) {
                if (onAllDone != null) {
                    onAllDone.run();
                }
            }
        };

        for (MovingTextBlock block : blocks) {
            block.setOnPathCompleted(onSingleBlockDone);
            addAndStartBlock(block);
        }
    }

    private void addAndStartBlock(MovingTextBlock block) {
        if (block == null) return;
        datapath.getChildren().add(block);
        activeBlocks.add(block);
        block.startMoving();
    }

    private void removeBlock(MovingTextBlock block) {
        block.stopMoving();
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), block);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            datapath.getChildren().remove(block);
            activeBlocks.remove(block);
        });
        fade.play();
    }

    public void clearAllBlocks() {
        List<MovingTextBlock> blocksToRemove = new ArrayList<>(activeBlocks);
        for (MovingTextBlock block : blocksToRemove) {
            removeBlock(block);
        }
        activeBlocks.clear();
        datapath.draw();
    }

    public void pauseAll() {
        for (MovingTextBlock block : activeBlocks) {
            block.pause();
        }
    }

    public void resumeAll() {
        for (MovingTextBlock block : activeBlocks) {
            block.resume();
        }
    }

    public void setAllRates(double rate) {
        for (MovingTextBlock block : activeBlocks) {
            block.setRate(rate);
        }
    }

    public void stepAll() {
        Duration stepDuration = Duration.millis(16);
        for (MovingTextBlock block : activeBlocks) {
            block.step(stepDuration);
        }
    }
}