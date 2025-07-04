package main.java.com.mydomain.legv8simulator.UI.Animation;
import javafx.animation.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import main.java.com.mydomain.legv8simulator.UI.Animation.MovingTextBlock.*;
import main.java.com.mydomain.legv8simulator.UI.datapath.DatapathGraphicsFX;
import main.java.com.mydomain.legv8simulator.UI.datapath.LEGv8Datapath;
import static main.java.com.mydomain.legv8simulator.UI.datapath.DatapathGraphicsFX.*;
import main.java.com.mydomain.legv8simulator.core.SimulationManager;
import main.java.com.mydomain.legv8simulator.utils.BitUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


// Text Block Controller
public class TextBlockController extends StackPane{
    public LEGv8Datapath datapath;
    private List<MovingTextBlock> activeBlocks;
    private SimulationManager simManager;

    Color controlBlock = Color.web("#e040fb");
    
    public TextBlockController(LEGv8Datapath datapath) {
        this.datapath = datapath;
        this.activeBlocks = new ArrayList<>();
    }
    
    // Trong file TextBlockController.java

    public int getActiveBlockCount() {
        return activeBlocks.size();
    }
    public void simulateFetch() {
        // Lấy địa chỉ PC hiện tại từ SimulationManager
        this.clearAllBlocks();
        System.out.println(" ============== START FETCH STAGE ============");
        simManager = SimulationManager.getInstance();
        String adrr = String.format("0x%04X", simManager.getSimulator().cpu.getPC().getValue());
        System.out.println("initial Address: " + adrr);
        addAndStartBlock(MovingBlockFactory.pcToInstructionMemory(datapath, adrr));

        MovingTextBlock pcToAdd4Block = MovingBlockFactory.pcToAdd4(datapath, adrr);

         simManager.stepSimulation(1);
                simManager= SimulationManager.getInstance();


        pcToAdd4Block.setOnPathCompleted(() -> {
            MovingTextBlock const4Block = MovingBlockFactory.constant4ToAdd4(datapath);
            const4Block.setOnPathCompleted(() -> {
                String newAddr = String.format("0x%04X", simManager.getSimulator().cpu.getPC().getValue());
                addAndStartBlock(MovingBlockFactory.add4ResultToMux(datapath, newAddr));
            });
            addAndStartBlock(const4Block);
        });
        addAndStartBlock(pcToAdd4Block);
    }

    public void simulateDecode() {
        //this.clearAllBlocks();
        System.out.println(" ============== START DECODE STAGE ============");
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(2);
        int instr = simManager.getSimulator().snapshot.if_id_latch.instructionMachineCode;

        MovingTextBlock instructionBus = MovingBlockFactory.instructionBus(datapath, BitUtils.toBinaryString32(instr));

        instructionBus.setOnPathCompleted(() -> {
            addAndStartBlock(MovingBlockFactory.opcodeToControl(BitUtils.toBinaryString(instr, 21, 31)));
            addAndStartBlock(MovingBlockFactory.rnToRegisterFile(BitUtils.toBinaryString(instr, 5, 9)));
            addAndStartBlock(MovingBlockFactory.rmToMuxRegInput(BitUtils.toBinaryString(instr, 16, 20)));
            addAndStartBlock(MovingBlockFactory.rtToRegisterFile(BitUtils.toBinaryString(instr, 0, 4)));
            addAndStartBlock(MovingBlockFactory.rdToMuxWriteReg(BitUtils.toBinaryString(instr, 0, 4)));
            addAndStartBlock(MovingBlockFactory.immediateToSignExtend(BitUtils.toBinaryString(instr, 21, 31) + "..."));
        });

        addAndStartBlock(instructionBus);
    }

    public void simulateExecute() {
        //this.clearAllBlocks();
        System.out.println(" ============== START EXECUTE STAGE ============");
        // Lấy id_ex_latch từ SimulationManager
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(3);
        simManager = SimulationManager.getInstance();
        var id_ex_latch = simManager.getSimulator().id_ex_latch;
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
        });
        addAndStartBlock(memToMux);
    }

    public void endStage()
    {
        addAndStartBlock(MovingBlockFactory.muxResultToPC(datapath, "..."));
    }
    
    private void addAndStartBlock(MovingTextBlock block) {
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
        datapath.draw();
    }

    // Thêm những phương thức này vào trong lớp TextBlockController

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
        // Định nghĩa một bước nhảy nhỏ, ví dụ 16ms ~ 1 frame ở 60fps
        Duration stepDuration = Duration.millis(16);
        for (MovingTextBlock block : activeBlocks) {
            block.step(stepDuration);
        }
    }


}