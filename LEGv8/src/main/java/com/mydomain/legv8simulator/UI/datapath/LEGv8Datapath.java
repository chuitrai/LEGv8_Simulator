package main.java.com.mydomain.legv8simulator.UI.datapath;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import static main.java.com.mydomain.legv8simulator.UI.datapath.DatapathGraphicsFX.*;


public class LEGv8Datapath extends Pane {

    private Canvas canvas;
    private GraphicsContext gc;

    private double width;
    private double height;

    // Trong class LEGv8Datapath

    // --- Biến toàn cục cho layout ---
    private double C1_PC_IM, C2_CONTROL, C3_REGISTERS, C4_ALU, C5_DATAMEM, C6_MUX;
    private double R_BRANCH_LOGIC, R_CONTROL, R_MAIN_PATH, R_SIGN_EXTEND;
    private double rectWidth, rectHeight, aluWidth, aluHeight, pcWidth, pcHeight, ellipseWidth, ellipseHeight;
    private double pcRectX, pcRectY, instrMemX, instrMemY, regX, regY, aluX, aluY, dataMemX, dataMemY;
    private double add4X, add4Y, shiftLeft2X, shiftLeft2Y, addBranchX, addBranchY;
    private double controlX, controlY, signExtendX, signExtendY, aluControlX, aluControlY;
    private double flagSize, flagX, flagY;
    private double muxRegInputX, muxRegInputY, muxAluInputX, muxAluInputY, muxPcSourceX, muxPcSourceY, muxMemToRegX, muxMemToRegY;
    private double pcRectWidth, pcRectHeight, instrMemWidth, instrMemHeight, regWidth, regHeight, aluBlockWidth, aluBlockHeight;
    private double dataMemWidth, dataMemHeight, add4Width, add4Height, shiftLeft2Width, shiftLeft2Height, addBranchWidth, addBranchHeight;
    private double controlWidth, controlHeight, signExtendWidth, signExtendHeight, aluControlWidth, aluControlHeight, flagBoxSize;
    private double muxWidth, muxHeight;
    private double gateW, gateH;
    private double andGate1X, andGate2X, orGateX;
    private double andGateY, orGateY;

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
}

    public LEGv8Datapath() {
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

        
    // Kích thước font chữ co giãn
    int baseFontSize = (int) (width / 90); // 90 là một hằng số tỷ lệ có thể điều chỉnh
    if (baseFontSize < 8) baseFontSize = 8; // Đặt kích thước tối thiểu

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
        drawInstrToSignExtend(gc, false);
        drawInstrToRegRead1(gc, false);
        drawInstrToMuxReg0(gc, false);
        drawInstrToMuxReg1(gc, false);
        drawInstrToRegWrite(gc, false);
        drawMUXRegToRegRead2(gc, false);
        drawInstrToALUControl(gc, false);
        drawSignExtendToShiftLeft(gc, false);
        drawSignExtendToMuxALU(gc, false);
        drawRegistersToMuxALU(gc, false);
        drawRegistersToDataMemory(gc, false);
        drawRegistersToALU(gc, false);
        drawMuxALUToALU(gc, false);
        // drawALUToDataMemory(gc, false);
        // drawALUToFlags(gc, false);
        drawALUToMuxMemToReg(gc, false);
        drawDataMemoryToMuxMemToReg(gc, false);
        drawMuxMemToRegToRegisters(gc, false);

        // Control signals
        drawReg2Loc(gc, false);
        drawUncondBranch(gc, false);
        drawFlagBranch(gc, false);
        drawZeroBranch(gc, false);
        drawMemRead(gc, false);
        drawMemToReg(gc, false);
        drawMemWrite(gc, false);
        drawFlagWrite(gc, false);
        drawALUSrc(gc, false);
        drawALUOp(gc, false);
        drawRegWrite(gc, false);
        drawFlagToAndGate(gc, false);
        drawALUToALUControl(gc, false);
        drawALUToAndGate(gc, false);
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

        // Instruction Memory Ports
        drawText(gc, "Read \naddress", instrMemX + 0.05 * instrMemWidth, instrMemY + 0.1 * instrMemHeight, BLACK, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Instruction\n [31 - 0]", instrMemX + 0.95 * instrMemWidth,  instrMemY + 0.45 * instrMemHeight, BLACK, portFontSize, TextAlignment.RIGHT);

        // --- Nhãn cho các đường dây Instruction ---
        drawText(gc, "Instruction [31 - 21]", instrMemX + instrMemWidth + 0.9*pcWidth, controlY + controlHeight*0.45, BLACK, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Instruction [20 - 16]", instrMemX + instrMemWidth + 0.9*pcWidth, muxRegInputY + 0.05* muxHeight, BLACK, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Instruction [9 - 5]", instrMemX + instrMemWidth + 0.9*pcWidth, regY + 0.05 * regHeight, BLACK, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Instruction [4 - 0]", instrMemX + instrMemWidth + 0.9*pcWidth, regY + 0.75 *regHeight, BLACK, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Instruction [31 - 0]", instrMemX + instrMemWidth + 0.9*pcWidth, signExtendY + signExtendHeight * 0.4, BLACK, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Instruction [31 - 21]", signExtendX - 0.65*signExtendWidth, R_SIGN_EXTEND + 1.35*ellipseHeight, BLACK, portFontSize, TextAlignment.LEFT);

        // Register Ports
        drawText(gc, "Read\nregister 1", regX + 0.05 * regWidth, regY + 0.1 * regHeight, BLACK, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Read\nregister 2", regX + 0.05 * regWidth, regY + 0.35 * regHeight, BLACK, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Write\nregister", regX + 0.05 * regWidth, regY + 0.7 * regHeight, BLACK, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Write\ndata", regX + 0.05 * regWidth, regY + 0.9 * regHeight, BLACK, portFontSize, TextAlignment.LEFT);

        drawText(gc, "Read\ndata 1", regX + regWidth*0.95, aluY + 0.2 * aluBlockHeight, BLACK, portFontSize, TextAlignment.RIGHT);
        drawText(gc, "Read\ndata 2", regX + regWidth *0.9  + 5, muxAluInputY + 0.3 * muxHeight, BLACK, portFontSize, TextAlignment.RIGHT);
        
        // ALU Ports
        drawText(gc, "Zero", aluX + aluBlockWidth*0.95, aluY + aluBlockHeight*0.4, CONTROL_SIGNAL, portFontSize, TextAlignment.RIGHT);

        // Data Memory Ports
        drawText(gc, "Address", dataMemX + 0.05 * dataMemWidth, dataMemY + dataMemHeight * 0.18, BLACK, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Read\ndata", dataMemX + dataMemWidth * 0.95, dataMemY + dataMemHeight * 0.35, BLACK, portFontSize, TextAlignment.RIGHT);
        drawText(gc, "Write\ndata", dataMemX + dataMemWidth * 0.05, dataMemY + dataMemHeight * 0.8, BLACK, portFontSize, TextAlignment.LEFT);

        // --- Nhãn cho các tín hiệu Control ---
        String[] controlSignals = {"Reg2Loc", "UncondBranch", "FlagBranch", "ZeroBranch", "MemRead", "MemToReg", "MemWrite", "FlagWrite", "ALUSrc", "ALUOp", "RegWrite"};
        for (int i = 0; i < controlSignals.length; i++) {
            double yPos = controlY + controlHeight * (0.02 + i * 0.09) ; // Rải đều các nhãn
            drawText(gc, controlSignals[i], controlX + controlWidth*1.1, yPos, CONTROL_SIGNAL, portFontSize-1, TextAlignment.LEFT);
        }

        // --- Nhãn cho các hằng số và thông tin khác ---
        drawText(gc, "4", C1_PC_IM + pcWidth*2.8, add4Y + add4Height*0.85, BLACK, baseFontSize, TextAlignment.RIGHT);

        // Nhãn cho Sign Extend
        drawDiagSlash(gc, signExtendX - 0.35 * signExtendWidth, signExtendY + 0.5 * signExtendHeight, BLACK);
        drawDiagSlash(gc, signExtendX + signExtendWidth + 0.35 * signExtendWidth, signExtendY + 0.5 * signExtendHeight, BLACK);

        drawText(gc, "32", signExtendX - 0.3 * signExtendWidth, signExtendY + 0.3 *signExtendHeight, BLACK, portFontSize, TextAlignment.RIGHT);
        drawText(gc, "64", signExtendX + signExtendWidth + 0.3 * signExtendWidth, signExtendY + 0.3 *signExtendHeight, BLACK, portFontSize, TextAlignment.LEFT);
    }

    public void drawPCToAdd4(GraphicsContext gc, boolean highlight) {
        drawVerticalSegment(gc, C1_PC_IM + pcWidth*1.5, R_MAIN_PATH, add4Y + add4Height*0.25, highlight? Highlight :BLACK, false, false); 
        drawRightArrow(gc, C1_PC_IM + pcWidth*1.5, add4Y + add4Height*0.25, add4X, highlight? Highlight :BLACK, false); 
        drawHorizontalSegment(gc, C1_PC_IM + pcWidth, R_MAIN_PATH, C1_PC_IM + pcWidth*1.5, highlight ? Highlight :BLACK, false, true);     
        }
    public void drawPCToInstructionMemory(GraphicsContext gc, boolean highlight) {
        drawRightArrow(gc, C1_PC_IM + pcWidth, R_MAIN_PATH, instrMemX, highlight ? Highlight : BLACK, false); // PC to Instruction Memory
    }

    public void drawPCToAddBranch(GraphicsContext gc, boolean highlight) {
        drawHorizontalSegment(gc, C1_PC_IM + pcWidth, R_MAIN_PATH, C1_PC_IM + pcWidth*1.5, highlight ? Highlight :BLACK, false, true);     
        drawVerticalSegment(gc, C1_PC_IM + pcWidth*1.5, R_MAIN_PATH, controlY, highlight? Highlight :BLACK, false, true); 
        drawHorizontalSegment(gc, C1_PC_IM + pcWidth*1.5, controlY, add4X + add4Width*2.5, highlight ? Highlight :BLACK, true, isCache());
        drawVerticalSegment(gc, add4X + add4Width*2.5, controlY, addBranchY + 0.25*addBranchHeight, highlight ? Highlight :BLACK, isDisable(), isCache());
        drawRightArrow(gc, add4X + add4Width*2.5, addBranchY + 0.25*addBranchHeight, addBranchX, highlight ? Highlight :BLACK, false); // PC to Shift Left 2
    }
    public void drawAdd4ToMux(GraphicsContext gc, boolean highlight) {
        drawRightArrow(gc, add4X + add4Width, add4Y + 0.5*add4Height, muxPcSourceX, highlight ? Highlight :BLACK, false); // Add 4 to MUX
    }

    public void drawMuxToPC(GraphicsContext gc, boolean highlight) {

        drawRightArrow(gc, C1_PC_IM - pcWidth*0.5, R_MAIN_PATH, C1_PC_IM, highlight ? Highlight :BLACK, false); // To PC
        drawVerticalSegment(gc, C1_PC_IM - pcWidth*0.5, R_MAIN_PATH, R_BRANCH_LOGIC*0.3,  highlight ? Highlight :BLACK, false, false); 
        drawHorizontalSegment(gc, C1_PC_IM - pcWidth*0.5, R_BRANCH_LOGIC*0.3, C6_MUX + 0.04*width, highlight ? Highlight :BLACK, false, false);
        drawVerticalSegment(gc, C6_MUX + 0.04*width, R_BRANCH_LOGIC*0.3, R_BRANCH_LOGIC*0.9, highlight ? Highlight :BLACK, false, false); // MUX to PC
        drawHorizontalSegment(gc, C6_MUX + 0.04*width, R_BRANCH_LOGIC*0.9, C6_MUX, highlight ? Highlight :BLACK, false, false); // MUX to PC
    }

    public void drawAddBranchToMUX(GraphicsContext gc, boolean highlight) {
        drawRightArrow(gc, C3_REGISTERS + width*0.25 + pcWidth*1.5, R_BRANCH_LOGIC*1.1, C6_MUX - 0.02*width, highlight ? Highlight :BLACK, false); // Add Branch to MUX
    }

    public void drawAdd4(GraphicsContext gc, boolean highlight) {
        drawRightArrow(gc, C1_PC_IM + pcWidth*3, R_BRANCH_LOGIC*0.85, add4X, highlight ? Highlight : BLACK, false); // Add 4
    }

    public void drawInstrToControl(GraphicsContext gc, boolean highlight) {

        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight :BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, controlY + 0.5 *controlHeight, highlight ? Highlight :BLACK, true, false); 
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, controlY + 0.5 *controlHeight, C2_CONTROL, highlight ? Highlight :BLACK, false); // Instruction Memory to Control
    }
    
    public void drawShiftLeftToAddBranch(GraphicsContext gc, boolean highlight) {
        drawRightArrow(gc, C3_REGISTERS + width*0.15 + ellipseWidth, R_BRANCH_LOGIC*1.3, C3_REGISTERS + width*0.25, highlight ? Highlight : BLACK, false); // Shift Left 2 to Add Branch
    }
    public void drawInstrToSignExtend(GraphicsContext gc, boolean highlight) {
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight :BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, signExtendY + 0.5*ellipseHeight, highlight ? Highlight :BLACK, true, false); 
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, signExtendY + 0.5*ellipseHeight, signExtendX, highlight ? Highlight : BLACK, false); // Shift Left 2 to Add Branch
    }
    public void drawInstrToRegRead1(GraphicsContext gc, boolean highlight) {
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight : BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, regY + 0.1 * regHeight, highlight ? Highlight : BLACK, true, false); 
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.1 * regHeight, C3_REGISTERS, highlight ? Highlight : BLACK, true); // Instruction Memory to Registers
    }

    public void drawInstrToMuxReg0(GraphicsContext gc, boolean highlight)
    {
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight : BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, muxRegInputY + 0.15 * muxHeight, highlight ? Highlight : BLACK, true, false); 
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, muxRegInputY + 0.15 * muxHeight, C2_CONTROL - 0.02*width, highlight ? Highlight : BLACK, true); // Instruction Memory to MUX Register Input
    }

    public void drawInstrToMuxReg1(GraphicsContext gc, boolean highlight)
    {
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight : BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, regY + 0.7 *regHeight, highlight ? Highlight : BLACK, true, false); 
        drawHorizontalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.7 *regHeight, C2_CONTROL - 0.05*width, highlight ? Highlight : BLACK, false,true); // Instruction Memory to Sign Extend
        drawVerticalSegment(gc, C2_CONTROL - 0.05*width, regY + 0.7 *regHeight, muxRegInputY + 0.8*muxHeight, highlight ? Highlight : BLACK, true, false); // MUX Register Input to Register Input
        drawRightArrow(gc, C2_CONTROL - 0.05*width, muxRegInputY + 0.8*muxHeight, muxRegInputX , highlight ? Highlight : BLACK, false); // Instruction Memory to MUX Register Input
    }

    public void drawInstrToRegWrite(GraphicsContext gc, boolean highlight) {
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight : BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, regY + 0.7 * regHeight, highlight ? Highlight : BLACK, true, false); 
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.7 * regHeight, C3_REGISTERS, highlight ? Highlight : BLACK, true); // Instruction Memory to Registers
    }

    public void drawMUXRegToRegRead2(GraphicsContext gc, boolean highlight){
        drawRightArrow(gc, C2_CONTROL, R_MAIN_PATH + 0.075 * height, C3_REGISTERS, highlight? Highlight : BLACK, false); // MUX Register Input to Registers
    }

    public void drawInstrToALUControl(GraphicsContext gc, boolean highlight)
    {
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight :BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, signExtendY + 0.5*ellipseHeight, highlight ? Highlight :BLACK, true, false); 
        drawHorizontalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, signExtendY + 0.5*ellipseHeight, signExtendX - 0.7*signExtendWidth, highlight ? Highlight : BLACK, false, true); // Shift Left 2 to Add Branch
        drawVerticalSegment(gc, signExtendX - 0.7*signExtendWidth, R_SIGN_EXTEND + 0.5*ellipseHeight, R_SIGN_EXTEND + 1.5*ellipseHeight, highlight ? Highlight : BLACK, true, false);
        drawHorizontalSegment(gc, signExtendX - 0.7*signExtendWidth, R_SIGN_EXTEND + 1.5*ellipseHeight, C4_ALU - width*0.05, highlight ? Highlight : BLACK, false, false); // Instruction Memory to ALU Control
        drawVerticalSegment(gc, C4_ALU - width*0.05, R_SIGN_EXTEND + 1.5*ellipseHeight, R_SIGN_EXTEND + 0.8*ellipseHeight, highlight ? Highlight : BLACK, false, false); // ALU Control to Sign Extend
        drawRightArrow(gc, C4_ALU - width*0.05, R_SIGN_EXTEND + 0.8*ellipseHeight, aluControlX, highlight ? Highlight : BLACK, false); // Instruction Memory to ALU Control
    }

    public void drawSignExtendToShiftLeft(GraphicsContext gc, boolean highlight)
    {
        drawHorizontalSegment(gc, signExtendX + ellipseWidth, signExtendY + 0.5*ellipseHeight, muxAluInputX - muxWidth, highlight ? Highlight : BLACK, isDisable(), isCache());
        drawVerticalSegment(gc,  muxAluInputX - muxWidth, signExtendY + 0.5*ellipseHeight, shiftLeft2Y + 0.5*ellipseHeight, highlight ? Highlight : BLACK, isDisable(), isCache());
        drawRightArrow(gc,  muxAluInputX - muxWidth, shiftLeft2Y + 0.5*ellipseHeight, shiftLeft2X, highlight ? Highlight : BLACK, false); // Sign Extend to Shift Left 2
    }

    public void drawSignExtendToMuxALU(GraphicsContext gc, boolean highlight)
    {
        drawHorizontalSegment(gc, signExtendX + ellipseWidth, signExtendY + 0.5*ellipseHeight, muxAluInputX - muxWidth, highlight ? Highlight : BLACK, isDisable(), isCache());
        drawVerticalSegment(gc,  muxAluInputX - muxWidth, signExtendY + 0.5*ellipseHeight, muxAluInputY + 0.8*muxHeight, highlight ? Highlight : BLACK, isDisable(), isCache());
        drawRightArrow(gc,  muxAluInputX - muxWidth, muxAluInputY + 0.8*muxHeight, muxAluInputX, highlight ? Highlight : BLACK, false); // Sign Extend to Shift Left 2
    }

        // Registers -> MUX ALU Input
    public void drawRegistersToMuxALU(GraphicsContext gc, boolean highlight) {
        drawRightArrow(gc, regX + rectWidth, muxAluInputY + 0.3 * muxHeight, muxAluInputX, highlight ? Highlight : BLACK, false);
    }

    // Registers -> Data Memory
    public void drawRegistersToDataMemory(GraphicsContext gc, boolean highlight) {
        drawHorizontalSegment(gc, regX + rectWidth, muxAluInputY + 0.3 * muxHeight, regX + rectWidth * 1.1, highlight ? Highlight : BLACK, false, false);
        drawVerticalSegment(gc, regX + rectWidth * 1.1, muxAluInputY + 0.3 * muxHeight, dataMemY + 0.8 * dataMemHeight, highlight ? Highlight : BLACK, true, false);
        drawRightArrow(gc, regX + rectWidth * 1.1, dataMemY + 0.8 * dataMemHeight, dataMemX, highlight ? Highlight : BLACK, false);
    }

    // Registers -> ALU
    public void drawRegistersToALU(GraphicsContext gc, boolean highlight) {

        drawRightArrow(gc, regX + rectWidth, R_MAIN_PATH + 0.025 * height, aluX, highlight ? Highlight : BLACK, false);
    }

    // MUX ALU Input -> ALU
    public void drawMuxALUToALU(GraphicsContext gc, boolean highlight) {
        drawRightArrow(gc, muxAluInputX + muxWidth, muxAluInputY + 0.5 * muxHeight, aluX, highlight ? Highlight : BLACK, false);
    }

    // ALU -> Data Memory
    public void drawALUToDataMemory(GraphicsContext gc, boolean highlight) {

        drawRightArrow(gc, C4_ALU + aluWidth, aluY + 0.6 * aluHeight, dataMemX, highlight ? Highlight : BLACK, false);
    }

    // ALU -> Flags
    public void drawALUToFlags(GraphicsContext gc, boolean highlight) {

        drawUpArrow(gc, aluX + 0.48 * aluWidth, aluY + 0.12 * aluHeight, flagY + flagBoxSize, highlight ? Highlight : BLACK, isCache());
    }

    // ALU -> MUX MemToReg
    public void drawALUToMuxMemToReg(GraphicsContext gc, boolean highlight) {

        drawHorizontalSegment(gc, aluX + aluWidth, aluY + 0.6 * aluHeight, dataMemX - 0.25 * dataMemWidth, highlight ? Highlight : BLACK, false, false);
        drawVerticalSegment(gc, dataMemX - 0.25 * dataMemWidth, aluY + 0.6 * aluHeight, dataMemY + dataMemHeight * 1.2, highlight ? Highlight : BLACK, true, isCache());
        drawHorizontalSegment(gc, dataMemX - 0.25 * dataMemWidth, dataMemY + dataMemHeight * 1.2, muxMemToRegX - 0.8 * muxWidth, highlight ? Highlight : BLACK, false, false);
        drawVerticalSegment(gc, muxMemToRegX - 0.8 * muxWidth, dataMemY + dataMemHeight * 1.2, muxMemToRegY + 0.8 * muxHeight, highlight ? Highlight : BLACK, isDisable(), isCache());
        drawRightArrow(gc, muxMemToRegX - 0.8 * muxWidth, muxMemToRegY + 0.8 * muxHeight, muxMemToRegX, highlight ? Highlight : BLACK, false);
    }

