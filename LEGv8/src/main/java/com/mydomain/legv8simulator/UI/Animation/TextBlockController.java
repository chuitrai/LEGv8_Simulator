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
        simManager.stepSimulation(1); // Chạy logic trước
        simManager = SimulationManager.getInstance();

        MovingTextBlock pcToIMBlock = MovingBlockFactory.pcToInstructionMemory(datapath, adrr);
        MovingTextBlock pcToAdd4Block = MovingBlockFactory.pcToAdd4(datapath, adrr);

        AtomicInteger completionCounter = new AtomicInteger(0);
        final int TOTAL_TASKS = 2; // Một cho pcToIM, một cho luồng PC+4
        Runnable onSubTaskDone = () -> {
            if (completionCounter.incrementAndGet() == TOTAL_TASKS && onDone != null) {
                onDone.run();
            }
        };

        pcToIMBlock.setOnPathCompleted(onSubTaskDone);

        pcToAdd4Block.setOnPathCompleted(() -> {
            MovingTextBlock const4Block = MovingBlockFactory.constant4ToAdd4(datapath);
            const4Block.setOnPathCompleted(() -> {
                String newAddr = String.format("0x%04X", simManager.getSimulator().cpu.getPC().getValue());
                MovingTextBlock resultBlock = MovingBlockFactory.add4ResultToMux(datapath, newAddr);
                resultBlock.setOnPathCompleted(onSubTaskDone); // Luồng PC+4 hoàn thành
                addAndStartBlock(resultBlock);
            });
            addAndStartBlock(const4Block);
        });

        addAndStartBlock(pcToIMBlock);
        addAndStartBlock(pcToAdd4Block);
    }

    public void simulateDecode(Runnable onDone) {
        // this.clearAllBlocks(); // Không clear giữa các giai đoạn trong pipeline
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
                MovingBlockFactory.immediateToSignExtend(BitUtils.toBinaryString(instr, 21, 31) + "...")
            );
            runBlocksInParallel(decodedBlocks, onDone);
        });

        addAndStartBlock(instructionBus);
    }

    public void simulateExecute(Runnable onDone) {
        // this.clearAllBlocks();
        System.out.println("============== START EXECUTE STAGE ============");
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(3);
        simManager = SimulationManager.getInstance();
        var id_ex_latch = simManager.getSimulator().id_ex_latch;

        if (id_ex_latch == null) {
            System.out.println("ID_EX_Latch is null, skipping execute stage.");
            if (onDone != null) onDone.run(); // Vẫn phải gọi onDone để chuỗi tiếp tục
            return;
        }

        drawControlText(datapath.gc, true);
        int instr = simManager.getSimulator().snapshot.if_id_latch.instructionMachineCode;
        id_ex_latch.print();

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
            MovingBlockFactory.aluOpSignal(datapath, id_ex_latch.controlSignals.aluOp > 0 ? BitUtils.toBinaryString(id_ex_latch.controlSignals.aluOp, 0, 1) : "0"),
            MovingBlockFactory.regWriteSignal(datapath, id_ex_latch.controlSignals.regWrite ? "1" : "0"),

            // Data Paths
            MovingBlockFactory.opcodeToALUControl(datapath, "(32bit)..." + BitUtils.toBinaryString(instr, 0, 7)),
            MovingBlockFactory.readData1ToALU(datapath, "0x" + Long.toHexString(id_ex_latch.readData1)),
            MovingBlockFactory.readData2ToMux(datapath, "0x" + Long.toHexString(id_ex_latch.readData2)),
            MovingBlockFactory.signExtendToMux(datapath, "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(BitUtils.signExtend64(instr, 31)), 0, 7)),
            MovingBlockFactory.signExtendToShiftLeft(datapath, "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(BitUtils.signExtend64(instr, 31)), 0, 7)),
            MovingBlockFactory.pcForBranching(datapath, String.format("0x%04X", simManager.getSimulator().cpu.getPC().getValue())),
            MovingBlockFactory.muxResultToRegDest(datapath, id_ex_latch.controlSignals.reg2Loc ? BitUtils.toBinaryString(instr, 0, 4) : BitUtils.toBinaryString(instr, 16, 20)),
            MovingBlockFactory.muxResultToALU(datapath, id_ex_latch.controlSignals.aluSrc ? "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(BitUtils.signExtend64(instr, 31)), 0, 7) : Long.toHexString(id_ex_latch.readData2)),
            MovingBlockFactory.shiftResultToAddBranch(datapath, "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(BitUtils.signExtend64(instr, 31) << 2), 0, 7)),

            // And, Or, Logic
            MovingBlockFactory.aluToFlags(datapath, (simManager.getSimulator().cpu.getFlagsRegister().isN() ? "[1]" : "[0]") + (simManager.getSimulator().cpu.getFlagsRegister().isZ() ? "[1]" : "[0]") + (simManager.getSimulator().cpu.getFlagsRegister().isC() ? "[1]" : "[0]") + (simManager.getSimulator().cpu.getFlagsRegister().isV() ? "[1]" : "[0]")),
            MovingBlockFactory.aluZeroToAndGate(datapath, (simManager.getSimulator().cpu.getFlagsRegister().isZ() ? "[1]" : "[0]")),
            MovingBlockFactory.and1ToOrGate(datapath, ((simManager.getSimulator().cpu.getFlagsRegister().isN() && id_ex_latch.controlSignals.flagBranch) || (simManager.getSimulator().cpu.getFlagsRegister().isC() && id_ex_latch.controlSignals.flagBranch) || (simManager.getSimulator().cpu.getFlagsRegister().isV() && id_ex_latch.controlSignals.flagBranch)) ? "1" : "0"),
            MovingBlockFactory.and2ToOrGate(datapath, (simManager.getSimulator().cpu.getFlagsRegister().isZ() && id_ex_latch.controlSignals.zeroBranch) ? "1" : "0"),
            MovingBlockFactory.flagToAndGate(datapath, (simManager.getSimulator().cpu.getFlagsRegister().isN() ? "[1]" : "[0]") + (simManager.getSimulator().cpu.getFlagsRegister().isC() ? "[1]" : "[0]") + (simManager.getSimulator().cpu.getFlagsRegister().isV() ? "[1]" : "[0]")),
            MovingBlockFactory.orResultToMux(datapath, (((simManager.getSimulator().cpu.getFlagsRegister().isN() && id_ex_latch.controlSignals.flagBranch) || (simManager.getSimulator().cpu.getFlagsRegister().isC() && id_ex_latch.controlSignals.flagBranch) || (simManager.getSimulator().cpu.getFlagsRegister().isV() && id_ex_latch.controlSignals.flagBranch)) || (id_ex_latch.controlSignals.uncondBranch) || (simManager.getSimulator().cpu.getFlagsRegister().isZ() && id_ex_latch.controlSignals.zeroBranch)) ? "1" : "0"),
            MovingBlockFactory.aluControlToALU(datapath, BitUtils.toBinaryString(simManager.getSimulator().snapshot.id_ex_latch.controlSignals.aluControl, 0, 3)),
            MovingBlockFactory.branchAdderToMux(datapath, "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(BitUtils.signExtend64(instr, 31) + simManager.getSimulator().cpu.getPC().getValue()), 0, 7))
        );
        
        runBlocksInParallel(blocksInStage, onDone);
    }

    public void simulateMemoryAccess(Runnable onDone) {
        // this.clearAllBlocks();
        System.out.println("============== START MEMORY STAGE ============");
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(4);
        simManager = SimulationManager.getInstance();
        var ex_mem_latch = simManager.getSimulator().ex_mem_latch;

        // Chỉ mô phỏng STORE vì code gốc đã comment phần LOAD
        String address = "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(ex_mem_latch.aluResult), 0, 15);
        String data = "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(ex_mem_latch.dataToWriteToMemory), 0, 15);
        
        List<MovingTextBlock> blocksInStage = List.of(
            MovingBlockFactory.aluToMemoryAddress(datapath, address),
            MovingBlockFactory.writeDataToMemory(datapath, data)
        );
        
        runBlocksInParallel(blocksInStage, onDone);
    }

    public void simulateWriteback(Runnable onDone) {
        // this.clearAllBlocks();
        System.out.println("============== START WRITE STAGE ============");
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(5);
        simManager = SimulationManager.getInstance();
        var mem_wb_latch = simManager.getSimulator().mem_wb_latch;

        String aluResult = "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(mem_wb_latch.aluResult), 0, 7);
        String memData = mem_wb_latch.dataReadFromMemory == 0 ? "0" : "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(mem_wb_latch.dataReadFromMemory), 0, 7);
        String finalResult = mem_wb_latch.controlSignals.memToReg ? memData : aluResult;

        MovingTextBlock aluToMux = MovingBlockFactory.aluToMemMux(datapath, aluResult);
        MovingTextBlock memToMux = MovingBlockFactory.memoryReadToMux(datapath, memData);

        AtomicInteger completionCounter = new AtomicInteger(0);
        final int TOTAL_TASKS = 2; // Hai khối chạy vào Mux
        Runnable onMuxInputDone = () -> {
            if (completionCounter.incrementAndGet() == TOTAL_TASKS) {
                // Sau khi cả hai vào Mux, khối ghi lại mới bắt đầu
                MovingTextBlock wbBlock = MovingBlockFactory.writebackToRegisterFile(datapath, finalResult);
                wbBlock.setOnPathCompleted(onDone); // Khi khối cuối cùng này xong, giai đoạn kết thúc
                addAndStartBlock(wbBlock);
            }
        };

        aluToMux.setOnPathCompleted(onMuxInputDone);
        memToMux.setOnPathCompleted(onMuxInputDone);

        addAndStartBlock(aluToMux);
        addAndStartBlock(memToMux);
    }

    public void simulateEndStage(Runnable onDone) {
        // this.clearAllBlocks();
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
            if (completionCounter.incrementAndGet() == totalBlocks && onAllDone != null) {
                onAllDone.run();
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