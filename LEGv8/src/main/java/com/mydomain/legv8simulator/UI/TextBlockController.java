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
import main.java.com.mydomain.legv8simulator.simulator.control.ControlSignals;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import main.java.com.mydomain.legv8simulator.UI.MovingTextBlock.*;


// Text Block Controller
public class TextBlockController extends StackPane{
    private LEGv8Datapath datapath;
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
        simManager = SimulationManager.getInstance();
        simManager.stepSimulation(1); 
        simManager = SimulationManager.getInstance();
        drawPC(datapath.gc,true);
        drawAdd4Block(datapath.gc, true);
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
        int instr = simManager.getMachineCode()[simManager.getCurrentPC()/4];
        drawInstructionMemory(datapath.gc, true);
        MovingTextBlock instruction = new MovingTextBlock(BitUtils.toBinaryString32(instr));
        MovingTextBlock opcode = new MovingTextBlock(BitUtils.toBinaryString(instr,21,31));
        MovingTextBlock rn = new MovingTextBlock(BitUtils.toBinaryString(instr,5,9));
        MovingTextBlock imm = new MovingTextBlock(BitUtils.toBinaryString32(instr));
        MovingTextBlock rd = new MovingTextBlock(BitUtils.toBinaryString(instr,0,4));
        MovingTextBlock rt = new MovingTextBlock(BitUtils.toBinaryString(instr,0,4));
        MovingTextBlock rm = new MovingTextBlock(BitUtils.toBinaryString(instr,16,20));
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
            rdPath.add(new PathSegment(C2_CONTROL - 0.05*width, regY + 0.7 *regHeight, C2_CONTROL - 0.05*width, muxRegInputY + 0.8*muxHeight, regY + 0.7 *regHeight));
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
        drawCompEllipse(datapath.gc, controlX, controlY, controlWidth, controlHeight, controlBorderColor, controlFillColor, true); // Control
        drawControlText(datapath.gc, true);
        simulateReg2Loc("1");
        simulateUncondBranch("1");
        simulateFlagBranch("1");
        simulateZeroBranch("0");
        simulateMemRead("0");
        simulateMemToReg("1");
        simulateMemWrite("1");
        simulateFlagWrite("1");
        simulateALUSrc("0");
        simulateALUOp("0");
        simulateRegWrite("1");
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

    public void simulateReg2Loc(String content) {
    MovingTextBlock reg2Loc = new MovingTextBlock(content, controlBlock);
    List<PathSegment> reg2LocPath = new ArrayList<>();
    drawReg2Loc(datapath.gc, true);
    reg2LocPath.add(new PathSegment(controlX + 0.7 * controlWidth, controlY + 0.05 * controlHeight, controlX + controlWidth, controlY + 0.05 * controlHeight, 0.5));
    reg2LocPath.add(new PathSegment(controlX + controlWidth, controlY + 0.05 * controlHeight,controlX + controlWidth,addBranchY + addBranchHeight * 0.65,0.5));
    reg2LocPath.add(new PathSegment(controlX + controlWidth,addBranchY + addBranchHeight * 0.65, instrMemX + instrMemWidth * 1.15,addBranchY + addBranchHeight * 0.65,0.5));
    reg2LocPath.add(new PathSegment(instrMemX + instrMemWidth * 1.15,addBranchY + addBranchHeight * 0.65,instrMemX + instrMemWidth * 1.15,muxRegInputY + muxHeight * 1.75,0.5));
    reg2LocPath.add(new PathSegment(instrMemX + instrMemWidth * 1.15,muxRegInputY + muxHeight * 1.75,muxRegInputX + 0.5 * muxWidth,muxRegInputY + muxHeight * 1.75,0.5));
    reg2LocPath.add(new PathSegment(muxRegInputX + 0.5 * muxWidth,muxRegInputY + muxHeight * 1.75,muxRegInputX + 0.5 * muxWidth,muxRegInputY + muxHeight, 0.5));
    reg2Loc.setPath(reg2LocPath);
    addAndStartBlock(reg2Loc);
    }
    