// Data Memory -> MUX MemToReg
public void drawDataMemoryToMuxMemToReg(GraphicsContext gc, boolean highlight) {
    drawRightArrow(gc, dataMemX + dataMemWidth, muxMemToRegY + 0.2 * muxHeight, muxMemToRegX, highlight ? Highlight : BLACK, false);
}

// MUX MemToReg -> Registers
public void drawMuxMemToRegToRegisters(GraphicsContext gc, boolean highlight) {
    drawHorizontalSegment(gc, muxMemToRegX + muxWidth, muxMemToRegY + 0.5 * muxHeight, muxMemToRegX + muxWidth * 1.8, highlight ? Highlight : BLACK, false, false);
    drawVerticalSegment(gc, muxMemToRegX + muxWidth * 1.8, muxMemToRegY + 0.5 * muxHeight, aluControlY + aluControlHeight * 2, highlight ? Highlight : BLACK, false, false);
    drawHorizontalSegment(gc, muxMemToRegX + muxWidth * 1.8, aluControlY + aluControlHeight * 2, regX - 0.3 * rectWidth, highlight ? Highlight : BLACK, false, false);
    drawVerticalSegment(gc, regX - rectWidth * 0.3, aluControlY + aluControlHeight * 2, regY + 0.9 * regHeight, highlight ? Highlight : BLACK, false, false);
    drawRightArrow(gc, regX - rectWidth * 0.3, regY + 0.9 * regHeight, regX, highlight ? Highlight : BLACK, false);
}

