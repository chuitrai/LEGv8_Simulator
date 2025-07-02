package main.java.com.mydomain.legv8simulator.UI;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.com.mydomain.legv8simulator.UI.datapath.LEGv8Datapath;
import static main.java.com.mydomain.legv8simulator.UI.datapath.DatapathGraphicsFX.*;
import main.java.com.mydomain.legv8simulator.core.SimulationManager;
import main.java.com.mydomain.legv8simulator.utils.BitUtils;
import main.java.com.mydomain.legv8simulator.gui.DatapathSnapshot;
import main.java.com.mydomain.legv8simulator.instruction.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import main.java.com.mydomain.legv8simulator.UI.MovingTextBlock.*;


// Text Block Controller
public class TextBlockController extends StackPane{
    private LEGv8Datapath datapath;
    private List<MovingTextBlock> activeBlocks;
    private SimulationManager simManager;
    
    public TextBlockController(LEGv8Datapath datapath) {
        this.datapath = datapath;
        this.activeBlocks = new ArrayList<>();
    }
    
    // Trong file TextBlockController.java

    public int getActiveBlockCount() {
        return activeBlocks.size();
    }
    public void simulateFetch() {
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(1); 
        simManager = SimulationManager.getInstance();
        drawPCToAdd4(datapath.gc, true);
        drawPCToInstructionMemory(datapath.gc, true);
        String adrr = String.format("0x%04X", simManager.getCpu().getPC().getValue());
        MovingTextBlock block1 = new MovingTextBlock(adrr, "#3498db");
        MovingTextBlock block2 = new MovingTextBlock(adrr, "#2ecc71");

        // Định nghĩa path: PC -> Instruction Memory -> Control Unit
        List<PathSegment> fetchPath1 = new ArrayList<>();
        fetchPath1.add(new PathSegment(C1_PC_IM + pcWidth, R_MAIN_PATH, instrMemX - block1.getWidth(), R_MAIN_PATH, 1.0));  // PC to Instruction Memory
        block1.setPath(fetchPath1);

        addAndStartBlock(block1);


        // Định nghĩa path: PC -> Instruction Memory -> Control Unit
        List<PathSegment> fetchPath2 = new ArrayList<>();
        fetchPath2.add(new PathSegment(C1_PC_IM + pcWidth, R_MAIN_PATH, C1_PC_IM + pcWidth*1.5, R_MAIN_PATH, 1.0));  // PC to Instruction Memory
        fetchPath2.add(new PathSegment(C1_PC_IM + pcWidth*1.5, R_MAIN_PATH, C1_PC_IM + pcWidth*1.5, add4Y + add4Height*0.25, 1.0));   // To Control Unit
        fetchPath2.add(new PathSegment(C1_PC_IM + pcWidth*1.5, add4Y + add4Height*0.25, add4X - block2.getWidth(), add4Y + add4Height*0.25, 1.0));  // Instruction Memory to decode
        block2.setPath(fetchPath2);

        // Gắn callback khi block2 kết thúc
        block2.setOnPathCompleted(() -> {
            MovingTextBlock pcBlock = new MovingTextBlock(String.format("0x%04X", 4));
            PathSegment pcSegment = new PathSegment(C1_PC_IM + pcWidth*3, R_BRANCH_LOGIC*0.85, add4X - pcBlock.getWidth(), R_BRANCH_LOGIC*0.85, 1.0);
            pcBlock.setPath(List.of(pcSegment));
            drawAdd4(datapath.gc, true);
            pcBlock.setOnPathCompleted(() -> {
                String newAddr = String.format("0x%04X", simManager.getCpu().getPC().getValue());
                MovingTextBlock block3 = new MovingTextBlock(newAddr);
                List<PathSegment> fetchPath3 = new ArrayList<>();
                fetchPath3.add(new PathSegment(add4X + add4Width, add4Y + 0.5*add4Height, muxPcSourceX - block3.getWidth(), add4Y + 0.5*add4Height, 1.0));
                block3.setPath(fetchPath3);
                addAndStartBlock(block3);
            });
            drawAdd4ToMux(datapath.gc, true);
            addAndStartBlock(pcBlock);
           
        });
        addAndStartBlock(block2);
    }
    public void simulateDecode() {
        simManager = SimulationManager.getInstance();
        // DatapathSnapshot snapshot = simManager.getSimulator().createSnapshot();
        // Instruction instr = snapshot.id_ex_latch.instruction;
        MovingTextBlock instruction = new MovingTextBlock(BitUtils.toBinaryString32(24));
        MovingTextBlock opcode = new MovingTextBlock("instr.getOpcodeMnemonic()");
        MovingTextBlock rn = new MovingTextBlock(BitUtils.toBinaryString(24,5,9));
        MovingTextBlock imm = new MovingTextBlock(BitUtils.toBinaryString32(24));
        MovingTextBlock rd = new MovingTextBlock(BitUtils.toBinaryString(24,0,4));
        MovingTextBlock rt = new MovingTextBlock(BitUtils.toBinaryString(24,0,4));
        MovingTextBlock rm = new MovingTextBlock(BitUtils.toBinaryString(24,16,20));
        drawInstrToControl(datapath.gc, true);
        drawInstrToRegRead1(datapath.gc, true);
        drawInstrToMuxReg0(datapath.gc, true);
        drawInstrToMuxReg1(datapath.gc, true);
        drawInstrToRegWrite(datapath.gc, true);
        drawInstrToSignExtend(datapath.gc, true);
        PathSegment instrPath = new PathSegment(instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth - instruction.getWidth(), R_MAIN_PATH + 0.07 * height);
        instruction.setPath(List.of(instrPath));

        instruction.setOnPathCompleted(() -> {
            List<PathSegment> opcodePath = new ArrayList<>();
            opcodePath.add(new PathSegment(instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, instrMemX + instrMemWidth + 0.8*pcWidth, controlY + 0.5 *controlHeight));
            opcodePath.add(new PathSegment(instrMemX + instrMemWidth + 0.8*pcWidth, controlY + 0.5 *controlHeight, C2_CONTROL - opcode.getWidth(), controlY + 0.5 *controlHeight)); 
            opcode.setPath(opcodePath);
            addAndStartBlock(opcode);

            List<PathSegment> rnPath = new ArrayList<>();
            rnPath.add(new PathSegment(instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.1 * regHeight, C3_REGISTERS - rn.getWidth(),  regY + 0.1 * regHeight));
            rn.setPath(rnPath);
            addAndStartBlock(rn);

            List<PathSegment> rmPath = new ArrayList<>();
            rmPath.add(new PathSegment(instrMemX + instrMemWidth + 0.8*pcWidth, muxRegInputY + 0.15 * muxHeight, C2_CONTROL - 0.02*width - rm.getWidth(), muxRegInputY + 0.15 * muxHeight));
            rm.setPath(rmPath);
            addAndStartBlock(rm);

            List<PathSegment> rtPath = new ArrayList<>();
            rtPath.add(new PathSegment(instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.7 * regHeight, C3_REGISTERS - rt.getWidth(),regY + 0.7 * regHeight));
            rt.setPath(rtPath);
            addAndStartBlock(rt);

            List<PathSegment> rdPath = new ArrayList<>();
            rdPath.add(new PathSegment(instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.7 *regHeight, C2_CONTROL - 0.05*width, regY + 0.7 *regHeight));
            rdPath.add(new PathSegment(C2_CONTROL - 0.05*width, regY + 0.7 *regHeight, C2_CONTROL - 0.05*width, muxRegInputY + 0.8*muxHeight));
            rdPath.add(new PathSegment(C2_CONTROL - 0.05*width, muxRegInputY + 0.8*muxHeight, muxRegInputX - rd.getWidth(),muxRegInputY + 0.8*muxHeight));
            rd.setPath(rdPath);
            addAndStartBlock(rd);
            
            List<PathSegment> immPath = new ArrayList<>();
            immPath.add(new PathSegment(instrMemX + instrMemWidth + 0.8*pcWidth, signExtendY + 0.5*ellipseHeight, signExtendX - imm.getWidth(), signExtendY + 0.5*ellipseHeight)); // Shift Left 2 to Add Branch
            imm.setPath(immPath);
            addAndStartBlock(imm);
        });
        addAndStartBlock(instruction);
    }
    
