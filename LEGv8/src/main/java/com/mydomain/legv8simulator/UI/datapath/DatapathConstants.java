package main.java.com.mydomain.legv8simulator.UI.datapath;

import javafx.scene.text.Font;

/**
 * Lớp này chứa các hằng số TỶ LỆ để vẽ Datapath của MIPS.
 *
 * <p><b>QUAN TRỌNG:</b> Để giữ sự tương đồng với file gốc, tên các biến được giữ nguyên
 * (ví dụ: pcRectX, pcRectY). Tuy nhiên, giá trị của chúng KHÔNG phải là tọa độ cuối cùng,
 * mà là <strong>TỶ LỆ</strong> so với chiều rộng hoặc chiều cao của vùng chứa.</p>
 *
 * <p><b>CÁCH SỬ DỤNG:</b></p>
 * <pre>
 * {@code
 * // Giả sử 'pane' là vùng vẽ của bạn
 * double width = pane.getWidth();
 * double height = pane.getHeight();
 *
 * // Tính toán tọa độ và kích thước thực tế
 * double actualPcRectX = width * DatapathConstants.pcRectX;
 * double actualPcRectY = height * DatapathConstants.pcRectY;
 * double actualPcRectWidth = width * DatapathConstants.pcRectWidth;
 * double actualPcRectHeight = height * DatapathConstants.pcRectHeight;
 *
 * // Tạo hình chữ nhật
 * Rectangle pc = new Rectangle(actualPcRectX, actualPcRectY, actualPcRectWidth, actualPcRectHeight);
 * }
 * </pre>
 */
public final class DatapathConstants {

    // Constructor private để ngăn việc tạo đối tượng từ lớp tiện ích này.
    private DatapathConstants() {}

    // --- HỆ LƯỚI TỌA ĐỘ TƯƠNG ĐỐI (DỰA TRÊN TỶ LỆ) ---
    // Các cột X được tính bằng phần trăm của chiều rộng
    public static final double C1_PC_IM = 0.08;
    public static final double C2_CONTROL = 0.4;
    public static final double C3_REGISTERS = 0.45;
    public static final double C4_ALU = 0.64;
    public static final double C5_DATAMEM = 0.81;
    public static final double C6_MUX = 0.925;

    // Các hàng Y được tính bằng phần trăm của chiều cao
    public static final double R_BRANCH_LOGIC = 0.18;
    public static final double R_CONTROL = 0.35;
    public static final double R_MAIN_PATH = 0.55;
    public static final double R_SIGN_EXTEND = 0.8;

    // Kích thước các khối được tính theo tỷ lệ
    public static final double rectWidth = 0.1;      // so với width
    public static final double rectHeight = 0.2;     // so với height
    public static final double aluWidth = 0.085;     // so với width
    public static final double aluHeight = 0.2;      // so với height
    public static final double pcWidth = 0.035;      // so với width
    public static final double pcHeight = 0.09;      // so với height
    public static final double ellipseWidth = 0.05;  // so với width
    public static final double ellipseHeight = 0.07; // so với height

    // Kích thước font chữ co giãn
    public static final double BASE_FONT_SIZE_RATIO = 1.0 / 90.0; // Tỷ lệ font so với chiều rộng
    public static final int MIN_FONT_SIZE = 8;

    // ===================================
    // VỊ TRÍ VÀ KÍCH THƯỚC CÁC KHỐI
    // Các giá trị này được tính toán từ các tỷ lệ cơ bản ở trên
    // ===================================

    // --- VỊ TRÍ CÁC KHỐI CHÍNH ---
    public static final double pcRectX = C1_PC_IM;
    public static final double pcRectY = R_MAIN_PATH - pcHeight / 2.0;

    public static final double instrMemX = C1_PC_IM + 0.08;
    public static final double instrMemY = R_MAIN_PATH - 0.02;

    public static final double regX = C3_REGISTERS;
    public static final double regY = R_MAIN_PATH - 0.01;

    public static final double aluX = C4_ALU;
    public static final double aluY = R_MAIN_PATH - aluHeight * 0.05;

    public static final double dataMemX = C5_DATAMEM;
    public static final double dataMemY = R_MAIN_PATH + 0.4 * aluHeight;