public void drawReg2Loc(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, controlX + 0.7*controlWidth, controlY + 0.05*controlHeight, controlX + controlWidth, color, isDisable(), isCache());
    drawVerticalSegment(gc, controlX + controlWidth, addBranchY + addBranchHeight*0.65, controlY + 0.05*controlHeight, color, isDisable(), isCache());
    drawHorizontalSegment(gc, controlX + controlWidth, addBranchY + addBranchHeight*0.65, instrMemX + instrMemWidth*1.15, color, isDisable(), isCache());
    drawVerticalSegment(gc, instrMemX + instrMemWidth*1.15, addBranchY + addBranchHeight*0.65, muxRegInputY + muxHeight*1.75, color, isDisable(), isCache());
    drawHorizontalSegment(gc, instrMemX + instrMemWidth*1.15,  muxRegInputY + muxHeight*1.75, muxRegInputX + 0.5*muxWidth, color, isDisable(), isCache());
    drawVerticalSegment(gc, muxRegInputX + 0.5*muxWidth, muxRegInputY + muxHeight*1.75, muxRegInputY + muxHeight, color, isDisable(), isCache());
}

public void drawUncondBranch(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, controlX + 0.85*controlWidth, controlY + 0.14*controlHeight, orGateX + 0.1*gateW, color, isDisable(), isCache());
}

