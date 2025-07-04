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

        AtomicInteger completionCounter = new AtomicInteger(0);
        Runnable onSubTaskDone = () -> {
            if (completionCounter.incrementAndGet() == 2 && onDone != null) {
                onDone.run();
            }
        };

        pcToIMBlock.setOnPathCompleted(onSubTaskDone);

        pcToAdd4Block.setOnPathCompleted(() -> {
            MovingTextBlock const4Block = MovingBlockFactory.constant4ToAdd4(datapath);
            const4Block.setOnPathCompleted(() -> {
                simManager.stepSimulation(1);
                simManager = SimulationManager.getInstance();
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

    public void simulateDecode(Runnable onDone) {
        // clearAllBlocks();
        System.out.println("============== START DECODE STAGE ============");
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(2);
        int instr = simManager.getSimulator().snapshot.if_id_latch.instructionMachineCode;

        MovingTextBlock instructionBus = MovingBlockFactory.instructionBus(datapath, BitUtils.toBinaryString32(instr));

        instructionBus.setOnPathCompleted(() -> {
            List<MovingTextBlock> decodedBlocks = List.of(
                MovingBlockFactory.opcodeToControl(BitUtils.toBinaryString(instr, 21, 31)),
                MovingBlockFactory.rnToRegisterFile(BitUtils.toBinaryString(instr, 5, 9)),
                MovingBlockFactory.rmToMuxRegInput(BitUtils.toBinaryString(instr, 16, 20)),
                MovingBlockFactory.rtToRegisterFile(BitUtils.toBinaryString(instr, 0, 4)),
                MovingBlockFactory.rdToMuxWriteReg(BitUtils.toBinaryString(instr, 0, 4)),
                MovingBlockFactory.immediateToSignExtend(BitUtils.toBinaryString32(instr))
            );
            runBlocksInParallel(decodedBlocks, onDone);
        });

        addAndStartBlock(instructionBus);
    }

    public void simulateExecute(Runnable onDone) {
        // clearAllBlocks();
        System.out.println("============== START EXECUTE STAGE ============");
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(3);
        var id_ex_latch = simManager.getSimulator().id_ex_latch;
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

    public void simulateMemoryAccess(Runnable onDone) {
        // clearAllBlocks();
        System.out.println("============== START MEMORY STAGE ============");
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(4);
        var ex_mem_latch = simManager.getSimulator().ex_mem_latch;

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