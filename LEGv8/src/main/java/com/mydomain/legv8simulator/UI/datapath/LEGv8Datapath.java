package main.java.com.mydomain.legv8simulator.UI.datapath;

import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import main.java.com.mydomain.legv8simulator.UI.MovingTextBlock;
import main.java.com.mydomain.legv8simulator.core.SimulationManager;

import static main.java.com.mydomain.legv8simulator.UI.datapath.DatapathGraphicsFX.*;

public class LEGv8Datapath extends Pane {

    public Canvas canvas;
    public GraphicsContext gc;
    private Label pcLabel, imLabel, rfLabel, aluLabel, dmLabel;
    
    // Predefined paths cho các loại instruction
    // private Map<String, List<Point>> instructionPaths;

    private SimulationManager simManager;



    

// Hàm cập nhật giá trị các biến layout
public void updateLayoutVars(double width, double height) {
    // Các cột X
    C1_PC_IM     = width * 0.08;
    C2_CONTROL   = width * 0.4;
    C3_REGISTERS = width * 0.45;
    C4_ALU       = width * 0.64;
    C5_DATAMEM   = width * 0.81;
    C6_MUX       = width * 0.925;

    // Các hàng Y
    R_BRANCH_LOGIC = height * 0.18;
    R_CONTROL      = height * 0.35;
    R_MAIN_PATH    = height * 0.55;
    R_SIGN_EXTEND  = height * 0.8;

    // Kích thước các khối
    rectWidth     = width * 0.1;
    rectHeight    = height * 0.2;
    aluWidth      = width * 0.085;
    aluHeight     = height * 0.2;
    pcWidth       = width * 0.035;
    pcHeight      = height * 0.09;
    ellipseWidth  = width * 0.05;
    ellipseHeight = height * 0.07;

    // Vị trí các khối chính
    pcRectX = C1_PC_IM;
    pcRectY = R_MAIN_PATH - pcHeight / 2;
    instrMemX = C1_PC_IM + width * 0.08;
    instrMemY = R_MAIN_PATH - 0.02 * height;
    regX = C3_REGISTERS;
    regY = R_MAIN_PATH - height * 0.01;
    aluX = C4_ALU;
    aluY = R_MAIN_PATH - aluHeight * 0.05;
    dataMemX = C5_DATAMEM;
    dataMemY = R_MAIN_PATH + 0.4 * aluHeight;

    // Vị trí khối logic trên cùng
    add4X = C1_PC_IM + width * 0.13;
    add4Y = R_BRANCH_LOGIC - pcHeight;
    shiftLeft2X = C3_REGISTERS + 0.15 * width;
    shiftLeft2Y = R_BRANCH_LOGIC * 1.1;
    addBranchX = C3_REGISTERS + width * 0.25;
    addBranchY = R_BRANCH_LOGIC - pcHeight * 0.4;

    // Vị trí khối điều khiển và mở rộng
    controlX = C2_CONTROL;
    controlY = R_CONTROL - 0.1 * height;
    signExtendX = C3_REGISTERS + width * 0.02;
    signExtendY = R_SIGN_EXTEND;
    aluControlX = C4_ALU - width * 0.025;
    aluControlY = R_SIGN_EXTEND * 1.02;

    // Flag
    flagSize = width * 0.018;
    flagX = C4_ALU + 0.05 * aluWidth;
    flagY = R_MAIN_PATH - aluHeight * 0.05 - flagSize - height * 0.01;

    // Vị trí MUX
    muxRegInputX = C2_CONTROL - 0.02 * width;
    muxRegInputY = R_MAIN_PATH + 0.03 * height;
    muxAluInputX = C4_ALU - 0.04 * width;
    muxAluInputY = R_MAIN_PATH + 0.11 * height;
    muxPcSourceX = C6_MUX - 0.02 * width;
    muxPcSourceY = R_BRANCH_LOGIC - 0.065 * height;
    muxMemToRegX = C6_MUX;
    muxMemToRegY = R_MAIN_PATH + 0.6 * aluHeight;

    // Kích thước các khối cụ thể
    pcRectWidth = pcWidth;
    pcRectHeight = pcHeight;
    instrMemWidth = rectWidth;
    instrMemHeight = rectHeight;
    regWidth = rectWidth;
    regHeight = rectHeight * 1.16;
    aluBlockWidth = aluWidth;
    aluBlockHeight = aluHeight;
    dataMemWidth = rectWidth * 0.9;
    dataMemHeight = rectHeight * 0.9;
    add4Width = pcWidth;
    add4Height = pcHeight * 0.8;
    shiftLeft2Width = ellipseWidth;
    shiftLeft2Height = ellipseHeight;
    addBranchWidth = pcWidth * 1.5;
    addBranchHeight = pcHeight * 1.2;
    controlWidth = ellipseWidth * 1.2;
    controlHeight = ellipseHeight * 4;
    signExtendWidth = ellipseWidth;
    signExtendHeight = ellipseHeight;
    aluControlWidth = ellipseWidth;
    aluControlHeight = ellipseHeight;
    flagBoxSize = flagSize;
    muxWidth = 0.02 * width;
    muxHeight = 0.09 * height;

    // Các cổng logic
    gateW = width * 0.022;
    gateH = height * 0.028;

    // Vị trí X của các cổng logic
    andGate1X = C4_ALU + aluBlockWidth + width * 0.015;
    andGate2X = andGate1X + gateW + width * 0.045;
    orGateX = andGate2X + gateW*2.5;

    // Vị trí Y của các cổng logic
    andGateY = flagY - 0.3 * gateH;
    orGateY = R_BRANCH_LOGIC + height * 0.1;

     baseFontSize = (int) (width / 90); // 90 là một hằng số tỷ lệ có thể điều chỉnh
    if (baseFontSize < 8) baseFontSize = 8; // Đặt kích thước tối thiểu
    portFontSize = baseFontSize > 11 ? 11 : baseFontSize; // Giới hạn kích thước font cho cổng

}