public void drawFlagBranch(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, controlX + 0.9*controlWidth, controlY + 0.23*controlHeight, andGate1X - 0.5*gateW, color, isDisable(), isCache());
    drawVerticalSegment(gc, andGate1X - 0.5*gateW, andGateY + 0.2*gateH, controlY + 0.23*controlHeight, color, isDisable(), isCache());
    drawHorizontalSegment(gc, andGate1X - 0.5*gateW, andGateY + 0.2*gateH, andGate1X, color, isDisable(), isCache());
}

public void drawZeroBranch(GraphicsContext gc,  boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, controlX + 0.95*controlWidth, controlY + 0.32*controlHeight, andGate2X - 0.7*gateW, color, isDisable(), isCache());
    drawVerticalSegment(gc, andGate2X - 0.7*gateW, andGateY + 0.2*gateH, controlY + 0.32*controlHeight, color, isDisable(), isCache());
    drawHorizontalSegment(gc, andGate2X - 0.7*gateW, andGateY + 0.2*gateH, andGate2X, color, isDisable(), isCache());
}

public void drawMemRead(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, controlX + 0.97*controlWidth, controlY + 0.41*controlHeight, muxMemToRegX + 2.5*muxWidth, color, isDisable(), isCache());
    drawVerticalSegment(gc, muxMemToRegX + 2.5*muxWidth, controlY + 0.41*controlHeight, dataMemY + 1.4*dataMemHeight, color, isDisable(), isCache());
    drawHorizontalSegment(gc, muxMemToRegX + 2.5*muxWidth, dataMemY + 1.4*dataMemHeight, dataMemX + dataMemWidth*0.5, color, isDisable(), isCache());
    drawVerticalSegment(gc, dataMemX + dataMemWidth*0.5, dataMemY + 1.4*dataMemHeight, dataMemY + dataMemHeight, color, isDisable(), isCache());
}

