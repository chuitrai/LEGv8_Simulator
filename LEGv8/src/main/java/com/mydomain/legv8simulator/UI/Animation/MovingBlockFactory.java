package main.java.com.mydomain.legv8simulator.UI.Animation;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import main.java.com.mydomain.legv8simulator.UI.datapath.LEGv8Datapath;

import java.util.ArrayList;
import java.util.List;

import static main.java.com.mydomain.legv8simulator.UI.datapath.DatapathGraphicsFX.*;

/**
 * Factory for creating all MovingTextBlock animations.
 * Each method now also handles drawing the highlighted path.
 */
public class MovingBlockFactory {

    private static final Color DATA_COLOR = Color.web("#3498db");
    private static final Color ADDRESS_COLOR = Color.web("#2ecc71");
    private static final Color CONTROL_COLOR = Color.web("#e040fb");

    // =================================================================
    // == FETCH STAGE
    // =================================================================

    public static MovingTextBlock pcToInstructionMemory(LEGv8Datapath datapath, String address) {
        drawPCToInstructionMemory(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(address, "#3498db");
        block.setPath(List.of(new PathSegment(C1_PC_IM + pcWidth, R_MAIN_PATH, instrMemX - block.getWidth(), R_MAIN_PATH, 1.0)));
        return block;
    }

    public static MovingTextBlock pcToAdd4(LEGv8Datapath datapath, String address) {
        drawPCToAdd4(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(address, "#2ecc71");
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(C1_PC_IM + pcWidth, R_MAIN_PATH, C1_PC_IM + pcWidth * 1.5, R_MAIN_PATH, 1.0));
        path.add(new PathSegment(C1_PC_IM + pcWidth * 1.5, R_MAIN_PATH, C1_PC_IM + pcWidth * 1.5, add4Y + add4Height * 0.25, 1.0));
        path.add(new PathSegment(C1_PC_IM + pcWidth * 1.5, add4Y + add4Height * 0.25, add4X - block.getWidth(), add4Y + add4Height * 0.25, 1.0));
        block.setPath(path);
        return block;
    }
    
    public static MovingTextBlock constant4ToAdd4(LEGv8Datapath datapath) {
        drawAdd4(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(String.format("0x%04X", 4));
        block.setPath(List.of(new PathSegment(C1_PC_IM + pcWidth * 3, R_BRANCH_LOGIC * 0.85, add4X - block.getWidth(), R_BRANCH_LOGIC * 0.85, 1.0)));
        return block;
    }

    public static MovingTextBlock add4ResultToMux(LEGv8Datapath datapath, String newAddress) {
        drawAdd4ToMux(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(newAddress);
        block.setPath(List.of(new PathSegment(add4X + add4Width, add4Y + 0.5 * add4Height, muxPcSourceX - block.getWidth(), add4Y + 0.5 * add4Height, 1.0)));
        return block;
    }

    public static MovingTextBlock pcForBranching(LEGv8Datapath datapath, String data) {
        drawPCToAddBranch(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(data, ADDRESS_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(C1_PC_IM + pcWidth, R_MAIN_PATH, C1_PC_IM + pcWidth * 1.5, R_MAIN_PATH, 0.5));
        path.add(new PathSegment(C1_PC_IM + pcWidth * 1.5, R_MAIN_PATH, C1_PC_IM + pcWidth * 1.5, controlY, 0.5));
        path.add(new PathSegment(C1_PC_IM + pcWidth * 1.5, controlY, add4X + add4Width * 2.5, controlY, 0.5));
        path.add(new PathSegment(add4X + add4Width * 2.5, controlY, add4X + add4Width * 2.5, addBranchY + 0.25 * addBranchHeight, 0.5));
        path.add(new PathSegment(add4X + add4Width * 2.5, addBranchY + 0.25 * addBranchHeight, addBranchX, addBranchY + 0.25 * addBranchHeight, 0.5));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock branchAdderToMux(LEGv8Datapath datapath, String branchAddress) {
        drawAddBranchToMUX(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(branchAddress, ADDRESS_COLOR);
        block.setPath(List.of(new PathSegment(C3_REGISTERS + width*0.25 + pcWidth, R_BRANCH_LOGIC*1.1, C6_MUX - 0.02*width, R_BRANCH_LOGIC*1.1, 1.0)));
        return block;
    }

    public static MovingTextBlock muxResultToPC(LEGv8Datapath datapath, String finalAddress) {
        drawMuxToPC(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(finalAddress, ADDRESS_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(C6_MUX, R_BRANCH_LOGIC*0.9, C6_MUX + 0.04*width, R_BRANCH_LOGIC*0.9, 0.5));
        path.add(new PathSegment(C6_MUX + 0.04*width, R_BRANCH_LOGIC*0.9, C6_MUX + 0.04*width, R_BRANCH_LOGIC*0.3, 0.5));
        path.add(new PathSegment(C6_MUX + 0.04*width, R_BRANCH_LOGIC*0.3, C1_PC_IM - pcWidth*0.5, R_BRANCH_LOGIC*0.3, 0.5));
        path.add(new PathSegment(C1_PC_IM - pcWidth*0.5, R_BRANCH_LOGIC*0.3, C1_PC_IM - pcWidth*0.5, R_MAIN_PATH, 0.5));
        path.add(new PathSegment(C1_PC_IM - pcWidth*0.5, R_MAIN_PATH, C1_PC_IM - block.getWidth(), R_MAIN_PATH, 0.5));
        block.setPath(path);
        return block;
    }


    // =================================================================
    // == DECODE STAGE
    // =================================================================

    public static MovingTextBlock instructionBus(LEGv8Datapath datapath, String instruction) {
        // This is a "bus" so it triggers multiple draw calls
        drawInstrToControl(datapath.gc, true);
        // drawInstrToSignExtend(datapath.gc, true);
        drawInstrToRegRead1(datapath.gc, true);
        drawInstrToMuxReg0(datapath.gc, true);
        drawInstrToMuxReg1(datapath.gc, true);
        drawInstrToRegWrite(datapath.gc, true);
        
        MovingTextBlock block = new MovingTextBlock(instruction);
        block.setPath(List.of(new PathSegment(instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height, instrMemX + instrMemWidth + 0.8 * pcWidth - block.getWidth(), R_MAIN_PATH + 0.07 * height)));
        return block;
    }

    public static MovingTextBlock opcodeToControl(String opcode) {
        MovingTextBlock block = new MovingTextBlock(opcode, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(instrMemX + instrMemWidth + 0.8 * pcWidth, R_MAIN_PATH + 0.07 * height, instrMemX + instrMemWidth + 0.8 * pcWidth, controlY + 0.5 * controlHeight));
        path.add(new PathSegment(instrMemX + instrMemWidth + 0.8 * pcWidth, controlY + 0.5 * controlHeight, C2_CONTROL - block.getWidth(), controlY + 0.5 * controlHeight));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock rnToRegisterFile(String rn) {
         MovingTextBlock block = new MovingTextBlock(rn, ADDRESS_COLOR);
         block.setPath(List.of(new PathSegment(instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.1 * regHeight, C3_REGISTERS,  regY + 0.1 * regHeight)));
         return block;
    }

    public static MovingTextBlock rmToMuxRegInput(String rm) {
        MovingTextBlock block = new MovingTextBlock(rm, ADDRESS_COLOR);
        block.setPath(List.of(new PathSegment(instrMemX + instrMemWidth + 0.8*pcWidth, muxRegInputY + 0.15 * muxHeight, C2_CONTROL - 0.02*width, muxRegInputY + 0.15 * muxHeight)));
        return block;
    }
    
    public static MovingTextBlock rtToRegisterFile(String rt) {
        MovingTextBlock block = new MovingTextBlock(rt, ADDRESS_COLOR);
        block.setPath(List.of(new PathSegment(instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.7 * regHeight, C3_REGISTERS,regY + 0.7 * regHeight)));
        return block;
    }
    
    public static MovingTextBlock rdToMuxWriteReg(String rd) {
        MovingTextBlock block = new MovingTextBlock(rd, ADDRESS_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.7 *regHeight, C2_CONTROL - 0.05*width, regY + 0.7 *regHeight));
        path.add(new PathSegment(C2_CONTROL - 0.05*width, regY + 0.7 *regHeight, C2_CONTROL - 0.05*width, muxRegInputY + 0.8*muxHeight));
        path.add(new PathSegment(C2_CONTROL - 0.05*width, muxRegInputY + 0.8*muxHeight, muxRegInputX,muxRegInputY + 0.8*muxHeight));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock immediateToSignExtend(String immediate) {
        MovingTextBlock block = new MovingTextBlock(immediate, DATA_COLOR);
        block.setPath(List.of(new PathSegment(instrMemX + instrMemWidth + 0.8*pcWidth, signExtendY + 0.5*ellipseHeight, signExtendX - 0.7*signExtendWidth, signExtendY + 0.5*ellipseHeight)));
        return block;
    }

    // =================================================================
    // == EXECUTE STAGE
    // =================================================================
    
    public static MovingTextBlock opcodeToALUControl(LEGv8Datapath datapath, String label) {
        drawInstrToALUControl(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(label, DATA_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(signExtendX - 0.7*signExtendWidth, R_SIGN_EXTEND + 0.5*ellipseHeight, signExtendX - 0.7*signExtendWidth, R_SIGN_EXTEND + 1.5*ellipseHeight));
        path.add(new PathSegment(signExtendX - 0.7*signExtendWidth, R_SIGN_EXTEND + 1.5*ellipseHeight, C4_ALU - width*0.05, R_SIGN_EXTEND + 1.5*ellipseHeight));
        path.add(new PathSegment(C4_ALU - width*0.05, R_SIGN_EXTEND + 1.5*ellipseHeight, C4_ALU - width*0.05, R_SIGN_EXTEND + 0.8*ellipseHeight));
        path.add(new PathSegment(C4_ALU - width*0.05, R_SIGN_EXTEND + 0.8*ellipseHeight, aluControlX,  R_SIGN_EXTEND + 0.8*ellipseHeight));
        block.setPath(path);
        return block;
    }
    
    public static MovingTextBlock readData1ToALU(LEGv8Datapath datapath, String data) {
        drawRegistersToALU(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(data, DATA_COLOR);
        block.setPath(List.of(new PathSegment(regX + rectWidth, R_MAIN_PATH + 0.025 * height, aluX, R_MAIN_PATH + 0.025 * height)));
        return block;
    }
    
    public static MovingTextBlock readData2ToMux(LEGv8Datapath datapath, String data) {
        drawRegistersToMuxALU(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(data, DATA_COLOR);
        block.setPath(List.of(new PathSegment( regX + rectWidth, muxAluInputY + 0.3 * muxHeight, muxAluInputX, muxAluInputY + 0.3 * muxHeight)));
        return block;
    }

    public static MovingTextBlock signExtendToMux(LEGv8Datapath datapath, String data) {
        drawSignExtendToMuxALU(datapath.gc, true);
        MovingTextBlock block =  new MovingTextBlock(data, DATA_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(signExtendX + ellipseWidth, signExtendY + 0.5*ellipseHeight, muxAluInputX - muxWidth, signExtendY + 0.5*ellipseHeight));
        path.add(new PathSegment(muxAluInputX - muxWidth, signExtendY + 0.5*ellipseHeight, muxAluInputX - muxWidth, muxAluInputY + 0.8*muxHeight));
        path.add(new PathSegment(muxAluInputX - muxWidth, muxAluInputY + 0.8*muxHeight, muxAluInputX, muxAluInputY + 0.8*muxHeight));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock signExtendToShiftLeft(LEGv8Datapath datapath, String data) {
        drawSignExtendToShiftLeft(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(data, DATA_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(signExtendX + ellipseWidth, signExtendY + 0.5*ellipseHeight, muxAluInputX - muxWidth, signExtendY + 0.5*ellipseHeight));
        path.add(new PathSegment(muxAluInputX - muxWidth, signExtendY + 0.5*ellipseHeight, muxAluInputX - muxWidth, shiftLeft2Y + 0.5*ellipseHeight));
        path.add(new PathSegment(muxAluInputX - muxWidth, shiftLeft2Y + 0.5*ellipseHeight, shiftLeft2X,shiftLeft2Y + 0.5*ellipseHeight));
        block.setPath(path);
        return block;
    }
    
    public static MovingTextBlock shiftResultToAddBranch(LEGv8Datapath datapath, String data) {
        drawShiftLeftToAddBranch(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(data, DATA_COLOR);
        block.setPath(List.of(new PathSegment(C3_REGISTERS + width*0.15 + ellipseWidth, R_BRANCH_LOGIC*1.3, C3_REGISTERS + width*0.25,  R_BRANCH_LOGIC*1.3)));
        return block;
    }
    
    public static MovingTextBlock muxResultToRegDest(LEGv8Datapath datapath, String data) {
        drawMUXRegToRegRead2(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(data, ADDRESS_COLOR);
        block.setPath(List.of(new PathSegment(C2_CONTROL, R_MAIN_PATH + 0.075 * height, C3_REGISTERS,R_MAIN_PATH + 0.075 * height, 1)));
        return block;
    }

    public static MovingTextBlock muxResultToALU(LEGv8Datapath datapath, String data) {
        drawMuxALUToALU(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(data, DATA_COLOR);
        block.setPath(List.of(new PathSegment(muxAluInputX + muxWidth, muxAluInputY + 0.5 * muxHeight, aluX, muxAluInputY + 0.5 * muxHeight, 1)));
        return block;
    }

    public static MovingTextBlock aluControlToALU(LEGv8Datapath datapath, String label) {
        drawALUControlToALU(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(label, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(aluControlX + aluControlWidth, aluControlY + aluControlHeight * 0.5, aluX + 0.48 * aluWidth, aluControlY + aluControlHeight * 0.5, 0.5));
        path.add(new PathSegment(aluX + 0.48 * aluWidth, aluControlY + aluControlHeight * 0.5, aluX + 0.48 * aluWidth, aluY + 0.88 * aluHeight, 0.5));
        block.setPath(path);
        return block;
    }

    // =================================================================
    // == CONTROL SIGNALS
    // =================================================================

    public static MovingTextBlock reg2LocSignal(LEGv8Datapath datapath, String content) {
        drawReg2Loc(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(controlX + 0.7 * controlWidth, controlY + 0.05 * controlHeight, controlX + controlWidth, controlY + 0.05 * controlHeight, 0.5));
        path.add(new PathSegment(controlX + controlWidth, controlY + 0.05 * controlHeight,controlX + controlWidth,addBranchY + addBranchHeight * 0.65,0.5));
        path.add(new PathSegment(controlX + controlWidth,addBranchY + addBranchHeight * 0.65, instrMemX + instrMemWidth * 1.15,addBranchY + addBranchHeight * 0.65,0.5));
        path.add(new PathSegment(instrMemX + instrMemWidth * 1.15,addBranchY + addBranchHeight * 0.65,instrMemX + instrMemWidth * 1.15,muxRegInputY + muxHeight * 1.75,0.5));
        path.add(new PathSegment(instrMemX + instrMemWidth * 1.15,muxRegInputY + muxHeight * 1.75,muxRegInputX + 0.5 * muxWidth,muxRegInputY + muxHeight * 1.75,0.5));
        path.add(new PathSegment(muxRegInputX + 0.5 * muxWidth,muxRegInputY + muxHeight * 1.75,muxRegInputX + 0.5 * muxWidth,muxRegInputY + muxHeight, 0.5));
        block.setPath(path);
        return block;
    }
    
    public static MovingTextBlock uncondBranchSignal(LEGv8Datapath datapath, String content) {
        drawUncondBranch(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, CONTROL_COLOR);
        block.setPath(List.of(new PathSegment(controlX + 0.85 * controlWidth, controlY + 0.14 * controlHeight, orGateX + 0.1 * gateW, controlY + 0.14 * controlHeight, 0.7)));
        return block;
    }

    public static MovingTextBlock flagBranchSignal(LEGv8Datapath datapath, String content) {
        drawFlagBranch(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(controlX + 0.9 * controlWidth, controlY + 0.23 * controlHeight, andGate1X - 0.5 * gateW, controlY + 0.23 * controlHeight, 0.5));
        path.add(new PathSegment(andGate1X - 0.5 * gateW, controlY + 0.23 * controlHeight, andGate1X - 0.5 * gateW, andGateY + 0.2 * gateH, 0.5));
        path.add(new PathSegment(andGate1X - 0.5 * gateW, andGateY + 0.2 * gateH, andGate1X, andGateY + 0.2 * gateH, 0.5));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock zeroBranchSignal(LEGv8Datapath datapath, String content) {
        drawZeroBranch(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(controlX + 0.95 * controlWidth, controlY + 0.32 * controlHeight, andGate2X - 0.7 * gateW, controlY + 0.32 * controlHeight, 0.5));
        path.add(new PathSegment(andGate2X - 0.7 * gateW, controlY + 0.32 * controlHeight, andGate2X - 0.7 * gateW, andGateY + 0.2 * gateH, 0.5));
        path.add(new PathSegment(andGate2X - 0.7 * gateW, andGateY + 0.2 * gateH, andGate2X, andGateY + 0.2 * gateH, 0.5));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock memReadSignal(LEGv8Datapath datapath, String content) {
        drawMemRead(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(controlX + 0.97 * controlWidth, controlY + 0.41 * controlHeight, muxMemToRegX + 2.5 * muxWidth, controlY + 0.41 * controlHeight, 0.5));
        path.add(new PathSegment(muxMemToRegX + 2.5 * muxWidth, controlY + 0.41 * controlHeight, muxMemToRegX + 2.5 * muxWidth, dataMemY + 1.4 * dataMemHeight, 0.5));
        path.add(new PathSegment(muxMemToRegX + 2.5 * muxWidth, dataMemY + 1.4 * dataMemHeight, dataMemX + dataMemWidth * 0.5, dataMemY + 1.4 * dataMemHeight, 0.5));
        path.add(new PathSegment(dataMemX + dataMemWidth * 0.5, dataMemY + 1.4 * dataMemHeight, dataMemX + dataMemWidth * 0.5, dataMemY + dataMemHeight, 0.5));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock memToRegSignal(LEGv8Datapath datapath, String content) {
        drawMemToReg(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(controlX + 0.99 * controlWidth, controlY + 0.5 * controlHeight, muxMemToRegX + 0.5 * muxWidth, controlY + 0.5 * controlHeight, 0.5));
        path.add(new PathSegment(muxMemToRegX + 0.5 * muxWidth, controlY + 0.5 * controlHeight, muxMemToRegX + 0.5 * muxWidth, muxMemToRegY, 0.5));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock memWriteSignal(LEGv8Datapath datapath, String content) {
        drawMemWrite(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(controlX + 0.97 * controlWidth, controlY + 0.59 * controlHeight, dataMemX + dataMemWidth * 0.5, controlY + 0.59 * controlHeight, 0.5));
        path.add(new PathSegment(dataMemX + dataMemWidth * 0.5, controlY + 0.59 * controlHeight, dataMemX + dataMemWidth * 0.5, dataMemY, 0.5));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock flagWriteSignal(LEGv8Datapath datapath, String content) {
        drawFlagWrite(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(controlX + 0.98 * controlWidth, controlY + 0.68 * controlHeight, aluX + 0.48 * aluWidth, controlY + 0.68 * controlHeight, 0.5));
        path.add(new PathSegment(aluX + 0.48 * aluWidth, controlY + 0.68 * controlHeight, aluX + 0.48 * aluWidth, flagY, 0.5));
        block.setPath(path);
        return block;
    }
    
    public static MovingTextBlock aluSrcSignal(LEGv8Datapath datapath, String content) {
        drawALUSrc(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(controlX + 0.93 * controlWidth, controlY + 0.77 * controlHeight, muxAluInputX + 0.5 * muxWidth, controlY + 0.77 * controlHeight, 0.5));
        path.add(new PathSegment(muxAluInputX + 0.5 * muxWidth, controlY + 0.77 * controlHeight, muxAluInputX + 0.5 * muxWidth, muxAluInputY, 0.5));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock aluOpSignal(LEGv8Datapath datapath, String content) {
        drawALUOp(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(controlX + 0.83 * controlWidth, controlY + 0.86 * controlHeight, muxAluInputX - 1.5 * muxWidth, controlY + 0.86 * controlHeight, 0.5));
        path.add(new PathSegment(muxAluInputX - 1.5 * muxWidth, controlY + 0.86 * controlHeight, muxAluInputX - 1.5 * muxWidth, aluControlY + 1.7 * aluControlHeight, 0.5));
        path.add(new PathSegment(muxAluInputX - 1.5 * muxWidth, aluControlY + 1.7 * aluControlHeight, aluControlX + aluControlHeight * 0.5, aluControlY + 1.7 * aluControlHeight, 0.5));
        path.add(new PathSegment(aluControlX + aluControlHeight * 0.5, aluControlY + 1.7 * aluControlHeight, aluControlX + aluControlHeight * 0.5, aluControlY + aluControlHeight, 0.5));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock regWriteSignal(LEGv8Datapath datapath, String content) {
        drawRegWrite(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(content, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(controlX + 0.7 * controlWidth, controlY + 0.95 * controlHeight, regX + 0.5 * rectWidth, controlY + 0.95 * controlHeight, 0.5));
        path.add(new PathSegment(regX + 0.5 * rectWidth, controlY + 0.95 * controlHeight, regX + 0.5 * rectWidth, regY, 0.5));
        block.setPath(path);
        return block;
    }
    
    // =================================================================
    // == LOGIC GATES AND BRANCHING
    // =================================================================
    
    public static MovingTextBlock aluToFlags(LEGv8Datapath datapath, String label) {
        drawALUToFlags(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(label, CONTROL_COLOR);
        block.setPath(List.of(new PathSegment(aluX + 0.48 * aluWidth, aluY + 0.12 * aluHeight, aluX + 0.48 * aluWidth, flagY + flagBoxSize, 0.7)));
        return block;
    }

    public static MovingTextBlock aluZeroToAndGate(LEGv8Datapath datapath, String label) {
        drawALUToAndGate(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(label, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(aluX + aluWidth, aluY + 0.4 * aluBlockHeight, andGate2X - 0.7 * gateW, aluY + 0.4 * aluBlockHeight, 0.5));
        path.add(new PathSegment(andGate2X - 0.7 * gateW, aluY + 0.4 * aluBlockHeight, andGate2X - 0.7 * gateW, andGateY + 0.8 * gateH, 0.5));
        path.add(new PathSegment(andGate2X - 0.7 * gateW, andGateY + 0.8 * gateH, andGate2X, andGateY + 0.8 * gateH, 0.5));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock and1ToOrGate(LEGv8Datapath datapath, String label) {
        drawAnd1ToOrGate(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(label, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(andGate1X + 0.9 * gateW, andGateY + 0.5 * gateH, andGate2X - 1.2 * gateW, andGateY + 0.5 * gateH, 0.5));
        path.add(new PathSegment(andGate2X - 1.2 * gateW, andGateY + 0.5 * gateH, andGate2X - 1.2 * gateW, orGateY + 0.65 * gateH, 0.5));
        path.add(new PathSegment(andGate2X - 1.2 * gateW, orGateY + 0.65 * gateH, orGateX + 0.2 * gateW, orGateY + 0.65 * gateH, 0.5));
        block.setPath(path);
        return block;
    }
    
    public static MovingTextBlock and2ToOrGate(LEGv8Datapath datapath, String label) {
        drawAnd2ToOrGate(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(label, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(andGate2X + 0.95 * gateW, andGateY + 0.5 * gateH, orGateX - 0.8 * gateW, andGateY + 0.5 * gateH, 0.5));
        path.add(new PathSegment(orGateX - 0.8 * gateW, andGateY + 0.5 * gateH, orGateX - 0.8 * gateW, orGateY + 0.95 * gateH, 0.5));
        path.add(new PathSegment(orGateX - 0.8 * gateW, orGateY + 0.95 * gateH, orGateX + 0.1 * gateW, orGateY + 0.95 * gateH, 0.5));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock orResultToMux(LEGv8Datapath datapath, String label) {
        drawOrGateToMux(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(label, CONTROL_COLOR);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(orGateX + 1.2 * gateW, orGateY + 0.6 * gateH, muxPcSourceX + 0.5 * muxWidth, orGateY + 0.6 * gateH, 0.5));
        path.add(new PathSegment(muxPcSourceX + 0.5 * muxWidth, orGateY + 0.6 * gateH, muxPcSourceX + 0.5 * muxWidth, muxPcSourceY + muxHeight, 0.5));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock flagToAndGate(LEGv8Datapath datapath, String label) {
        drawFlagToAndGate(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(label, CONTROL_COLOR);
        block.setPath(List.of(new PathSegment(flagX + 4 * flagBoxSize, flagY + 0.5 * flagBoxSize, andGate1X, flagY + 0.5 * flagBoxSize, 0.5)));
        return block;
    }

    // =================================================================
    // == MEMORY ACCESS STAGE
    // =================================================================
    
    public static MovingTextBlock aluToMemoryAddress(LEGv8Datapath datapath, String address) {
        drawALUToDataMemory(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(address);
        block.setPath(List.of(new PathSegment(C4_ALU + aluWidth, aluY + 0.6 * aluHeight, dataMemX - block.getWidth(), aluY + 0.6 * aluHeight, 1.0)));
        return block;
    }

    public static MovingTextBlock writeDataToMemory(LEGv8Datapath datapath, String data) {
        drawRegistersToDataMemory(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(data);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(regX + rectWidth, muxAluInputY + 0.3 * muxHeight, regX + rectWidth * 1.1, muxAluInputY + 0.3 * muxHeight, 0.5));
        path.add(new PathSegment(regX + rectWidth * 1.1, muxAluInputY + 0.3 * muxHeight, regX + rectWidth * 1.1, dataMemY + 0.8 * dataMemHeight, 0.5));
        path.add(new PathSegment(regX + rectWidth * 1.1, dataMemY + 0.8 * dataMemHeight, dataMemX - block.getWidth(), dataMemY + 0.8 * dataMemHeight, 0.5));
        block.setPath(path);
        return block;
    }

    public static MovingTextBlock memoryReadToMux(LEGv8Datapath datapath, String data) {
        drawDataMemoryToMuxMemToReg(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(data);
        block.setPath(List.of(new PathSegment(dataMemX + dataMemWidth, muxMemToRegY + 0.2 * muxHeight, muxMemToRegX - block.getWidth(), muxMemToRegY + 0.2 * muxHeight, 1.0)));
        return block;
    }


    // =================================================================
    // == WRITE-BACK STAGE
    // =================================================================
    
    public static MovingTextBlock aluToMemMux(LEGv8Datapath datapath, String aluResult) {
        drawALUToMuxMemToReg(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(aluResult);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(aluX + aluWidth, aluY + 0.6 * aluHeight, dataMemX - 0.25 * dataMemWidth, aluY + 0.6 * aluHeight, 0.5));
        path.add(new PathSegment(dataMemX - 0.25 * dataMemWidth, aluY + 0.6 * aluHeight, dataMemX - 0.25 * dataMemWidth, dataMemY + dataMemHeight * 1.2, 0.5));
        path.add(new PathSegment(dataMemX - 0.25 * dataMemWidth, dataMemY + dataMemHeight * 1.2, muxMemToRegX - 0.8 * muxWidth, dataMemY + dataMemHeight * 1.2, 0.5));
        path.add(new PathSegment(muxMemToRegX - 0.8 * muxWidth, dataMemY + dataMemHeight * 1.2, muxMemToRegX - 0.8 * muxWidth, muxMemToRegY + 0.8 * muxHeight, 0.5));
        path.add(new PathSegment(muxMemToRegX - 0.8 * muxWidth, muxMemToRegY + 0.8 * muxHeight, muxMemToRegX - block.getWidth(), muxMemToRegY + 0.8 * muxHeight, 0.5));
        block.setPath(path);
        return block;
    }
    
    public static MovingTextBlock writebackToRegisterFile(LEGv8Datapath datapath, String finalData) {
        drawMuxMemToRegToRegisters(datapath.gc, true);
        MovingTextBlock block = new MovingTextBlock(finalData);
        List<PathSegment> path = new ArrayList<>();
        path.add(new PathSegment(muxMemToRegX + muxWidth, muxMemToRegY + 0.5 * muxHeight, muxMemToRegX + muxWidth * 1.8, muxMemToRegY + 0.5 * muxHeight, 0.5));
        path.add(new PathSegment(muxMemToRegX + muxWidth * 1.8, muxMemToRegY + 0.5 * muxHeight, muxMemToRegX + muxWidth * 1.8, aluControlY + aluControlHeight * 2, 0.5));
        path.add(new PathSegment(muxMemToRegX + muxWidth * 1.8, aluControlY + aluControlHeight * 2, regX - 0.3 * rectWidth, aluControlY + aluControlHeight * 2, 0.5));
        path.add(new PathSegment(regX - rectWidth * 0.3, aluControlY + aluControlHeight * 2, regX - rectWidth * 0.3, regY + 0.9 * regHeight, 0.5));
        path.add(new PathSegment(regX - rectWidth * 0.3, regY + 0.9 * regHeight, regX - block.getWidth(), regY + 0.9 * regHeight, 0.5));
        block.setPath(path);
        return block;
    }

}