    public void simulateExecute() {
        MovingTextBlock data1 = new MovingTextBlock("0x1234", "#27ae60");
        MovingTextBlock data2 = new MovingTextBlock("0x5678", "#27ae60");
        
        // Data paths: Register File -> MUX -> ALU
        List<PathSegment> dataPath1 = new ArrayList<>();
        dataPath1.add(new PathSegment(290, 350, 350, 230, 1.0));  // Register to MUX
        dataPath1.add(new PathSegment(350, 230, 600, 310, 1.0));  // MUX to ALU
        data1.setPath(dataPath1);
        
        List<PathSegment> dataPath2 = new ArrayList<>();
        dataPath2.add(new PathSegment(290, 380, 350, 230, 1.2));  // Register to MUX
        dataPath2.add(new PathSegment(350, 230, 600, 330, 1.0));  // MUX to ALU
        data2.setPath(dataPath2);
        
        addAndStartBlock(data1);
        addAndStartBlock(data2);
        
        // Result xuất hiện sau khi inputs đến ALU
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2.5), e -> {
            MovingTextBlock result = new MovingTextBlock("0x68AC", "#27ae60");
            List<PathSegment> resultPath = new ArrayList<>();
            resultPath.add(new PathSegment(650, 320, 750, 300, 1.0));  // ALU to output
            result.setPath(resultPath);
            addAndStartBlock(result);
        }));
        delay.play();
    }
    
    public void simulateMemoryAccess() {
        MovingTextBlock address = new MovingTextBlock("0x1000", "#9b59b6");
        MovingTextBlock data = new MovingTextBlock("Data", "#9b59b6");
        
        // Address path: ALU -> Data Memory
        List<PathSegment> addressPath = new ArrayList<>();
        addressPath.add(new PathSegment(700, 320, 750, 300, 1.0));
        addressPath.add(new PathSegment(750, 300, 820, 290, 0.8));
        address.setPath(addressPath);
        
        // Data path: Memory -> Register File (writeback)
        List<PathSegment> dataPath = new ArrayList<>();
        dataPath.add(new PathSegment(820, 320, 820, 400, 1.0));
        dataPath.add(new PathSegment(820, 400, 500, 400, 1.5));
        dataPath.add(new PathSegment(500, 400, 220, 400, 1.0));
        data.setPath(dataPath);
        
        addAndStartBlock(address);
        
        // Data xuất hiện sau khi address đến Memory
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.8), e -> {
            addAndStartBlock(data);
        }));
        delay.play();
    }
    
    public void simulateWriteback() {
        MovingTextBlock result = new MovingTextBlock("Final Result", "#f39c12");
        
        // Writeback path: Memory/ALU -> Register File
        List<PathSegment> writebackPath = new ArrayList<>();
        writebackPath.add(new PathSegment(800, 320, 600, 350, 1.0));
        writebackPath.add(new PathSegment(600, 350, 500, 380, 1.0));
        writebackPath.add(new PathSegment(500, 380, 290, 380, 1.5));
        result.setPath(writebackPath);
        
        addAndStartBlock(result);
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