public void drawMemToReg(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, controlX + 0.99*controlWidth, controlY + 0.5*controlHeight, muxMemToRegX + 0.5*muxWidth, color, isDisable(), isCache());
    drawVerticalSegment(gc, muxMemToRegX + 0.5*muxWidth, controlY + 0.5*controlHeight, muxMemToRegY, color, isDisable(), isCache());
}

public void drawMemWrite(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, controlX + 0.97*controlWidth, controlY + 0.59*controlHeight, dataMemX + dataMemWidth*0.5, color, isDisable(), isCache());
    drawVerticalSegment(gc, dataMemX + dataMemWidth*0.5, controlY + 0.59*controlHeight, dataMemY, color, isDisable(), isCache());
}

public void drawFlagWrite(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, controlX + 0.98*controlWidth, controlY + 0.68*controlHeight, aluX + 0.48*aluWidth, color, isDisable(), isCache());
    drawVerticalSegment(gc, aluX + 0.48*aluWidth, controlY + 0.68*controlHeight, flagY, color, isDisable(), isCache());
}

public void drawALUSrc(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, controlX + 0.93*controlWidth, controlY + 0.77*controlHeight, muxAluInputX + 0.5*muxWidth, color, isDisable(), isCache());
    drawVerticalSegment(gc, muxAluInputX + 0.5*muxWidth, controlY + 0.77*controlHeight, muxAluInputY, color, isDisable(), isCache());
}

