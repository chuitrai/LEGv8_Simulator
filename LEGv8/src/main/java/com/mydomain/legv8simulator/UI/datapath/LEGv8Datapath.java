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

    double width = getWidth();
    double height = getHeight();

    // Nếu canvas chưa có kích thước thì không vẽ gì cả
    if (width == 0 || height == 0) {
        return;
    }

    gc.clearRect(0, 0, width, height);

    // Thiết lập màu nền
    gc.setFill(Color.WHITE);
    gc.fillRect(0, 0, width, height);

    // --- HỆ LƯỚI TỌA ĐỘ TƯƠNG ĐỐI (DỰA TRÊN TỶ LỆ) ---
    // Các cột X được tính bằng phần trăm của chiều rộng
    final double C1_PC_IM = width * 0.08;
    final double C2_CONTROL = width * 0.4;
    final double C3_REGISTERS = width * 0.45;
    final double C4_ALU = width * 0.64;
    final double C5_DATAMEM = width * 0.81;
    final double C6_MUX = width * 0.925;

    // Các hàng Y được tính bằng phần trăm của chiều cao
    final double R_BRANCH_LOGIC = height * 0.18;
    final double R_CONTROL = height * 0.35;
    final double R_MAIN_PATH = height * 0.55;
    final double R_SIGN_EXTEND = height * 0.8;

    // Kích thước các khối được tính theo tỷ lệ
    double rectWidth = width * 0.1;
    double rectHeight = height * 0.2;
    double aluWidth = width * 0.085;
    double aluHeight = height * 0.2;
    double pcWidth = width * 0.035;
    double pcHeight = height * 0.09;
    double ellipseWidth = width * 0.05;
    double ellipseHeight = height * 0.07;

    // Vị trí các khối được tính toán dựa trên kích thước và tỷ lệ

    
    // Kích thước font chữ co giãn
    int baseFontSize = (int) (width / 90); // 90 là một hằng số tỷ lệ có thể điều chỉnh
    if (baseFontSize < 8) baseFontSize = 8; // Đặt kích thước tối thiểu

    // ===================================
    // BƯỚC 1: VẼ CÁC KHỐI CHÍNH VÀ PHỤ
    // ===================================
    // --- Khối trên dòng chính ---
        // --- VỊ TRÍ CÁC KHỐI CHÍNH ---
        double pcRectX = C1_PC_IM;
        double pcRectY = R_MAIN_PATH - pcHeight / 2;

        double instrMemX = C1_PC_IM + width * 0.08;
        double instrMemY = R_MAIN_PATH - 0.02 * height;

        double regX = C3_REGISTERS;
        double regY = R_MAIN_PATH - height * 0.01;

        double aluX = C4_ALU;
        double aluY = R_MAIN_PATH - aluHeight * 0.05;

        double dataMemX = C5_DATAMEM;
        double dataMemY = R_MAIN_PATH + 0.4 * aluHeight;

        // --- VỊ TRÍ KHỐI LOGIC TRÊN CÙNG ---
        double add4X = C1_PC_IM + width * 0.13;
        double add4Y = R_BRANCH_LOGIC - pcHeight;

        double shiftLeft2X = C3_REGISTERS + 0.15 * width;
        double shiftLeft2Y = R_BRANCH_LOGIC * 1.1;

        double addBranchX = C3_REGISTERS + width * 0.25;
        double addBranchY = R_BRANCH_LOGIC - pcHeight * 0.4;

        // --- VỊ TRÍ KHỐI ĐIỀU KHIỂN VÀ MỞ RỘNG ---
        double controlX = C2_CONTROL;
        double controlY = R_CONTROL - 0.1 * height;

        double signExtendX = C3_REGISTERS + width * 0.02;
        double signExtendY = R_SIGN_EXTEND;

        double aluControlX = C4_ALU - width * 0.025;
        double aluControlY = R_SIGN_EXTEND * 1.02;

        double flagSize = width * 0.018;
        double flagX = C4_ALU + 0.05 * aluWidth;
        double flagY = R_MAIN_PATH - aluHeight * 0.05 - flagSize - height * 0.01;

        // --- VỊ TRÍ MUX ---
        double muxRegInputX = C2_CONTROL - 0.02 * width;
        double muxRegInputY = R_MAIN_PATH + 0.03 * height;

        double muxAluInputX = C4_ALU - 0.04 * width;
        double muxAluInputY = R_MAIN_PATH + 0.11 * height;

        double muxPcSourceX = C6_MUX - 0.02 * width;
        double muxPcSourceY = R_BRANCH_LOGIC - 0.065 * height;

        double muxMemToRegX = C6_MUX;
        double muxMemToRegY = R_MAIN_PATH + 0.6 * aluHeight;

        // --- KHAI BÁO KÍCH THƯỚC CÁC KHỐI ---
        // Program Counter
        double pcRectWidth = pcWidth;
        double pcRectHeight = pcHeight;
        // Instruction Memory
        double instrMemWidth = rectWidth;
        double instrMemHeight = rectHeight;
        // Registers
        double regWidth = rectWidth;
        double regHeight = rectHeight * 1.16;
        // ALU
        double aluBlockWidth = aluWidth;
        double aluBlockHeight = aluHeight;
        // Data Memory
        double dataMemWidth = rectWidth * 0.9;
        double dataMemHeight = rectHeight * 0.9;
        // Add 4
        double add4Width = pcWidth;
        double add4Height = pcHeight * 0.8;
        // Shift left 2
        double shiftLeft2Width = ellipseWidth;
        double shiftLeft2Height = ellipseHeight;
        // Add Branch
        double addBranchWidth = pcWidth * 1.5;
        double addBranchHeight = pcHeight * 1.2;
        // Control
        double controlWidth = ellipseWidth * 1.2;
        double controlHeight = ellipseHeight * 4;
        // Sign-extend
        double signExtendWidth = ellipseWidth;
        double signExtendHeight = ellipseHeight;
        // ALU Control
        double aluControlWidth = ellipseWidth;
        double aluControlHeight = ellipseHeight;
        // Flag Box
        double flagBoxSize = flagSize;
        // MUX
        double muxWidth = 0.02 * width;
        double muxHeight = 0.09 * height;

        // --- VẼ CÁC KHỐI ---
        drawCompRect(gc, pcRectX, pcRectY, pcRectWidth, pcRectHeight, Color.BLACK, Color.web("#ffe3e3")); // Program Counter
        drawCompRect(gc, instrMemX, instrMemY, instrMemWidth, instrMemHeight, Color.BLACK, Color.web("#e3f6e3")); // Instruction Memory
        drawCompRect(gc, regX, regY, regWidth, regHeight, Color.BLACK, Color.web("#ffe3ec")); // Registers
        drawALU(gc, aluX, aluY, aluBlockWidth, aluBlockHeight, Color.BLACK, Color.web("#f3e5f5"), false); // ALU
        drawCompRect(gc, dataMemX, dataMemY, dataMemWidth, dataMemHeight, Color.BLACK, Color.web("#fde2e2")); // Data Memory

        // --- Khối logic trên cùng ---
        drawALU(gc, add4X, add4Y, add4Width, add4Height, Color.web("#1C2B2B"), Color.web("#f0f4f8"), false); // Add 4
        drawEllipse(gc, shiftLeft2X, shiftLeft2Y, shiftLeft2Width, shiftLeft2Height, Color.web("#1C2B2B"), Color.web("#f9f9f9")); // Shift left 2
        drawALU(gc, addBranchX, addBranchY, addBranchWidth, addBranchHeight, Color.web("#1C2B2B"), Color.web("#f0f4f8"), false); // Add Branch

        // --- Khối điều khiển và mở rộng ---
        drawCompEllipse(gc, controlX, controlY, controlWidth, controlHeight, Color.web("#12C0E8"), Color.web("#E1F5FE"), false); // Control
        drawCompEllipse(gc, signExtendX, signExtendY, signExtendWidth, signExtendHeight, Color.web("#1C2B2B"), Color.web("#f0f4f8"), false); // Sign-extend
        drawCompEllipse(gc, aluControlX, aluControlY, aluControlWidth, aluControlHeight, Color.web("#12C0E8"), Color.web("#E1F5FE"), false); // ALU Control

        drawFlagBox(gc, "N", flagX, flagY, flagBoxSize, baseFontSize - 2);
        drawFlagBox(gc, "Z", flagX + flagBoxSize, flagY, flagBoxSize, baseFontSize - 2);
        drawFlagBox(gc, "C", flagX + 2 * (flagBoxSize), flagY, flagBoxSize, baseFontSize - 2);
        drawFlagBox(gc, "V", flagX + 3 * (flagBoxSize), flagY, flagBoxSize, baseFontSize - 2);

        // --- VẼ CÁC BỘ MUX ---
        drawMux(gc, muxRegInputX, muxRegInputY, muxWidth, muxHeight, Color.web("#1C2B2B"), Color.web("#E6E6FA"), false); // MUX Register Input
        drawMux(gc, muxAluInputX, muxAluInputY, muxWidth, muxHeight, Color.web("#1C2B2B"), Color.web("#E6E6FA"), false); // MUX ALU Input
        drawMux(gc, muxPcSourceX, muxPcSourceY, muxWidth, muxHeight, Color.web("#1C2B2B"), Color.web("#E6E6FA"), false); // MUX PC Source
        drawMux(gc, muxMemToRegX, muxMemToRegY, muxWidth, muxHeight, Color.web("#1C2B2B"), Color.web("#E6E6FA"), false); // MUX MemToReg

        // // ===================================
        // // 3. VẼ CÁC CỔNG LOGIC
        // // ===================================
        double gateW = width * 0.022;
        double gateH = height * 0.028;

        // Vị trí X của các cổng logic
        double andGate1X = C4_ALU + aluBlockWidth + width * 0.015;
        double andGate2X = andGate1X + gateW + width * 0.045;
        double orGateX = andGate2X + gateW*2.5;

        // Vị trí Y của các cổng logic
        double andGateY = flagY - 0.3 * gateH;
        double orGateY = R_BRANCH_LOGIC + height * 0.1;

        // --- Vẽ các cổng ---
        drawAndGateHorizontal(gc, andGate1X, andGateY, gateW, gateH, CONTROL_SIGNAL);
        drawAndGateHorizontal(gc, andGate2X, andGateY, gateW, gateH, CONTROL_SIGNAL);
        drawOrGateHorizontal(gc, orGateX, orGateY, gateW * 1.2, gateH * 1.2);

        // // ===================================
        // // 4. VẼ CÁC ĐƯỜNG DÂY DỮ LIỆU (DATA PATH - BLACK)
        // // ===================================
        // // PC -> Add
        drawVerticalSegment(gc, C1_PC_IM + pcWidth*1.5, R_BRANCH_LOGIC*0.6, R_MAIN_PATH, BLACK, false, true); // PC to Add 4
        drawRightArrow(gc, C1_PC_IM + pcWidth*1.5, R_BRANCH_LOGIC*0.6, C1_PC_IM + width*0.13, BLACK, false); // PC to Add 4
        drawRightArrow(gc, C1_PC_IM + pcWidth*3, R_BRANCH_LOGIC*0.85, C1_PC_IM + width*0.13, BLACK, false); // Add 4
        drawRightArrow(gc, C1_PC_IM - pcWidth*0.5, R_MAIN_PATH, C1_PC_IM, BLACK, false); // To PC
        drawVerticalSegment(gc, C1_PC_IM - pcWidth*0.5, R_MAIN_PATH, R_BRANCH_LOGIC*0.3,  BLACK, false, false); 
        drawHorizontalSegment(gc, C1_PC_IM - pcWidth*0.5, R_BRANCH_LOGIC*0.3, C6_MUX + 0.04*width, BLACK, false, false);
        drawVerticalSegment(gc, C6_MUX + 0.04*width, R_BRANCH_LOGIC*0.3, R_BRANCH_LOGIC*0.9, BLACK, false, false); // MUX to PC
        drawRightArrow(gc, C1_PC_IM + width*0.13 + pcWidth, R_BRANCH_LOGIC*0.75, C6_MUX - 0.02*width, BLACK, false); // Add 4 to MUX
        drawHorizontalSegment(gc, C6_MUX + 0.04*width, R_BRANCH_LOGIC*0.9, C6_MUX, BLACK, false, false); // MUX to PC
        drawRightArrow(gc, C3_REGISTERS + width*0.25 + pcWidth*1.5, R_BRANCH_LOGIC*1.1, C6_MUX - 0.02*width, BLACK, false); // Add Branch to MUX
        
        drawRightArrow(gc, C1_PC_IM + pcWidth, R_MAIN_PATH, instrMemX, BLACK, false); // PC to Instruction Memory

        // Shift Left 2 -> Add Branch
        drawRightArrow(gc, C3_REGISTERS + width*0.15 + ellipseWidth, R_BRANCH_LOGIC*1.3, C3_REGISTERS + width*0.25, BLACK, false); // Shift Left 2 to Add Branch
        // PC -> Add Shift
        drawHorizontalSegment(gc, C1_PC_IM + pcWidth*1.5, controlY, add4X + add4Width*2.5, BLACK, true, isCache());
        drawVerticalSegment(gc, add4X + add4Width*2.5, controlY, addBranchY + 0.25*addBranchHeight, BLACK, isDisable(), isCache());
        drawRightArrow(gc, add4X + add4Width*2.5, addBranchY + 0.25*addBranchHeight, addBranchX, BLACK, false); // PC to Shift Left 2

        // Instruction Memory -> Control
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_SIGN_EXTEND + 0.5*ellipseHeight, controlY + 0.5 *controlHeight, BLACK, false, false); 
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, controlY + 0.5 *controlHeight, C2_CONTROL, BLACK, false); // Instruction Memory to Control

        // Instruction Memory -> Sign Extend
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_SIGN_EXTEND + 0.5*ellipseHeight, signExtendX, BLACK, false); // Instruction Memory to Sign Extend

        // Instruction Memory -> Registers
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, muxRegInputY + 0.15 * muxHeight, C2_CONTROL - 0.02*width, BLACK, true); // Instruction Memory to MUX Register Input
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.1 * regHeight, C3_REGISTERS, BLACK, true); // Instruction Memory to Registers
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.7 *regHeight, C3_REGISTERS, BLACK, true); // Instruction Memory to Sign Extend
        drawRightArrow(gc, C2_CONTROL, R_MAIN_PATH + 0.075 * height, C3_REGISTERS, BLACK, false); // MUX Register Input to Registers
        drawRightArrow(gc, C2_CONTROL - 0.05*width, muxRegInputY + 0.8*muxHeight, muxRegInputX , BLACK, false); // Instruction Memory to MUX Register Input
        drawVerticalSegment(gc, C2_CONTROL - 0.05*width, regY + 0.7 *regHeight, muxRegInputY + 0.8*muxHeight, BLACK, true, false); // MUX Register Input to Sign Extend
        
        // Registers -> MUX ALU Input
        drawRightArrow(gc, C3_REGISTERS + rectWidth, muxAluInputY + 0.3*muxHeight, muxAluInputX, BLACK, false); // Registers to MUX ALU Input

        // Registers -> Data Memory
        drawVerticalSegment(gc, regX + rectWidth*1.1 , muxAluInputY + 0.3*muxHeight, dataMemY + 0.8*dataMemHeight, BLACK, true, false); // Registers to Data Memory)
        drawRightArrow(gc, regX + rectWidth*1.1, dataMemY + 0.8*dataMemHeight, dataMemX, BLACK, false); // Registers to Data Memory

        // Registers -> ALU
        drawRightArrow(gc, C3_REGISTERS + rectWidth, R_MAIN_PATH + 0.025 * height, C4_ALU, BLACK, false); // Registers to ALU

        // MUX ALU Input -> ALU
        drawRightArrow(gc, muxAluInputX + muxWidth, muxAluInputY + 0.5*muxHeight, aluX, BLACK, false); // MUX ALU Input to ALU

        // Instruction Memory -> ALU Control
        drawVerticalSegment(gc, signExtendX - 0.7*signExtendWidth, R_SIGN_EXTEND + 0.5*ellipseHeight, R_SIGN_EXTEND + 1.5*ellipseHeight, BLACK, true, false);
        drawHorizontalSegment(gc, signExtendX - 0.7*signExtendWidth, R_SIGN_EXTEND + 1.5*ellipseHeight, C4_ALU - width*0.05, BLACK, false, false); // Instruction Memory to ALU Control
        drawVerticalSegment(gc, C4_ALU - width*0.05, R_SIGN_EXTEND + 1.5*ellipseHeight, R_SIGN_EXTEND + 0.8*ellipseHeight, BLACK, false, false); // ALU Control to Sign Extend
        drawRightArrow(gc, C4_ALU - width*0.05, R_SIGN_EXTEND + 0.8*ellipseHeight, aluControlX, BLACK, false); // Instruction Memory to ALU Control

        // Sign Extend -> Shift Left 2
        drawHorizontalSegment(gc, signExtendX + ellipseWidth, signExtendY + 0.5*ellipseHeight, muxAluInputX - muxWidth, BLACK, isDisable(), isCache());
        drawVerticalSegment(gc,  muxAluInputX - muxWidth, signExtendY + 0.5*ellipseHeight, shiftLeft2Y + 0.5*ellipseHeight, BLACK, isDisable(), isCache());
        drawRightArrow(gc,  muxAluInputX - muxWidth, shiftLeft2Y + 0.5*ellipseHeight, shiftLeft2X, BLACK, false); // Sign Extend to Shift Left 2

        drawRightArrow(gc, muxAluInputX - muxWidth, muxAluInputY + 0.75*muxHeight, muxAluInputX, BLACK, true); // Sign Extend to ALU Control

        // ALU -> Data Memory
        drawRightArrow(gc, C4_ALU + aluWidth, aluY + 0.6*aluHeight, dataMemX, BLACK, false); // ALU to Data Memory

        // ALU -> Flags
        drawUpArrow(gc, aluX + 0.48*aluWidth, aluY + 0.12*aluHeight, flagY + flagBoxSize, BLACK, isCache());

        // ALU -> MUX MemToReg
        drawVerticalSegment(gc, dataMemX - 0.25*dataMemWidth, aluY + 0.6*aluHeight, dataMemY + dataMemHeight * 1.2, BLACK, true, isCache());
        drawHorizontalSegment(gc, dataMemX - 0.25*dataMemWidth, dataMemY + dataMemHeight * 1.2, muxMemToRegX - 0.8*muxWidth, BLACK, false, false); // ALU to MUX MemToReg
        drawVerticalSegment(gc, muxMemToRegX - 0.8*muxWidth, dataMemY + dataMemHeight * 1.2, muxMemToRegY + 0.8*muxHeight, BLACK, isDisable(), isCache());
        drawRightArrow(gc, muxMemToRegX - 0.8*muxWidth, muxMemToRegY + 0.8*muxHeight, muxMemToRegX, BLACK, false); // ALU to MUX MemToReg

        // Data Memory -> MUX MemToReg
        drawRightArrow(gc, dataMemX + dataMemWidth, muxMemToRegY + 0.2*muxHeight, muxMemToRegX, BLACK, false); // Data Memory to MUX MemToReg

        // MUX MemToReg -> Registers
        drawHorizontalSegment(gc, muxMemToRegX + muxWidth, muxMemToRegY + 0.5*muxHeight,  muxMemToRegX + muxWidth*1.8, BLACK, false, false); // MUX MemToReg to Registers
        drawVerticalSegment(gc, muxMemToRegX + muxWidth*1.8, muxMemToRegY + 0.5*muxHeight, aluControlY +aluControlHeight*2, BLACK, false, false); // MUX MemToReg to Registers
        drawHorizontalSegment(gc, muxMemToRegX + muxWidth*1.8, aluControlY + aluControlHeight*2, regX - 0.3*rectWidth, BLACK, false, false); // MUX MemToReg to Registers
        drawVerticalSegment(gc, regX - rectWidth*0.3, aluControlY + aluControlHeight*2, regY + 0.9*regHeight, BLACK, false, false); // MUX MemToReg to Registers
        drawRightArrow(gc, regX - rectWidth*0.3, regY + 0.9*regHeight, regX, BLACK, false); // MUX MemToReg to Registers

        // CONTROL SIGNALS

        // Reg2Loc
        drawHorizontalSegment(gc, controlX + 0.7*controlWidth, controlY + 0.05*controlHeight, controlX + controlWidth, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, controlX + controlWidth, addBranchY + addBranchHeight*0.65, controlY + 0.05*controlHeight, ARM_BLUE, isDisable(), isCache());
        drawHorizontalSegment(gc, controlX + controlWidth, addBranchY + addBranchHeight*0.65, instrMemX + instrMemWidth*1.15, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, instrMemX + instrMemWidth*1.15, addBranchY + addBranchHeight*0.65, muxRegInputY + muxHeight*1.75, ARM_BLUE, isDisable(), isCache());
        drawHorizontalSegment(gc, instrMemX + instrMemWidth*1.15,  muxRegInputY + muxHeight*1.75, muxRegInputX + 0.5*muxWidth, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, muxRegInputX + 0.5*muxWidth, muxRegInputY + muxHeight*1.75, muxRegInputY + muxHeight, ARM_BLUE, isDisable(), isCache());

        // Unconditional Branch
        drawHorizontalSegment(gc, controlX + 0.85*controlWidth, controlY + 0.14*controlHeight, orGateX + 0.1*gateW, ARM_BLUE, isDisable(), isCache());

        // FlagBranch
        drawHorizontalSegment(gc, controlX + 0.9*controlWidth, controlY + 0.23*controlHeight, andGate1X - 0.5*gateW, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, andGate1X - 0.5*gateW, andGateY + 0.2*gateH, controlY + 0.23*controlHeight, ARM_BLUE, isDisable(), isCache());
        drawHorizontalSegment(gc, andGate1X - 0.5*gateW, andGateY + 0.2*gateH, andGate1X, ARM_BLUE, isDisable(), isCache());

        // ZeroBranch
        drawHorizontalSegment(gc, controlX + 0.95*controlWidth, controlY + 0.32*controlHeight, andGate2X - 0.7*gateW, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, andGate2X - 0.7*gateW, andGateY + 0.2*gateH, controlY + 0.32*controlHeight, ARM_BLUE, isDisable(), isCache());
        drawHorizontalSegment(gc, andGate2X - 0.7*gateW, andGateY + 0.2*gateH, andGate2X, ARM_BLUE, isDisable(), isCache());

        // MemRead
        drawHorizontalSegment(gc, controlX + 0.97*controlWidth, controlY + 0.41*controlHeight, muxMemToRegX + 2.5*muxWidth, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, muxMemToRegX + 2.5*muxWidth, controlY + 0.41*controlHeight, dataMemY + 1.4*dataMemHeight, ARM_BLUE, isDisable(), isCache());
        drawHorizontalSegment(gc, muxMemToRegX + 2.5*muxWidth, dataMemY + 1.4*dataMemHeight, dataMemX + dataMemWidth*0.5, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, dataMemX + dataMemWidth*0.5, dataMemY + 1.4*dataMemHeight, dataMemY + dataMemHeight, ARM_BLUE, isDisable(), isCache());

        // MemToReg
        drawHorizontalSegment(gc, controlX + 0.99*controlWidth, controlY + 0.5*controlHeight, muxMemToRegX + 0.5*muxWidth, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, muxMemToRegX + 0.5*muxWidth, controlY + 0.5*controlHeight, muxMemToRegY, ARM_BLUE, isDisable(), isCache());

        // MemWrite
        drawHorizontalSegment(gc, controlX + 0.97*controlWidth, controlY + 0.59*controlHeight, dataMemX + dataMemWidth*0.5, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, dataMemX + dataMemWidth*0.5, controlY + 0.59*controlHeight, dataMemY, ARM_BLUE, isDisable(), isCache());

        // FlagWrite
        drawHorizontalSegment(gc, controlX + 0.98*controlWidth, controlY + 0.68*controlHeight, aluX + 0.48*aluWidth, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, aluX + 0.48*aluWidth, controlY + 0.68*controlHeight, flagY, ARM_BLUE, isDisable(), isCache());

        // ALUSrc
        drawHorizontalSegment(gc, controlX + 0.93*controlWidth, controlY + 0.77*controlHeight, muxAluInputX + 0.5*muxWidth, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, muxAluInputX + 0.5*muxWidth, controlY + 0.77*controlHeight, muxAluInputY, ARM_BLUE, isDisable(), isCache());

        // ALUOp
        drawHorizontalSegment(gc, controlX + 0.83*controlWidth, controlY + 0.86*controlHeight, muxAluInputX - 1.5*muxWidth, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, muxAluInputX - 1.5*muxWidth, controlY + 0.86*controlHeight, aluControlY + 1.7 * aluControlHeight, ARM_BLUE, isDisable(), isCache());
        drawHorizontalSegment(gc, muxAluInputX - 1.5*muxWidth, aluControlY + 1.7 * aluControlHeight, aluControlX + aluControlHeight*0.5, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, aluControlX + aluControlHeight*0.5, aluControlY + 1.7 * aluControlHeight, aluControlY + aluControlHeight, ARM_BLUE, isDisable(), isCache());

        // RegWrite
        drawHorizontalSegment(gc, controlX + 0.7*controlWidth, controlY + 0.95*controlHeight, regX + 0.5 *rectWidth, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, regX + 0.5 *rectWidth, controlY + 0.95*controlHeight, regY, ARM_BLUE, isDisable(), isCache());

        // Flag to AND gate
        drawHorizontalSegment(gc, flagX + 4*flagBoxSize, flagY + 0.5*flagBoxSize, andGate1X , ARM_BLUE, isDisable(), isCache());

        // ALU to ALU Control
        drawVerticalSegment(gc, aluX + 0.48*aluWidth, aluY + 0.88*aluHeight, aluControlY + aluControlHeight*0.5, ARM_BLUE, isDisable(), isCache());
        drawHorizontalSegment(gc, aluX + 0.48*aluWidth, aluControlY + aluControlHeight*0.5, aluControlX + aluControlWidth, ARM_BLUE, isDisable(), isCache());

        // ALU to AND gate
        drawHorizontalSegment(gc, aluX + aluWidth, aluY + 0.4 * aluBlockHeight, andGate2X - 0.7*gateW, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, andGate2X - 0.7*gateW, andGateY + 0.8*gateH, aluY + 0.4 * aluBlockHeight, ARM_BLUE, isDisable(), isCache());
        drawHorizontalSegment(gc, andGate2X - 0.7*gateW, andGateY + 0.8*gateH, andGate2X, ARM_BLUE, isDisable(), isCache());

        // AND gate 1 to OR gate
        drawHorizontalSegment(gc, andGate1X + 0.9*gateW, andGateY + 0.5*gateH, andGate2X - 1.2*gateW, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, andGate2X - 1.2*gateW, orGateY + 0.65*gateH, andGateY + 0.5*gateH, ARM_BLUE, isDisable(), isCache());
        drawHorizontalSegment(gc, andGate2X - 1.2*gateW, orGateY + 0.65*gateH, orGateX + 0.2*gateW, ARM_BLUE, isDisable(), isCache());

        // AND gate 2 to OR gate
        drawHorizontalSegment(gc, andGate2X + 0.95*gateW, andGateY + 0.5*gateH, orGateX - 0.8*gateW, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, orGateX - 0.8*gateW, orGateY + 0.95*gateH, andGateY + 0.5*gateH, ARM_BLUE, isDisable(), isCache());
        drawHorizontalSegment(gc, orGateX - 0.8*gateW, orGateY + 0.95*gateH, orGateX + 0.1*gateW, ARM_BLUE, isDisable(), isCache());

        // OR gate to MUX
        drawHorizontalSegment(gc, orGateX + 1.2*gateW, orGateY + 0.6*gateH, muxPcSourceX + 0.5*muxWidth, ARM_BLUE, isDisable(), isCache());
        drawVerticalSegment(gc, muxPcSourceX + 0.5*muxWidth, orGateY + 0.6*gateH, muxPcSourceY + muxHeight, ARM_BLUE, isDisable(), isCache());

        // =======================================================
        //  BƯỚC 5: VẼ NHÃN (LABELS)
        // =======================================================

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
        int portFontSize = baseFontSize > 11 ? 11 : baseFontSize; // Giới hạn kích thước font cho cổng

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
}