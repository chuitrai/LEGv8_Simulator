package hellofx.datapath;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import static hellofx.datapath.DatapathGraphicsFX.*;

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

    // --- HỆ LƯỚI TỌA ĐỘ TƯƠNG ĐỐI (DỰA TRÊN TỶ LỆ) ---
    // Các cột X được tính bằng phần trăm của chiều rộng
    final double C1_PC_IM = width * 0.08;
    final double C2_CONTROL = width * 0.4;
    final double C3_REGISTERS = width * 0.45;
    final double C4_ALU = width * 0.63;
    final double C5_DATAMEM = width * 0.78;
    final double C6_MUX = width * 0.91;

    // Các hàng Y được tính bằng phần trăm của chiều cao
    final double R_BRANCH_LOGIC = height * 0.18;
    final double R_CONTROL = height * 0.35;
    final double R_MAIN_PATH = height * 0.55;
    final double R_SIGN_EXTEND = height * 0.85;

    // Kích thước các khối được tính theo tỷ lệ
    double rectWidth = width * 0.1;
    double rectHeight = height * 0.2;
    double aluWidth = width * 0.085;
    double aluHeight = height * 0.2;
    double pcWidth = width * 0.035;
    double pcHeight = height * 0.09;
    double ellipseWidth = width * 0.05;
    double ellipseHeight = height * 0.07;
    
    // Kích thước font chữ co giãn
    int baseFontSize = (int) (width / 90); // 90 là một hằng số tỷ lệ có thể điều chỉnh
    if (baseFontSize < 8) baseFontSize = 8; // Đặt kích thước tối thiểu

    // ===================================
    // BƯỚC 1: VẼ CÁC KHỐI CHÍNH VÀ PHỤ
    // ===================================
    // --- Khối trên dòng chính ---
        drawCompRect(gc, C1_PC_IM, R_MAIN_PATH - pcHeight/2,pcWidth, pcHeight); // Program Counter
        drawCompRect(gc, C1_PC_IM + width*0.09, R_MAIN_PATH - 0.02*height, rectWidth, rectHeight); // Instruction Memory
        drawCompRect(gc, C3_REGISTERS, R_MAIN_PATH - height*0.01, rectWidth, rectHeight*1.16); // Registers
        drawALU(gc, C4_ALU, R_MAIN_PATH - aluHeight*0.05, aluWidth, aluHeight, true); // ALU
        drawCompRect(gc, C5_DATAMEM, R_MAIN_PATH + 0.4*aluHeight, rectWidth*0.9, rectHeight*0.9); // Data Memory

        // --- Khối logic trên cùng ---
        drawALU(gc, C1_PC_IM + width*0.13, R_BRANCH_LOGIC - pcHeight, pcWidth*1.2, pcHeight, false); // Add 4
        drawCompEllipse(gc, C3_REGISTERS + 0.15*width, R_BRANCH_LOGIC , ellipseWidth, ellipseHeight, Color.BLACK, false); // Shift left 2
        drawALU(gc, C3_REGISTERS + width*0.25, R_BRANCH_LOGIC - pcHeight*0.4, pcWidth*1.5, pcHeight*1.2, false); // Add Branch

        // --- Khối điều khiển và mở rộng ---
        drawCompEllipse(gc, C2_CONTROL, R_CONTROL - 0.1*height, ellipseWidth*1.2, ellipseHeight*4, CONTROL_BLUE, false); // Control
        drawCompEllipse(gc, C3_REGISTERS + width*0.02, R_SIGN_EXTEND, ellipseWidth, ellipseHeight, Color.BLACK, false); // Sign-extend
        drawCompEllipse(gc, C4_ALU - width*0.02, R_SIGN_EXTEND, ellipseWidth, ellipseHeight, Color.BLUEVIOLET, false); // ALU Control

        double flagSize = width * 0.018;
        double flagY = R_MAIN_PATH - aluHeight*0.05 - flagSize - height*0.01; // Thêm khoảng cách nhỏ
        drawFlagBox(gc, "N", C4_ALU, flagY, flagSize, baseFontSize - 2);
        drawFlagBox(gc, "Z", C4_ALU + flagSize + 2, flagY, flagSize, baseFontSize - 2);
        drawFlagBox(gc, "C", C4_ALU + 2 * (flagSize + 2), flagY, flagSize, baseFontSize - 2);
        drawFlagBox(gc, "V", C4_ALU + 3 * (flagSize + 2), flagY, flagSize, baseFontSize - 2);
        // ===================================
        // 2. VẼ CÁC BỘ MUX
        // ===================================
        drawMux(gc, C2_CONTROL - 0.02*width, R_MAIN_PATH + 0.05*height, 0.02*width, 0.09*height, false,false); // MUX Register Input
        drawMux(gc, C4_ALU - 50, R_MAIN_PATH + 0.11*height,  0.02*width, 0.09*height, false,false); // MUX ALU Input
        drawMux(gc, C6_MUX - 0.02*width, R_BRANCH_LOGIC - 0.065*height,  0.02*width, 0.09*height, false,false); // MUX PC Source
        drawMux(gc, C6_MUX, R_MAIN_PATH + 0.6*aluHeight,  0.02*width, 0.09*height, false,false); // MUX MemToReg

        // // ===================================
        // // 3. VẼ CÁC CỔNG LOGIC
        // // ===================================
        double gateW = width * 0.025;
        double gateH = height * 0.03;

        // Vị trí X của các cổng logic, nằm giữa ALU và Data Memory
        double logicGatesX = C4_ALU ;

        // Vị trí Y của cổng AND trên
        double andGateY = flagY - 0.3 * gateH;

        // Vị trí Y của cổng AND dưới
        // double andGate2Y = R_CONTROL + height * 0.18;

        // Vị trí X, Y của cổng OR, nằm trước MUX chọn PC
        double orGateX = logicGatesX + width*0.18 + gateW;
        double orGateY = R_BRANCH_LOGIC + height * 0.05;

        // --- Vẽ các cổng ---
        drawAndGateHorizontal(gc, logicGatesX + width*0.1, andGateY, gateW, gateH, CONTROL_BLUE);
        drawAndGateHorizontal(gc, logicGatesX + width*0.18, andGateY, gateW, gateH, CONTROL_BLUE);
        drawOrGateHorizontal(gc, orGateX, orGateY, gateW * 1.2, gateH * 1.2);

        // // ===================================
        // // 4. VẼ CÁC ĐƯỜNG DÂY DỮ LIỆU (DATA PATH - BLACK)
        // // ===================================
        // // PC -> Add
        drawVerticalSegment(gc, C1_PC_IM + pcWidth*1.5, R_BRANCH_LOGIC*0.6, R_MAIN_PATH, CONTROL_BLUE, false, true); // PC to Add 4
        drawRightArrow(gc, C1_PC_IM + pcWidth*1.5, R_BRANCH_LOGIC*0.6, C1_PC_IM + width*0.13, CONTROL_BLUE, true); // PC to Add 4
        drawRightArrow(gc, C1_PC_IM + pcWidth, R_MAIN_PATH, C1_PC_IM + width*0.09, CONTROL_BLUE, true); // PC to Instruction Memory
        drawRightArrow(gc, C1_PC_IM + pcWidth*3, R_BRANCH_LOGIC*0.9, C1_PC_IM + width*0.13, CONTROL_BLUE, false); // PC to Add 4
        drawRightArrow(gc, C1_PC_IM - pcWidth*0.5, R_MAIN_PATH, C1_PC_IM, CONTROL_BLUE, false); // To PC
        drawVerticalSegment(gc, C1_PC_IM - pcWidth*0.5, R_MAIN_PATH, R_BRANCH_LOGIC*0.3,  CONTROL_BLUE, false, false); 
        drawHorizontalSegment(gc, C1_PC_IM - pcWidth*0.5, R_BRANCH_LOGIC*0.3, C6_MUX + 0.04*width, CONTROL_BLUE, false, false);
        drawVerticalSegment(gc, C6_MUX + 0.04*width, R_BRANCH_LOGIC*0.3, R_BRANCH_LOGIC*0.9, CONTROL_BLUE, false, false); // MUX to PC
        drawRightArrow(gc, C1_PC_IM + width*0.13 + pcWidth*1.2, R_BRANCH_LOGIC*0.75, C6_MUX - 0.02*width, CONTROL_BLUE, false); // To PC
        drawHorizontalSegment(gc, C6_MUX + 0.04*width, R_BRANCH_LOGIC*0.9, C6_MUX, CONTROL_BLUE, false, false); // MUX to PC
        drawRightArrow(gc, C3_REGISTERS + width*0.25 + pcWidth*1.5, R_BRANCH_LOGIC*1.1, C6_MUX - 0.02*width, CONTROL_BLUE, false); // Add 4 to MUX
        // // PC -> Instruction Memory
        // drawRightArrow(gc, C1_PC_IM + 60, R_MAIN_PATH, C1_PC_IM + 100, DATA_PATH_COLOR, true);
        // // PC+4 -> MUX
        // drawRightArrow(gc, C1_PC_IM + 180, R1_BRANCH_LOGIC, C5_DATAMEM - 50, DATA_PATH_COLOR, true);
        
        // // Instruction Memory Outputs
        // double imOutX = C1_PC_IM + 200;
        // // IM -> Registers
        // drawRightArrow(gc, imOutX, R3_MAIN_PATH - 70, C3_REGISTERS, DATA_PATH_COLOR, true);
        // drawRightArrow(gc, imOutX, R3_MAIN_PATH - 20, C3_REGISTERS, DATA_PATH_COLOR, true);
        // drawRightArrow(gc, imOutX, R3_MAIN_PATH + 20, C3_REGISTERS - 50, DATA_PATH_COLOR, true);
        // // IM -> Control
        // drawVerticalSegment(gc, imOutX + 40, R2_CONTROL + 100, R3_MAIN_PATH, DATA_PATH_COLOR, true, true);
        // drawRightArrow(gc, imOutX + 40, R2_CONTROL + 100, C2_CONTROL + 100, DATA_PATH_COLOR, false);
        // // IM -> Sign Extend
        // drawVerticalSegment(gc, imOutX + 80, R3_MAIN_PATH, R4_SIGN_EXTEND + 25, DATA_PATH_COLOR, true, true);
        // drawRightArrow(gc, imOutX + 80, R4_SIGN_EXTEND + 25, C3_REGISTERS + 20, DATA_PATH_COLOR, false);
        // // IM -> Shift Left
        // drawVerticalSegment(gc, imOutX + 110, R1_BRANCH_LOGIC + 15, R3_MAIN_PATH, DATA_PATH_COLOR, true, true);
        // drawRightArrow(gc, imOutX + 110, R1_BRANCH_LOGIC + 15, C3_REGISTERS, DATA_PATH_COLOR, false);
        
        // // Register Outputs -> ALU
        // drawRightArrow(gc, C3_REGISTERS + 120, R3_MAIN_PATH - 70, C4_ALU, DATA_PATH_COLOR, true);
        // drawRightArrow(gc, C3_REGISTERS + 120, R3_MAIN_PATH + 30, C4_ALU - 50, DATA_PATH_COLOR, true);
        // // Sign Extend -> MUX
        // drawVerticalSegment(gc, C3_REGISTERS + 60, R4_SIGN_EXTEND, R3_MAIN_PATH + 90, DATA_PATH_COLOR, true, true);
        // drawRightArrow(gc, C3_REGISTERS + 60, R3_MAIN_PATH + 90, C4_ALU - 50, DATA_PATH_COLOR, false);
        
        // // MUX -> ALU
        // drawRightArrow(gc, C4_ALU - 25, R3_MAIN_PATH + 65, C4_ALU, DATA_PATH_COLOR, true);
        
        // // ALU -> Data Mem / MUX
        // drawRightArrow(gc, C4_ALU + 100, R3_MAIN_PATH, C5_DATAMEM, DATA_PATH_COLOR, true);
        // drawVerticalSegment(gc, C4_ALU + 130, R3_MAIN_PATH, R3_MAIN_PATH - 15, DATA_PATH_COLOR, true, true);
        // drawRightArrow(gc, C4_ALU + 130, R3_MAIN_PATH - 15, C6_MUX, DATA_PATH_COLOR, false);
        
        // // Data Mem -> MUX
        // drawRightArrow(gc, C5_DATAMEM + 120, R3_MAIN_PATH + 15, C6_MUX, DATA_PATH_COLOR, true);
        
        // // Write Back Path
        // double wb_bus_y = R4_SIGN_EXTEND + 50;
        // drawHorizontalSegment(gc, C3_REGISTERS, wb_bus_y, C6_MUX + 25, DATA_PATH_COLOR, false, true);
        // drawVerticalSegment(gc, C3_REGISTERS, R3_MAIN_PATH + 100, wb_bus_y, DATA_PATH_COLOR, true, true);
        // drawLeftArrow(gc, C3_REGISTERS + 60, R3_MAIN_PATH + 100, C3_REGISTERS + 120, DATA_PATH_COLOR, false);

        // // Branch Logic Path
        // drawRightArrow(gc, C3_REGISTERS + 80, R1_BRANCH_LOGIC + 15, C3_REGISTERS + 140, DATA_PATH_COLOR, true);
        // drawVerticalSegment(gc, C1_PC_IM - 30, R1_BRANCH_LOGIC - 30, R1_BRANCH_LOGIC, DATA_PATH_COLOR, true, false);
        // drawRightArrow(gc, C1_PC_IM - 30, R1_BRANCH_LOGIC - 30, C3_REGISTERS + 140, DATA_PATH_COLOR, true);
        // drawRightArrow(gc, C3_REGISTERS + 200, R1_BRANCH_LOGIC, C5_DATAMEM - 50, DATA_PATH_COLOR, true);
        
        // // Final PC Path
        // double pc_final_bus_y = R1_BRANCH_LOGIC - 50;
        // drawVerticalSegment(gc, C1_PC_IM - 60, pc_final_bus_y, R3_MAIN_PATH, DATA_PATH_COLOR, true, true);
        // drawHorizontalSegment(gc, C1_PC_IM - 60, pc_final_bus_y, C5_DATAMEM - 25, DATA_PATH_COLOR, false, true);
        // drawLeftArrow(gc, C1_PC_IM, R3_MAIN_PATH, C1_PC_IM - 60, DATA_PATH_COLOR, false);

        // // ===================================
        // // 5. VẼ CÁC ĐƯỜNG ĐIỀU KHIỂN (CONTROL PATH - BLUE)
        // // ===================================
        // // Control -> MUXes
        // drawHorizontalSegment(gc, C2_CONTROL + 100, R2_CONTROL + 125, C4_ALU - 50, CONTROL_PATH_COLOR, true, true); // ALUSrc
        // drawHorizontalSegment(gc, C2_CONTROL + 100, R2_CONTROL + 155, C6_MUX + 25, CONTROL_PATH_COLOR, true, true); // RegWrite
        // //... thêm các đường khác...

        // // ALU Zero -> AND gate
        // drawRightArrow(gc, C4_ALU + 100, R3_MAIN_PATH - 60, C4_ALU + 120, CONTROL_PATH_COLOR, true);
        
        // // Logic Gates -> OR Gate -> MUX
        // drawRightArrow(gc, C4_ALU + 150, R2_CONTROL + 10, C5_DATAMEM - 100, CONTROL_PATH_COLOR, true);
        // drawRightArrow(gc, C4_ALU + 150, R2_CONTROL + 40, C5_DATAMEM - 100, CONTROL_PATH_COLOR, true);
        // drawRightArrow(gc, C5_DATAMEM - 60, R1_BRANCH_LOGIC + 62.5, C5_DATAMEM - 50, CONTROL_PATH_COLOR, true);

        // // ===================================
        // // 6. VẼ CÁC NHÃN (LABELS)
        // // ===================================
        // drawText(gc, "PC", C1_PC_IM + 30, R_MAIN_PATH, BLACK, 14);
        // drawText(gc, "Instruction\nmemory", C1_PC_IM + 150, R_MAIN_PATH, BLACK, 14);
        // drawText(gc, "Registers", C3_REGISTERS + 60, R_MAIN_PATH, BLACK, 16);
        // drawText(gc, "ALU", C4_ALU + 50, R_MAIN_PATH, BLACK, 16);
        // drawText(gc, "Data\nmemory", C5_DATAMEM + 60, R_MAIN_PATH, BLACK, 16);
        // drawText(gc, "Control", C2_CONTROL + 50, R_CONTROL + 100, BLACK, 16);
        // drawText(gc, "Add", C1_PC_IM + 150, R_BRANCH_LOGIC + 15, BLACK, 12);
        // drawText(gc, "4", C1_PC_IM + 130, R_BRANCH_LOGIC + 35, BLACK, 12);
        // drawText(gc, "Shift\nleft 2", C3_REGISTERS + 40, R_BRANCH_LOGIC + 20, BLACK, 12);
        // drawText(gc, "Add", C3_REGISTERS + 170, R_BRANCH_LOGIC + 15, BLACK, 12);
        // drawText(gc, "Sign-\nextend", C3_REGISTERS + 60, R_SIGN_EXTEND + 28, BLACK, 12);
        // drawText(gc, "ALU\ncontrol", C4_ALU + 60, R_SIGN_EXTEND + 28, BLACK, 12);
        
        // gc.setTextAlign(TextAlignment.LEFT);
        // drawText(gc, "Instruction [31-0]", imOutX + 85, R4_SIGN_EXTEND + 50, BLACK, 10);
        // drawText(gc, "Instruction [25-0]", imOutX + 115, R1_BRANCH_LOGIC + 35, BLACK, 10);
        // gc.setTextAlign(TextAlignment.CENTER);
    }
}