public void drawALUOp(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, controlX + 0.83*controlWidth, controlY + 0.86*controlHeight, muxAluInputX - 1.5*muxWidth, color, isDisable(), isCache());
    drawVerticalSegment(gc, muxAluInputX - 1.5*muxWidth, controlY + 0.86*controlHeight, aluControlY + 1.7 * aluControlHeight, color, isDisable(), isCache());
    drawHorizontalSegment(gc, muxAluInputX - 1.5*muxWidth, aluControlY + 1.7 * aluControlHeight, aluControlX + aluControlHeight*0.5, color, isDisable(), isCache());
    drawVerticalSegment(gc, aluControlX + aluControlHeight*0.5, aluControlY + 1.7 * aluControlHeight, aluControlY + aluControlHeight, color, isDisable(), isCache());
}

public void drawRegWrite(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, controlX + 0.7*controlWidth, controlY + 0.95*controlHeight, regX + 0.5 *rectWidth, color, isDisable(), isCache());
    drawVerticalSegment(gc, regX + 0.5 *rectWidth, controlY + 0.95*controlHeight, regY, color, isDisable(), isCache());
}

public void drawFlagToAndGate(GraphicsContext gc,boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, flagX + 4*flagBoxSize, flagY + 0.5*flagBoxSize, andGate1X , color, isDisable(), isCache());
}

