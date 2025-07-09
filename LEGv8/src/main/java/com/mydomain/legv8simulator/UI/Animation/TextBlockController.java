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

import static main.java.com.mydomain.legv8simulator.UI.datapath.DatapathGraphicsFX.*;

public class TextBlockController extends StackPane {

    public LEGv8Datapath datapath;
    public List<MovingTextBlock> activeBlocks;

    private SimulationManager simManager;
    private double currentRate = 1.0; // Tốc độ mặc định là 1x

    private boolean isN = false;
    private boolean isZ = false;
    private boolean isC = false;
    private boolean isV = false;

    public static List<MovingTextBlock> FetchBlocks = new ArrayList<>();
    public static List<MovingTextBlock> DecodeBlocks = new ArrayList<>();
    public static List<MovingTextBlock> ExecuteBlocks = new ArrayList<>();
    public static List<MovingTextBlock> MemoryBlocks = new ArrayList<>();
    public static List<MovingTextBlock> WritebackBlocks = new ArrayList<>();

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
    FetchBlocks.clear();
    DecodeBlocks.clear();
    ExecuteBlocks.clear();
    MemoryBlocks.clear();
    WritebackBlocks.clear();
        System.out.println("============== START FETCH STAGE ============");
    drawPC(datapath.gc, true);
    simManager = SimulationManager.getInstance();
    String adrr = String.format("0x%04X", simManager.getSimulator().cpu.getPC().getValue());
    simManager.stepSimulation(1); // Chạy logic trước
    simManager = SimulationManager.getInstance();

    MovingTextBlock pcToIMBlock = MovingBlockFactory.pcToInstructionMemory(datapath, adrr);
    MovingTextBlock pcToAdd4Block = MovingBlockFactory.pcToAdd4(datapath, adrr);

    // Thêm vào FetchBlocks
    FetchBlocks.add(pcToIMBlock);
    FetchBlocks.add(pcToAdd4Block);

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
        drawAdd4Block(datapath.gc, true);
        // Thêm vào FetchBlocks
        FetchBlocks.add(const4Block);

        const4Block.setOnPathCompleted(() -> {
            String newAddr = String.format("0x%04X", simManager.getSimulator().cpu.getPC().getValue());
            MovingTextBlock resultBlock = MovingBlockFactory.add4ResultToMux(datapath, newAddr);
            // Thêm vào FetchBlocks
            FetchBlocks.add(resultBlock);

            resultBlock.setOnPathCompleted(onSubTaskDone); // Luồng PC+4 hoàn thành
            addAndStartBlock(resultBlock);
        });
        addAndStartBlock(const4Block);
    });

    addAndStartBlock(pcToIMBlock);
    addAndStartBlock(pcToAdd4Block);
}