    public LEGv8Datapath() {
        setupDatapathComponents();
        
        // setupAnimationPaths();
        // initializeAnimations();
    }
    private void setupDatapathComponents() {
        canvas = new Canvas(); 
        gc = canvas.getGraphicsContext2D();
        this.getChildren().add(canvas);

        // Ràng buộc kích thước của Canvas với kích thước của Pane (chính nó)
        // Khi Pane thay đổi kích thước, Canvas cũng sẽ thay đổi theo.
        canvas.widthProperty().bind(this.widthProperty());
        canvas.heightProperty().bind(this.heightProperty());

        // Thêm các Listener để mỗi khi kích thước thay đổi, chúng ta vẽ lại.
        // Điều này đảm bảo sơ đồ luôn được cập nhật.
        canvas.widthProperty().addListener(evt -> draw());
        canvas.heightProperty().addListener(evt -> draw());
    }
    public void draw() {
        // Lấy kích thước hiện tại của canvas

        width = getWidth();
        height = getHeight();

        // Nếu canvas chưa có kích thước thì không vẽ gì cả
        if (width == 0 || height == 0) {
            return;
        }

        gc.clearRect(0, 0, width, height);

        // Thiết lập màu nền
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        updateLayoutVars(width, height);
        // --- VẼ CÁC KHỐI ---
        drawCompRect(gc, pcRectX, pcRectY, pcRectWidth, pcRectHeight, pcBorderColor, pcFillColor, true); // Program Counter
        drawCompRect(gc, instrMemX, instrMemY, instrMemWidth, instrMemHeight, instrMemBorderColor, instrMemFillColor); // Instruction Memory
        drawCompRect(gc, regX, regY, regWidth, regHeight, regBorderColor, regFillColor); // Registers
        drawALU(gc, aluX, aluY, aluBlockWidth, aluBlockHeight, aluBorderColor, aluFillColor, false); // ALU
        drawCompRect(gc, dataMemX, dataMemY, dataMemWidth, dataMemHeight, dataMemBorderColor, dataMemFillColor); // Data Memory

        // --- Khối logic trên cùng ---
        drawALU(gc, add4X, add4Y, add4Width, add4Height, add4BorderColor, add4FillColor, false); // Add 4
        drawCompEllipse(gc, shiftLeft2X, shiftLeft2Y, shiftLeft2Width, shiftLeft2Height, shiftLeft2BorderColor, shiftLeft2FillColor, true); // Shift left 2
        drawALU(gc, addBranchX, addBranchY, addBranchWidth, addBranchHeight, addBranchBorderColor, addBranchFillColor, false); // Add Branch

        // --- Khối điều khiển và mở rộng ---
        drawCompEllipse(gc, controlX, controlY, controlWidth, controlHeight, controlBorderColor, controlFillColor, false); // Control
        drawCompEllipse(gc, signExtendX, signExtendY, signExtendWidth, signExtendHeight, signExtendBorderColor, signExtendFillColor, false); // Sign-extend
        drawCompEllipse(gc, aluControlX, aluControlY, aluControlWidth, aluControlHeight, aluControlBorderColor, aluControlFillColor, false); // ALU Control

        drawFlagBox(gc, "N", flagX, flagY, flagBoxSize, baseFontSize - 2);
        drawFlagBox(gc, "Z", flagX + flagBoxSize, flagY, flagBoxSize, baseFontSize - 2);
        drawFlagBox(gc, "C", flagX + 2 * (flagBoxSize), flagY, flagBoxSize, baseFontSize - 2);
        drawFlagBox(gc, "V", flagX + 3 * (flagBoxSize), flagY, flagBoxSize, baseFontSize - 2);

        // --- VẼ CÁC BỘ MUX ---
        drawMux(gc, muxRegInputX, muxRegInputY, muxWidth, muxHeight, muxBorderColor, muxFillColor, true); // MUX Register Input
        drawMux(gc, muxAluInputX, muxAluInputY, muxWidth, muxHeight, muxBorderColor, muxFillColor, false); // MUX ALU Input
        drawMux(gc, muxPcSourceX, muxPcSourceY, muxWidth, muxHeight, muxBorderColor, muxFillColor, false); // MUX PC Source
        drawMux(gc, muxMemToRegX, muxMemToRegY, muxWidth, muxHeight, muxBorderColor, muxFillColor, false); // MUX MemToReg

        // // ===================================
        // // 3. VẼ CÁC CỔNG LOGIC
        // // ===================================


        // --- Vẽ các cổng ---
        drawAndGateHorizontal(gc, andGate1X, andGateY, gateW, gateH, CONTROL_SIGNAL);
        drawAndGateHorizontal(gc, andGate2X, andGateY, gateW, gateH, CONTROL_SIGNAL);
        drawOrGateHorizontal(gc, orGateX, orGateY, gateW * 1.2, gateH * 1.2);

        // // ===================================
        // // 4. VẼ CÁC ĐƯỜNG DÂY DỮ LIỆU (DATA PATH - BLACK)
        // // ===================================
        drawPCToAdd4(gc, false);
        drawPCToInstructionMemory(gc, false);
        drawPCToAddBranch(gc, false);
        drawAdd4ToMux(gc, false);
        drawMuxToPC(gc, false);
        drawAddBranchToMUX(gc, false);
        drawAdd4(gc, false);
        drawInstrToControl(gc, false);
        drawShiftLeftToAddBranch(gc, false);
        drawInstrToSignExtend(gc, true);
        drawInstrToRegRead1(gc, false);
        drawInstrToMuxReg0(gc, false);
        drawInstrToRegWrite(gc, false);
        drawInstrToMuxReg1(gc, false);
        drawMUXRegToRegRead2(gc, false);
        drawInstrToALUControl(gc, false);
        drawSignExtendToShiftLeft(gc, false);
        drawSignExtendToMuxALU(gc, false);
        drawRegistersToMuxALU(gc, false);
        drawRegistersToDataMemory(gc, false);
        drawRegistersToALU(gc, false);
        drawMuxALUToALU(gc, false);
        drawALUToDataMemory(gc, false);
        drawALUToFlags(gc, false);
        drawALUToMuxMemToReg(gc, false);
        drawDataMemoryToMuxMemToReg(gc, false);
        drawMuxMemToRegToRegisters(gc, false);

        // Control signals
        drawReg2Loc(gc, false);
        drawUncondBranch(gc, false);
        drawFlagBranch(gc, false);
        drawZeroBranch(gc, false);
        drawMemRead(gc, false);
        drawMemToReg(gc, true);
        drawMemWrite(gc, false);
        drawFlagWrite(gc, false);
        drawALUSrc(gc, false);
        drawALUOp(gc, false);
        drawRegWrite(gc, false);
        drawFlagToAndGate(gc, false);
        drawALUControlToALU(gc, false);
        drawALUToAndGate(gc, false);
        drawALUToFlags(gc, false);
        drawAnd1ToOrGate(gc, false);
        drawAnd2ToOrGate(gc, false);
        drawOrGateToMux(gc, false);

        // --- Nhãn cho các khối chính ---
        drawTextBold(gc, "PC", pcRectX + pcRectWidth / 2, pcRectY + pcRectHeight / 2, BLACK, baseFontSize + 2, TextAlignment.CENTER);
        drawTextBold(gc, "Instruction\nmemory", instrMemX + instrMemWidth/2, instrMemY + instrMemHeight * 0.85, BLACK, baseFontSize + 1, TextAlignment.CENTER);
        drawTextBold(gc, "Registers", regX + regWidth*0.45, regY + regHeight * 0.9, BLACK, baseFontSize + 1, TextAlignment.LEFT);
        drawTextBold(gc, "ALU", aluX + aluBlockWidth / 2, aluY + aluBlockHeight / 2, Color.BLACK, baseFontSize + 4, TextAlignment.CENTER);
        drawTextBold(gc, "Data\nmemory", dataMemX + dataMemWidth * 0.7, dataMemY + dataMemHeight * 0.7, BLACK, baseFontSize + 1, TextAlignment.CENTER);

        // --- Nhãn cho các khối phụ ---
        drawTextBold(gc, "Control", controlX + controlWidth / 2, controlY + controlHeight / 2, BLACK, baseFontSize + 4, TextAlignment.CENTER);
        drawTextBold(gc, "Add", add4X + add4Width / 2, add4Y + add4Height / 2, BLACK, baseFontSize, TextAlignment.CENTER);
        drawTextBold(gc, "Shift\nleft 2", shiftLeft2X + shiftLeft2Width / 2, shiftLeft2Y + shiftLeft2Height / 2, BLACK, baseFontSize, TextAlignment.CENTER);
        drawTextBold(gc, "Add", addBranchX + addBranchWidth / 2, addBranchY + addBranchHeight / 2, BLACK, baseFontSize, TextAlignment.CENTER);
        drawTextBold(gc, "Sign-\nextend", signExtendX + signExtendWidth / 2, signExtendY + signExtendHeight / 2, BLACK, baseFontSize, TextAlignment.CENTER);
        drawTextBold(gc, "ALU\ncontrol", aluControlX + aluControlWidth / 2, aluControlY + aluControlHeight / 2, BLACK, baseFontSize, TextAlignment.CENTER);

        // --- Nhãn cho các cổng (Ports) ---
        // --- Nhãn cho các tín hiệu Control ---
        drawControlText(gc, false);

        // --- Nhãn cho các hằng số và thông tin khác ---
        drawText(gc, "4", C1_PC_IM + pcWidth*2.8, add4Y + add4Height*0.85, BLACK, baseFontSize, TextAlignment.RIGHT);

        // Nhãn cho Sign Extend
        drawDiagSlash(gc, signExtendX - 0.35 * signExtendWidth, signExtendY + 0.5 * signExtendHeight, BLACK);
        drawDiagSlash(gc, signExtendX + signExtendWidth + 0.35 * signExtendWidth, signExtendY + 0.5 * signExtendHeight, BLACK);

        drawText(gc, "32", signExtendX - 0.3 * signExtendWidth, signExtendY + 0.3 *signExtendHeight, BLACK, portFontSize, TextAlignment.RIGHT);
        drawText(gc, "64", signExtendX + signExtendWidth + 0.3 * signExtendWidth, signExtendY + 0.3 *signExtendHeight, BLACK, portFontSize, TextAlignment.LEFT);
    }
}

    