public void drawALUToALUControl(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawVerticalSegment(gc, aluX + 0.48*aluWidth, aluY + 0.88*aluHeight, aluControlY + aluControlHeight*0.5, color, isDisable(), isCache());
    drawHorizontalSegment(gc, aluX + 0.48*aluWidth, aluControlY + aluControlHeight*0.5, aluControlX + aluControlWidth, color, isDisable(), isCache());
}

public void drawALUToAndGate(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, aluX + aluWidth, aluY + 0.4 * aluBlockHeight, andGate2X - 0.7*gateW, color, isDisable(), isCache());
    drawVerticalSegment(gc, andGate2X - 0.7*gateW, andGateY + 0.8*gateH, aluY + 0.4 * aluBlockHeight, color, isDisable(), isCache());
    drawHorizontalSegment(gc, andGate2X - 0.7*gateW, andGateY + 0.8*gateH, andGate2X, color, isDisable(), isCache());
}

public void drawAnd1ToOrGate(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, andGate1X + 0.9*gateW, andGateY + 0.5*gateH, andGate2X - 1.2*gateW, color, isDisable(), isCache());
    drawVerticalSegment(gc, andGate2X - 1.2*gateW, orGateY + 0.65*gateH, andGateY + 0.5*gateH, color, isDisable(), isCache());
    drawHorizontalSegment(gc, andGate2X - 1.2*gateW, orGateY + 0.65*gateH, orGateX + 0.2*gateW, color, isDisable(), isCache());
}

public void drawAnd2ToOrGate(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, andGate2X + 0.95*gateW, andGateY + 0.5*gateH, orGateX - 0.8*gateW, color, isDisable(), isCache());
    drawVerticalSegment(gc, orGateX - 0.8*gateW, orGateY + 0.95*gateH, andGateY + 0.5*gateH, color, isDisable(), isCache());
    drawHorizontalSegment(gc, orGateX - 0.8*gateW, orGateY + 0.95*gateH, orGateX + 0.1*gateW, color, isDisable(), isCache());
}

public void drawOrGateToMux(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
    drawHorizontalSegment(gc, orGateX + 1.2*gateW, orGateY + 0.6*gateH, muxPcSourceX + 0.5*muxWidth, color, isDisable(), isCache());
    drawVerticalSegment(gc, muxPcSourceX + 0.5*muxWidth, orGateY + 0.6*gateH, muxPcSourceY + muxHeight, color, isDisable(), isCache());
}

}