public void simulateDecode(Runnable onDone) {
    System.out.println("============== START DECODE STAGE ============");
    simManager = SimulationManager.getInstance();
    simManager.stepSimulation(2);
    int instr = simManager.getSimulator().snapshot.if_id_latch.instructionMachineCode;
    drawInstructionMemory(datapath.gc, true);
    MovingTextBlock instructionBus = MovingBlockFactory.instructionBus(datapath, BitUtils.toBinaryString32(instr));

    // Thêm vào DecodeBlocks
    DecodeBlocks.add(instructionBus);

    instructionBus.setOnPathCompleted(() -> {
        List<MovingTextBlock> decodedBlocks = List.of(
            MovingBlockFactory.opcodeToControl(BitUtils.toBinaryString(instr, 21, 31)),
            MovingBlockFactory.rnToRegisterFile(BitUtils.toBinaryString(instr, 5, 9)),
            MovingBlockFactory.rmToMuxRegInput(BitUtils.toBinaryString(instr, 16, 20)),
            MovingBlockFactory.rtToRegisterFile(BitUtils.toBinaryString(instr, 0, 4)),
            MovingBlockFactory.rdToMuxWriteReg(BitUtils.toBinaryString(instr, 0, 4)),
            MovingBlockFactory.immediateToSignExtend(BitUtils.toBinaryString(instr, 21, 31) + "...")
        );
        // Thêm tất cả vào DecodeBlocks
        DecodeBlocks.addAll(decodedBlocks);
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
            MovingBlockFactory.branchAdderToMux(datapath, "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(BitUtils.signExtend64(instr, 31) + simManager.getSimulator().cpu.getPC().getValue()), 0, 7)),
            MovingBlockFactory.muxResultToPC(datapath, 
            (((simManager.getSimulator().cpu.getFlagsRegister().isN() && id_ex_latch.controlSignals.flagBranch) 
            || (simManager.getSimulator().cpu.getFlagsRegister().isC() && id_ex_latch.controlSignals.flagBranch) 
            || (simManager.getSimulator().cpu.getFlagsRegister().isV() && id_ex_latch.controlSignals.flagBranch)) 
            || (id_ex_latch.controlSignals.uncondBranch) 
            || (simManager.getSimulator().cpu.getFlagsRegister().isZ() && id_ex_latch.controlSignals.zeroBranch)) ?  
            "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(BitUtils.signExtend64(instr, 31) + simManager.getSimulator().if_id_latch.pcIncremented), 0, 7) 
            : String.format("0x%04X", simManager.getSimulator().cpu.getPC().getValue()))
        );
        // Thêm tất cả vào ExecuteBlocks
        ExecuteBlocks.addAll(blocksInStage);
        runBlocksInParallel(blocksInStage, onDone);
        
        drawSignExtend(datapath.gc, true);
        drawAluControl(datapath.gc, true);
        drawMux(datapath.gc, muxRegInputX, muxRegInputY, muxWidth, muxHeight, id_ex_latch.controlSignals.reg2Loc ? false : true, id_ex_latch.controlSignals.reg2Loc ? true : false); // MUX Register Input
        if(id_ex_latch.controlSignals.aluSrc) {
            drawMux(datapath.gc,muxAluInputX, muxAluInputY, muxWidth, muxHeight, false, true); // MUX ALU Source
        } else {
            drawMux(datapath.gc, muxAluInputX, muxAluInputY, muxWidth, muxHeight, true, false); // MUX ALU Source
        }
        if(id_ex_latch.controlSignals.flagWrite) {
            isN = simManager.getSimulator().cpu.getFlagsRegister().isN();
            isZ = simManager.getSimulator().cpu.getFlagsRegister().isZ();
            isC = simManager.getSimulator().cpu.getFlagsRegister().isC();
            isV = simManager.getSimulator().cpu.getFlagsRegister().isV();
        }
        drawFlagBox(datapath.gc, "N", flagX, flagY, flagBoxSize, baseFontSize - 2, isN);
        drawFlagBox(datapath.gc, "Z", flagX + flagBoxSize, flagY, flagBoxSize, baseFontSize - 2, isZ);
        drawFlagBox(datapath.gc, "C", flagX + 2 * (flagBoxSize), flagY, flagBoxSize, baseFontSize - 2, isC);
        drawFlagBox(datapath.gc, "V", flagX + 3 * (flagBoxSize), flagY, flagBoxSize, baseFontSize - 2, isV);
        drawALUBlock(datapath.gc, true);
        drawShiftLeft2(datapath.gc, true);
        drawBranchAdder(datapath.gc, true);
        if(((simManager.getSimulator().cpu.getFlagsRegister().isN() && id_ex_latch.controlSignals.flagBranch) 
            || (simManager.getSimulator().cpu.getFlagsRegister().isC() && id_ex_latch.controlSignals.flagBranch) 
            || (simManager.getSimulator().cpu.getFlagsRegister().isV() && id_ex_latch.controlSignals.flagBranch)) 
            || (id_ex_latch.controlSignals.uncondBranch) 
            || (simManager.getSimulator().cpu.getFlagsRegister().isZ() && id_ex_latch.controlSignals.zeroBranch))
        {
        drawMux(datapath.gc, muxPcSourceX, muxPcSourceY, muxWidth, muxHeight, false, true); // MUX PC Source
        } else {
        drawMux(datapath.gc, muxPcSourceX, muxPcSourceY, muxWidth, muxHeight, true, false); // MUX PC Source
        }

    }

public void simulateMemoryAccess(Runnable onDone) {
    System.out.println("============== START MEMORY STAGE ============");
    simManager = SimulationManager.getInstance();
    simManager.stepSimulation(4);
    simManager = SimulationManager.getInstance();
    var ex_mem_latch = simManager.getSimulator().ex_mem_latch;

    drawDataMemory(datapath.gc, true);
    String address = "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(ex_mem_latch.aluResult), 0, 15);
    String data = "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(ex_mem_latch.dataToWriteToMemory), 0, 15);
    
    List<MovingTextBlock> blocksInStage = List.of(
        MovingBlockFactory.aluToMemoryAddress(datapath, address),
        MovingBlockFactory.writeDataToMemory(datapath, data)
    );
    // Thêm tất cả vào MemoryBlocks
    MemoryBlocks.addAll(blocksInStage);

    runBlocksInParallel(blocksInStage, onDone);
}

public void simulateWriteback(Runnable onDone) {
    System.out.println("============== START WRITE STAGE ============");
    simManager = SimulationManager.getInstance();
    simManager.stepSimulation(5);
    simManager = SimulationManager.getInstance();
    var mem_wb_latch = simManager.getSimulator().mem_wb_latch;
    var id_ex_latch = simManager.getSimulator().id_ex_latch;
    if(id_ex_latch.controlSignals.memToReg)
    {
        drawMemMux(datapath.gc, muxMemToRegX, muxMemToRegY, muxWidth, muxHeight, true, false);
    }
    else
    {
        drawMemMux(datapath.gc, muxMemToRegX, muxMemToRegY, muxWidth, muxHeight, false, true);
    }
    String aluResult = "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(mem_wb_latch.aluResult), 0, 7);
    String memData = mem_wb_latch.dataReadFromMemory == 0 ? "0" : "(64bit)..." + BitUtils.toBinaryString(BitUtils.getLow32Bits(mem_wb_latch.dataReadFromMemory), 0, 7);
    String finalResult = mem_wb_latch.controlSignals.memToReg ? memData : aluResult;

    MovingTextBlock aluToMux = MovingBlockFactory.aluToMemMux(datapath, aluResult);
    MovingTextBlock memToMux = MovingBlockFactory.memoryReadToMux(datapath, memData);

    // Thêm vào WritebackBlocks
    WritebackBlocks.add(aluToMux);
    WritebackBlocks.add(memToMux);

    AtomicInteger completionCounter = new AtomicInteger(0);
    final int TOTAL_TASKS = 2;
    Runnable onMuxInputDone = () -> {
        if (completionCounter.incrementAndGet() == TOTAL_TASKS) {
            MovingTextBlock wbBlock = MovingBlockFactory.writebackToRegisterFile(datapath, finalResult);
            // Thêm vào WritebackBlocks
            WritebackBlocks.add(wbBlock);

            wbBlock.setOnPathCompleted(onDone);
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

    public void runBlocksInParallel(List<MovingTextBlock> blocks, Runnable onAllDone) {
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
        block.setRate(currentRate); // <-- Đảm bảo block mới cũng nhận đúng tốc độ hiện tại!
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

    public void clearAllBlocksInStage(List<MovingTextBlock> stageBlocks) {
        if (stageBlocks == null || stageBlocks.isEmpty()) {
            return;
        }
        
        for (MovingTextBlock block : stageBlocks) {
            // Dừng animation của block
            block.stopMoving();
            
            // Tạo fade out effect
            FadeTransition fade = new FadeTransition(Duration.seconds(0.5), block);
            fade.setToValue(0);
            fade.setOnFinished(e -> {
                // Xóa khỏi UI và activeBlocks nhưng KHÔNG xóa khỏi stageBlocks
                datapath.getChildren().remove(block);
                activeBlocks.remove(block);
            });
            fade.play();
        }
        
        // Không clear stageBlocks - chỉ ẩn hiển thị thôi
        // stageBlocks.clear(); // <- KHÔNG làm điều này!
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
        this.currentRate = rate;
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

    public static List<Point> getAllBlockEndPoints(List<MovingTextBlock> blocks) {
        List<Point> endPoints = new ArrayList<>();
        for (MovingTextBlock block : blocks) {
            List<PathSegment> path = block.getPath();
            if (path != null && !path.isEmpty()) {
                PathSegment last = path.get(path.size() - 1);
                endPoints.add(last.end);
            }
        }
        return endPoints;
    }
    public static void moveBlocksToEndpoint(List<MovingTextBlock> blocks) {
        for (MovingTextBlock block : blocks) {
            List<PathSegment> path = block.getPath();
            if (path != null && !path.isEmpty()) {
                PathSegment last = path.get(path.size() - 1);
                block.setTranslateX(last.end.x);
                block.setTranslateY(last.end.y);
            }
        }
    }

    
}