    public void simulateUncondBranch(String content) {
        drawUncondBranch(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, controlBlock);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(
            controlX + 0.85 * controlWidth,
            controlY + 0.14 * controlHeight,
            orGateX + 0.1 * gateW,
            controlY + 0.14 * controlHeight,
            0.7
        ));
        block.setPath(path);
        addAndStartBlock(block);
    }

    public void simulateFlagBranch(String content) {
        drawFlagBranch(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, controlBlock);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(
            controlX + 0.9 * controlWidth,
            controlY + 0.23 * controlHeight,
            andGate1X - 0.5 * gateW,
            controlY + 0.23 * controlHeight,
            0.5
        ));
        path.add(new PathSegment(
            andGate1X - 0.5 * gateW,
            controlY + 0.23 * controlHeight,
            andGate1X - 0.5 * gateW,
            andGateY + 0.2 * gateH,
            0.5
        ));
        path.add(new PathSegment(
            andGate1X - 0.5 * gateW,
            andGateY + 0.2 * gateH,
            andGate1X,
            andGateY + 0.2 * gateH,
            0.5
        ));
        block.setPath(path);
        addAndStartBlock(block);
    }

    public void simulateZeroBranch(String content) {
        drawZeroBranch(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, controlBlock);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(
            controlX + 0.95 * controlWidth,
            controlY + 0.32 * controlHeight,
            andGate2X - 0.7 * gateW,
            controlY + 0.32 * controlHeight,
            0.5
        ));
        path.add(new PathSegment(
            andGate2X - 0.7 * gateW,
            controlY + 0.32 * controlHeight,
            andGate2X - 0.7 * gateW,
            andGateY + 0.2 * gateH,
            0.5
        ));
        path.add(new PathSegment(
            andGate2X - 0.7 * gateW,
            andGateY + 0.2 * gateH,
            andGate2X,
            andGateY + 0.2 * gateH,
            0.5
        ));
        block.setPath(path);
        addAndStartBlock(block);
    }

    public void simulateMemRead(String content) {
        drawMemRead(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, controlBlock);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(
            controlX + 0.97 * controlWidth,
            controlY + 0.41 * controlHeight,
            muxMemToRegX + 2.5 * muxWidth,
            controlY + 0.41 * controlHeight,
            0.5
        ));
        path.add(new PathSegment(
            muxMemToRegX + 2.5 * muxWidth,
            controlY + 0.41 * controlHeight,
            muxMemToRegX + 2.5 * muxWidth,
            dataMemY + 1.4 * dataMemHeight,
            0.5
        ));
        path.add(new PathSegment(
            muxMemToRegX + 2.5 * muxWidth,
            dataMemY + 1.4 * dataMemHeight,
            dataMemX + dataMemWidth * 0.5,
            dataMemY + 1.4 * dataMemHeight,
            0.5
        ));
        path.add(new PathSegment(
            dataMemX + dataMemWidth * 0.5,
            dataMemY + 1.4 * dataMemHeight,
            dataMemX + dataMemWidth * 0.5,
            dataMemY + dataMemHeight,
            0.5
        ));
        block.setPath(path);
        addAndStartBlock(block);
    }

    public void simulateMemToReg(String content) {
        drawMemToReg(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, controlBlock);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(
            controlX + 0.99 * controlWidth,
            controlY + 0.5 * controlHeight,
            muxMemToRegX + 0.5 * muxWidth,
            controlY + 0.5 * controlHeight,
            0.5
        ));
        path.add(new PathSegment(
            muxMemToRegX + 0.5 * muxWidth,
            controlY + 0.5 * controlHeight,
            muxMemToRegX + 0.5 * muxWidth,
            muxMemToRegY,
            0.5
        ));
        block.setPath(path);
        addAndStartBlock(block);
    }

    public void simulateMemWrite(String content) {
        drawMemWrite(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, controlBlock);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(
            controlX + 0.97 * controlWidth,
            controlY + 0.59 * controlHeight,
            dataMemX + dataMemWidth * 0.5,
            controlY + 0.59 * controlHeight,
            0.5
        ));
        path.add(new PathSegment(
            dataMemX + dataMemWidth * 0.5,
            controlY + 0.59 * controlHeight,
            dataMemX + dataMemWidth * 0.5,
            dataMemY,
            0.5
        ));
        block.setPath(path);
        addAndStartBlock(block);
    }

    public void simulateFlagWrite(String content) {
        drawFlagWrite(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, controlBlock);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(
            controlX + 0.98 * controlWidth,
            controlY + 0.68 * controlHeight,
            aluX + 0.48 * aluWidth,
            controlY + 0.68 * controlHeight,
            0.5
        ));
        path.add(new PathSegment(
            aluX + 0.48 * aluWidth,
            controlY + 0.68 * controlHeight,
            aluX + 0.48 * aluWidth,
            flagY,
            0.5
        ));
        block.setPath(path);
        addAndStartBlock(block);
    }

    public void simulateALUSrc(String content) {
        drawALUSrc(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, controlBlock);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(
            controlX + 0.93 * controlWidth,
            controlY + 0.77 * controlHeight,
            muxAluInputX + 0.5 * muxWidth,
            controlY + 0.77 * controlHeight,
            0.5
        ));
        path.add(new PathSegment(
            muxAluInputX + 0.5 * muxWidth,
            controlY + 0.77 * controlHeight,
            muxAluInputX + 0.5 * muxWidth,
            muxAluInputY,
            0.5
        ));
        block.setPath(path);
        addAndStartBlock(block);
    }

    public void simulateALUOp(String content) {
        drawALUOp(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, controlBlock);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(
            controlX + 0.83 * controlWidth,
            controlY + 0.86 * controlHeight,
            muxAluInputX - 1.5 * muxWidth,
            controlY + 0.86 * controlHeight,
            0.5
        ));
        path.add(new PathSegment(
            muxAluInputX - 1.5 * muxWidth,
            controlY + 0.86 * controlHeight,
            muxAluInputX - 1.5 * muxWidth,
            aluControlY + 1.7 * aluControlHeight,
            0.5
        ));
        path.add(new PathSegment(
            muxAluInputX - 1.5 * muxWidth,
            aluControlY + 1.7 * aluControlHeight,
            aluControlX + aluControlHeight * 0.5,
            aluControlY + 1.7 * aluControlHeight,
            0.5
        ));
        path.add(new PathSegment(
            aluControlX + aluControlHeight * 0.5,
            aluControlY + 1.7 * aluControlHeight,
            aluControlX + aluControlHeight * 0.5,
            aluControlY + aluControlHeight,
            0.5
        ));
        block.setPath(path);
        addAndStartBlock(block);
    }

    public void simulateRegWrite(String content) {
        drawRegWrite(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, controlBlock);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(
            controlX + 0.7 * controlWidth,
            controlY + 0.95 * controlHeight,
            regX + 0.5 * rectWidth,
            controlY + 0.95 * controlHeight,
            0.5
        ));
        path.add(new PathSegment(
            regX + 0.5 * rectWidth,
            controlY + 0.95 * controlHeight,
            regX + 0.5 * rectWidth,
            regY,
            0.5
        ));
        block.setPath(path);
        addAndStartBlock(block);
    }
}