    // --- VỊ TRÍ KHỐI LOGIC TRÊN CÙNG ---
    public static final double add4X = C1_PC_IM + 0.13;
    public static final double add4Y = R_BRANCH_LOGIC - pcHeight;

    public static final double shiftLeft2X = C3_REGISTERS + 0.15;
    public static final double shiftLeft2Y = R_BRANCH_LOGIC * 1.1;

    public static final double addBranchX = C3_REGISTERS + 0.25;
    public static final double addBranchY = R_BRANCH_LOGIC - pcHeight * 0.4;

    // --- VỊ TRÍ KHỐI ĐIỀU KHIỂN VÀ MỞ RỘNG ---
    public static final double controlX = C2_CONTROL;
    public static final double controlY = R_CONTROL - 0.1;

    public static final double signExtendX = C3_REGISTERS + 0.02;
    public static final double signExtendY = R_SIGN_EXTEND;

    public static final double aluControlX = C4_ALU - 0.025;
    public static final double aluControlY = R_SIGN_EXTEND * 1.02;

    public static final double flagSize = 0.018; // so với width
    public static final double flagX = C4_ALU + 0.05 * aluWidth;
    public static final double flagY = R_MAIN_PATH - aluHeight * 0.05 - flagSize - 0.01;

    // --- VỊ TRÍ MUX ---
    public static final double muxRegInputX = C2_CONTROL - 0.02;
    public static final double muxRegInputY = R_MAIN_PATH + 0.03;

    public static final double muxAluInputX = C4_ALU - 0.04;
    public static final double muxAluInputY = R_MAIN_PATH + 0.11;

    public static final double muxPcSourceX = C6_MUX - 0.02;
    public static final double muxPcSourceY = R_BRANCH_LOGIC - 0.065;

    public static final double muxMemToRegX = C6_MUX;
    public static final double muxMemToRegY = R_MAIN_PATH + 0.6 * aluHeight;

    // --- KHAI BÁO KÍCH THƯỚC CỤ THỂ CỦA CÁC KHỐI ---
    // Program Counter
    public static final double pcRectWidth = pcWidth;
    public static final double pcRectHeight = pcHeight;
    // Instruction Memory
    public static final double instrMemWidth = rectWidth;
    public static final double instrMemHeight = rectHeight;
    // Registers
    public static final double regWidth = rectWidth;
    public static final double regHeight = rectHeight * 1.16;
    // ALU
    public static final double aluBlockWidth = aluWidth;
    public static final double aluBlockHeight = aluHeight;
    // Data Memory
    public static final double dataMemWidth = rectWidth * 0.9;
    public static final double dataMemHeight = rectHeight * 0.9;
    // Add 4
    public static final double add4Width = pcWidth;
    public static final double add4Height = pcHeight * 0.8;
    // Shift left 2
    public static final double shiftLeft2Width = ellipseWidth;
    public static final double shiftLeft2Height = ellipseHeight;
    // Add Branch
    public static final double addBranchWidth = pcWidth * 1.5;
    public static final double addBranchHeight = pcHeight * 1.2;
    // Control
    public static final double controlWidth = ellipseWidth * 1.2;
    public static final double controlHeight = ellipseHeight * 4.0;
    // Sign-extend
    public static final double signExtendWidth = ellipseWidth;
    public static final double signExtendHeight = ellipseHeight;
    // ALU Control
    public static final double aluControlWidth = ellipseWidth;
    public static final double aluControlHeight = ellipseHeight;
    // Flag Box
    public static final double flagBoxSize = flagSize;
    // MUX
    public static final double muxWidth = 0.02;
    public static final double muxHeight = 0.09;


    /**
     * Tính toán và trả về một đối tượng Font co giãn theo chiều rộng của container.
     * @param containerWidth Chiều rộng hiện tại của container (ví dụ: pane.getWidth()).
     * @return Một đối tượng Font với kích thước đã được tính toán.
     */
    public static Font getResponsiveFont(double containerWidth) {
        double calculatedSize = containerWidth * BASE_FONT_SIZE_RATIO;
        return Font.font("Arial", Math.max(calculatedSize, MIN_FONT_SIZE));
    }
}