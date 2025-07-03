package main.java.com.mydomain.legv8simulator.UI.datapath;

import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * DatapathGraphicsFX là một thư viện đồ họa để vẽ các sơ đồ trên JavaFX Canvas.
 * Đây là phiên bản chuyển đổi từ thư viện GWT gốc.
 * <p>
 * Các đoạn dây và mũi tên nên luôn được vẽ từ đuôi đến đầu để đảm bảo các khớp nối hoàn hảo.
 *
 * @author Jonathan Wright, 2016 (Bản gốc GWT)
 * @author Chuyển đổi sang JavaFX, 2023
 */
public final class DatapathGraphicsFX {

    // Private constructor để ngăn việc tạo instance
    private DatapathGraphicsFX() {}

    public static final Color RED = Color.rgb(255, 77, 77);
    public static final Color BLACK = Color.BLACK;
    public static final Color WHITE = Color.WHITE;
    public static final Color GREY = Color.rgb(242, 242, 242);
    public static final Color CONTROL_SIGNAL = Color.web("#3BA0E2");
    public static final Color ARM_BLUE = Color.web("#44CAF3");
    public static final Color pcBorderColor = Color.BLACK;
    public static final Color pcFillColor = Color.web("#ffe3e3");
    public static final Color instrMemBorderColor = Color.BLACK;
    public static final Color instrMemFillColor = Color.web("#e3f6e3");
    public static final Color regBorderColor = Color.BLACK;
    public static final Color regFillColor = Color.web("#ffe3ec");
    public static final Color aluBorderColor = Color.BLACK;
    public static final Color aluFillColor = Color.web("#f3e5f5");
    public static final Color dataMemBorderColor = Color.BLACK;
    public static final Color dataMemFillColor = Color.web("#fde2e2");

    public static final Color add4BorderColor = Color.web("#1C2B2B");
    public static final Color add4FillColor = Color.web("#f0f4f8");
    public static final Color shiftLeft2BorderColor = Color.web("#1C2B2B");
    public static final Color shiftLeft2FillColor = Color.web("#f9f9f9");
    public static final Color addBranchBorderColor = Color.web("#1C2B2B");
    public static final Color addBranchFillColor = Color.web("#f0f4f8");

    public static final Color controlBorderColor = Color.web("#12C0E8");
    public static final Color controlFillColor = Color.web("#E1F5FE");
    public static final Color signExtendBorderColor = Color.web("#1C2B2B");
    public static final Color signExtendFillColor = Color.web("#f0f4f8");
    public static final Color aluControlBorderColor = Color.web("#12C0E8");
    public static final Color aluControlFillColor = Color.web("#E1F5FE");

    public static final Color muxBorderColor = Color.web("#1C2B2B");
    public static final Color muxFillColor = Color.web("#E6E6FA");

    public static final Color Highlight = Color.web("#FF1744");
    public static final Color HighlightControl = Color.rgb(136, 70, 234);

    public static final Color HighlightBorder = Color.RED; // Màu nhạt hơn cho các tín hiệu điều khiển
    public static final Color HighlightFill = Color.web("#ffffb5", 0.8); // Màu đậm hơn cho các tín hiệu điều khiển

    public static final Color HighlightText = Color.web("#ff6347"); // Màu đỏ tươi cho văn bản highlight 

    public static double width, height;

    // --- Biến toàn cục cho layout ---
    public static double C1_PC_IM, C2_CONTROL, C3_REGISTERS, C4_ALU, C5_DATAMEM, C6_MUX;
    public static double R_BRANCH_LOGIC, R_CONTROL, R_MAIN_PATH, R_SIGN_EXTEND;
    public static double rectWidth, rectHeight, aluWidth, aluHeight, pcWidth, pcHeight, ellipseWidth, ellipseHeight;
    public static double pcRectX, pcRectY, instrMemX, instrMemY, regX, regY, aluX, aluY, dataMemX, dataMemY;
    public static double add4X, add4Y, shiftLeft2X, shiftLeft2Y, addBranchX, addBranchY;
    public static double controlX, controlY, signExtendX, signExtendY, aluControlX, aluControlY;
    public static double flagSize, flagX, flagY;
    public static double muxRegInputX, muxRegInputY, muxAluInputX, muxAluInputY, muxPcSourceX, muxPcSourceY, muxMemToRegX, muxMemToRegY;
    public static double pcRectWidth, pcRectHeight, instrMemWidth, instrMemHeight, regWidth, regHeight, aluBlockWidth, aluBlockHeight;
    public static double dataMemWidth, dataMemHeight, add4Width, add4Height, shiftLeft2Width, shiftLeft2Height, addBranchWidth, addBranchHeight;
    public static double controlWidth, controlHeight, signExtendWidth, signExtendHeight, aluControlWidth, aluControlHeight, flagBoxSize;
    public static double muxWidth, muxHeight;
    public static double gateW, gateH;
    public static double andGate1X, andGate2X, orGateX;
    public static double andGateY, orGateY;
    public static int baseFontSize, portFontSize;


    /**
     * Vẽ một gạch chéo để thể hiện việc mở rộng dấu hoặc đệm zero.
     *
     * @param gc    GraphicsContext của canvas để vẽ.
     * @param x     Tọa độ x của điểm giữa gạch chéo.
     * @param y     Tọa độ y của điểm giữa gạch chéo.
     * @param color Màu của gạch chéo.
     */
    public static void drawDiagSlash(GraphicsContext gc, double x, double y, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(1.5);
        gc.beginPath();
        gc.moveTo(x - 5, y - 5);
        gc.lineTo(x + 6, y + 7);
        gc.stroke();
        gc.closePath();
    }

    /**
     * Vẽ một cổng AND nằm ngang với nền trắng và màu viền được chỉ định.
     *
     * @param gc     GraphicsContext của canvas để vẽ.
     * @param x      Tọa độ x của góc trên bên trái cổng AND.
     * @param y      Tọa độ y của góc trên bên trái cổng AND.
     * @param width  Chiều rộng của cổng AND.
     * @param height Chiều cao của cổng AND.
     * @param color  Màu viền của cổng AND.
     */
    public static void drawAndGateHorizontal(GraphicsContext gc, double x, double y,
                                             double width, double height, Color color) {
        gc.setStroke(color);
        gc.setFill(WHITE);
        gc.setLineWidth(2);
        gc.beginPath();
        gc.moveTo(x + width / 2, y);
        gc.lineTo(x, y);
        gc.lineTo(x , y + height);
        gc.lineTo(x + width/2, y + height);
        // Vẽ cung tròn nửa hình tròn
        gc.arc(x + (width / 2), y + height / 2, height / 2, height / 2, 270, 180);
        gc.fill();
        gc.stroke();
        gc.closePath();
    }
    
    /**
     * Vẽ một cổng AND nằm dọc.
     */
    public static void drawAndGateVertical(GraphicsContext gc, double x, double y,
                                           double width, double height, Color color) {
        gc.setStroke(color);
        gc.setFill(WHITE);
        gc.setLineWidth(2);
        gc.beginPath();
        gc.moveTo(x, y + height / 2);
        // Vẽ cung tròn nửa hình tròn
        gc.arc(x + width / 2, y + height / 2, width / 2, width / 2, 180, 180);
        gc.moveTo(x+width, y+height/2);
		gc.lineTo(x+width, y+height);
		gc.lineTo(x, y+height);
		gc.lineTo(x, y+height/2);
		gc.fill();
		gc.stroke();
		gc.closePath();
    }
    
    /**
     * Vẽ một cổng OR nằm ngang.
     * Ghi chú: Logic vẽ được đơn giản hóa bằng đường cong bậc hai (quadratic curves)
     * để dễ đọc hơn so với các lệnh `arc` phức tạp của bản gốc.
     */
    public static void drawOrGateHorizontal(GraphicsContext gc, double x, double y,
                                            double width, double height, Color color) {
        gc.setStroke(color);
        gc.setFill(WHITE);
        gc.setLineWidth(2);
        gc.beginPath();
        gc.moveTo(x, y);
        gc.quadraticCurveTo(x + width * 0.7, y, x + width, y + height / 2); // Đường cong trên
        gc.quadraticCurveTo(x + width * 0.7, y + height, x, y + height); // Đường cong dưới
        gc.quadraticCurveTo(x + width * 0.2, y + height / 2, x, y); // Đường cong sau
        gc.closePath();
        gc.fill();
        gc.stroke();
    }

    /**
     * Vẽ một cổng OR nằm dọc.
     */
    public static void drawOrGateVertical(GraphicsContext gc, double x, double y,
                                          double width, double height, Color color) {
        gc.setStroke(color);
        gc.setFill(WHITE);
        gc.setLineWidth(2);
        gc.beginPath();
        gc.moveTo(x, y);
        gc.quadraticCurveTo(x, y + height * 0.7, x + width / 2, y + height); // Đường cong trái
        gc.quadraticCurveTo(x + width, y + height * 0.7, x + width, y); // Đường cong phải
        gc.quadraticCurveTo(x + width / 2, y + height * 0.2, x, y); // Đường cong trên
        gc.closePath();
        gc.fill();
        gc.stroke();
    }

    /**
     * Vẽ một hình chữ nhật thành phần với highlight tùy chọn.
     */
    public static void drawCompRect(GraphicsContext gc, double x, double y,
                                    double width, double height, boolean highlightLeft, boolean highlightRight) {
        gc.setStroke(BLACK);
        gc.setLineWidth(2);
        
        gc.setFill(highlightLeft ? RED : GREY);
        gc.fillRect(x, y, width / 2, height);
        
        gc.setFill(highlightRight ? RED : GREY);
        gc.fillRect(x + width / 2, y, width / 2, height);
        
        gc.strokeRect(x, y, width, height);
    }

    public static void drawCompRect(GraphicsContext gc, double x, double y,
                                    double width, double height) {
        drawCompRect(gc, x, y, width, height, false, false);
    }
    
    public static void drawCompRect(GraphicsContext gc, double x, double y,
                                    double width, double height, Color colorLine, Color colorFill) {
        gc.setStroke(colorLine);
        gc.setFill(colorFill); // Giả định fill cùng màu với stroke
        gc.setLineWidth(2);
        gc.fillRect(x, y, width, height);
        gc.strokeRect(x, y, width, height);
    }

    public static void drawCompRect(GraphicsContext gc, double x, double y,
                                    double width, double height, Color colorLine, Color colorFill, boolean highlight) {
        gc.setStroke((highlight)? HighlightBorder : colorLine);
        if(!highlight)
        {
            gc.setFill(colorFill); 
            gc.fillRect(x, y, width, height);
        } 
        gc.setLineWidth(2);
        gc.strokeRect(x, y, width, height);
    }

    /**
     * Vẽ một hình ellipse thành phần với highlight tùy chọn.
     */
    public static void drawCompEllipse(GraphicsContext gc, double x, double y,
                                       double width, double height, Color lineColor, Color fillColor, boolean highlight) {
        drawEllipse(gc, x, y, width, height, highlight ? HighlightBorder : lineColor, highlight ? HighlightFill : fillColor, highlight);
    }
    
    /**
     * Vẽ một hình ellipse. Logic sử dụng đường cong bezier được giữ nguyên.
     */
    public static void drawEllipse(GraphicsContext gc, double x, double y, double width, double height,
                                   Color stroke, Color fill, boolean highlight) {
        double kappa = 0.5522848;
        double ox = (width / 2) * kappa;
        double oy = (height / 2) * kappa;
        double xe = x + width;
        double ye = y + height;
        double xm = x + width / 2;
        double ym = y + height / 2;

        gc.setLineWidth(2);
        gc.setStroke(stroke);
        gc.setFill(fill); 
        gc.beginPath();
        gc.moveTo(x, ym);
        gc.bezierCurveTo(x, ym - oy, xm - ox, y, xm, y);
        gc.bezierCurveTo(xm + ox, y, xe, ym - oy, xe, ym);
        gc.bezierCurveTo(xe, ym + oy, xm + ox, ye, xm, ye);
        gc.bezierCurveTo(xm - ox, ye, x, ym + oy, x, ym);
        gc.closePath();
        if(!highlight) gc.fill();
        gc.stroke();
    }

    /**
     * Vẽ một ALU (Arithmetic Logic Unit).
     */
    public static void drawALU(GraphicsContext gc, double x, double y, double width, double height, Color colorLine, Color colorFill, boolean highlight) {
        gc.setStroke(highlight ? HighlightBorder : colorLine);
        if(!highlight) gc.setFill(colorFill); 
        gc.setLineWidth(2);

        gc.beginPath();
        gc.moveTo(x, y);
        gc.lineTo(x + width, y + (height / 4));
        gc.lineTo(x + width, y + (3 * height / 4));
        gc.lineTo(x, y + height);
        gc.lineTo(x, y + height - (3 * height / 8));
        gc.lineTo(x + width / 5, y + height / 2);
        gc.lineTo(x, y + (3 * height / 8));
        gc.lineTo(x, y);
        gc.closePath();
        if(!highlight) gc.fill();
        gc.stroke();
    }
    
    /**
     * Vẽ một đoạn dây dọc.
     */
    public static void drawVerticalSegment(GraphicsContext gc, double x, double yStart, double yEnd,
                                           Color color, boolean joinStart, boolean joinEnd, double lineWidth) {
        gc.setFill(color);
        gc.setStroke(color);
        gc.setLineWidth(lineWidth);

        if (joinStart) {
            gc.fillOval(x - 3.5, yStart - 4, 8, 8); // Vẽ vòng tròn nối
        }
        if (joinEnd) {
            gc.fillOval(x - 3.5, yEnd - 4, 8, 8); // Vẽ vòng tròn nối
        }
        // yStart thường lớn hơn yEnd trong hệ tọa độ canvas
        gc.fillRect(x - lineWidth/2, Math.min(yStart, yEnd), lineWidth, Math.abs(yEnd - yStart));
    }

    public static void drawVerticalSegment(GraphicsContext gc, double x, double yStart, double yEnd,
                                           Color color, boolean joinStart, boolean joinEnd) {
        drawVerticalSegment(gc, x, yStart, yEnd, color, joinStart, joinEnd, 2);
    }
    
    /**
     * Vẽ một đoạn dây ngang.
     */
    public static void drawHorizontalSegment(GraphicsContext gc, double xStart, double y, double xEnd,
                                             Color color, boolean joinStart, boolean joinEnd, double lineWidth) {
        gc.setFill(color);
        gc.setStroke(color);
        gc.setLineWidth(lineWidth);

        if (joinStart) {
            gc.fillOval(xStart - 4, y - 3.5, 8, 8);
        }
        if (joinEnd) {
            gc.fillOval(xEnd - 4, y - 3.5, 8, 8);
        }
        gc.fillRect(Math.min(xStart, xEnd), y - lineWidth/2, Math.abs(xEnd - xStart), lineWidth);
    }
    
    public static void drawHorizontalSegment(GraphicsContext gc, double xStart, double y, double xEnd,
                                             Color color, boolean joinStart, boolean joinEnd) {
        drawHorizontalSegment(gc, xStart, y, xEnd, color, joinStart, joinEnd, 2);
    }
    
    /**
     * Vẽ một mũi tên dọc chỉ lên trên.
     */
    public static void drawUpArrow(GraphicsContext gc, double x, double yTail, double yHead,
                                   Color color, boolean join) {
        gc.setLineWidth(2);
        gc.setStroke(color);
        gc.setFill(color);

        if (join) {
            gc.fillOval(x - 3.5, yTail - 4, 8, 8);
        }
        gc.fillRect(x - 1, yHead + 8, 2, yTail - yHead - 8);
        drawUpArrowHead(gc, x, yHead);
    }

    /**
     * Vẽ một mũi tên ngang chỉ sang phải.
     */
    public static void drawRightArrow(GraphicsContext gc, double xTail, double y, double xHead,
                                      Color color, boolean join) {
        gc.setLineWidth(2);
        gc.setStroke(color);
        gc.setFill(color);

        if (join) {
            gc.fillOval(xTail - 4, y - 3.5, 8, 8);
        }
        gc.fillRect(xTail, y - 1, xHead - 8 - xTail, 2);
        drawRightArrowHead(gc, xHead, y);
    }

    /**
     * Vẽ một mũi tên ngang chỉ sang trái.
     */
    public static void drawLeftArrow(GraphicsContext gc, double xTail, double y, double xHead,
                                     Color color, boolean join) {
        gc.setLineWidth(2);
        gc.setStroke(color);
        gc.setFill(color);

        if (join) {
            gc.fillOval(xTail - 4, y - 3.5, 8, 8);
        }
        // xHead < xTail
        gc.fillRect(xHead + 8, y - 1, xTail - (xHead + 8), 2);
        drawLeftArrowHead(gc, xHead, y);
    }

    private static void drawRightArrowHead(GraphicsContext gc, double x, double y) {
        double[] xPoints = {x, x - 10, x - 10};
        double[] yPoints = {y, y - 5, y + 5};
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    private static void drawUpArrowHead(GraphicsContext gc, double x, double y) {
        double[] xPoints = {x, x - 5, x + 5};
        double[] yPoints = {y, y + 10, y + 10};
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    private static void drawLeftArrowHead(GraphicsContext gc, double x, double y) {
        double[] xPoints = {x, x + 10, x + 10};
        double[] yPoints = {y, y - 5, y + 5};
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    //... trong lớp DatapathGraphicsFX
    /**
     * Vẽ một bộ MUX (Multiplexer) bao gồm hai nửa hình tròn trên và dưới.
     * Đây là phiên bản chuyển đổi từ mã GWT Canvas gốc.
     *
     * @param gc             GraphicsContext để vẽ.
     * @param x              Tọa độ x của góc trên bên trái.
     * @param y              Tọa độ y của góc trên bên trái.
     * @param width          Chiều rộng của MUX.
     * @param height         Chiều cao tổng thể của MUX.
     * @param highlightTop   Highlight nửa trên nếu được chọn.
     * @param highlightBottom Highlight nửa dưới nếu được chọn.
     */
    public static void drawMux(GraphicsContext gc, double x, double y, double width, double height,
                            boolean highlightTop, boolean highlightBottom) {

        double radius = width / 2;
        double topArcCenterY = y + radius;
        double bottomArcCenterY = y + height - radius;

        gc.setFill(highlightTop ? HighlightFill : muxFillColor);
        gc.beginPath();
        gc.moveTo(x, y + height / 2);
        gc.lineTo(x, topArcCenterY);
        gc.arc(x + radius, topArcCenterY, radius, radius, 180, -180);
        gc.lineTo(x + width, y + height / 2);
        gc.closePath();
        gc.fill();

        gc.setFill(highlightBottom ? HighlightFill : muxFillColor);
        gc.beginPath();
        gc.moveTo(x + width, y + height / 2);
        gc.lineTo(x + width, bottomArcCenterY);
        gc.arc(x + radius, bottomArcCenterY, radius, radius, 0, 180);
        gc.lineTo(x, y + height / 2);
        gc.closePath();
        gc.fill();
        
        gc.setStroke(BLACK);
        gc.setLineWidth(2);
        gc.beginPath();
        
        gc.moveTo(x, y + height / 2);
        gc.lineTo(x, topArcCenterY);
        gc.arc(x + radius, topArcCenterY, radius, radius, 180, -180);
        
        gc.lineTo(x + width, bottomArcCenterY);
        gc.arc(x + radius, bottomArcCenterY, radius, radius, 0, -180);
        
        gc.closePath();
        
        gc.stroke();

        // Text
        drawText(gc, "0", x + width * 0.5, y + height * 0.15, BLACK, (int) (0.2 * height - 2), TextAlignment.CENTER);
        drawTextBold(gc, "M\ru\rx", x + width * 0.5, y + height * 0.5, BLACK, (int) (0.2 * height - 2), TextAlignment.CENTER);
        drawText(gc, "1", x + width * 0.5, y + height * 0.85, BLACK, (int) (0.2 * height - 2), TextAlignment.CENTER);
    }
    /**
     * Vẽ một bộ MUX (Multiplexer) với màu viền và màu nền giống nhau cho cả hai nửa.
     *
     * @param gc             GraphicsContext để vẽ.
     * @param x              Tọa độ x của góc trên bên trái.
     * @param y              Tọa độ y của góc trên bên trái.
     * @param width          Chiều rộng của MUX.
     * @param height         Chiều cao tổng thể của MUX.
     * @param borderColor    Màu viền.
     * @param fillColor      Màu nền cho cả hai nửa.
     */
    public static void drawMux(GraphicsContext gc, double x, double y, double width, double height,
                               Color borderColor, Color fillColor, boolean highlight) {

        double radius = width / 2;
        double topArcCenterY = y + radius;
        double bottomArcCenterY = y + height - radius;

        // Top half
        gc.setFill(highlight?HighlightFill:fillColor);
        gc.beginPath();
        gc.moveTo(x, y + height / 2);
        gc.lineTo(x, topArcCenterY);
        gc.arc(x + radius, topArcCenterY, radius, radius, 180, -180);
        gc.lineTo(x + width, y + height / 2);
        gc.closePath();
        gc.fill();

        // Bottom half
        gc.setFill(highlight?HighlightFill:fillColor);
        gc.beginPath();
        gc.moveTo(x + width, y + height / 2);
        gc.lineTo(x + width, bottomArcCenterY);
        gc.arc(x + radius, bottomArcCenterY, radius, radius, 0, -180);
        gc.lineTo(x, y + height / 2);
        gc.closePath();
        gc.fill();

        // Border - highlight all if needed
        gc.setLineWidth(2);
        gc.setStroke(highlight ? HighlightBorder : borderColor);
        gc.beginPath();
        gc.moveTo(x, y + height / 2);
        gc.lineTo(x, topArcCenterY);
        gc.arc(x + radius, topArcCenterY, radius, radius, 180, -180);
        gc.lineTo(x + width, bottomArcCenterY);
        gc.arc(x + radius, bottomArcCenterY, radius, radius, 0, -180);
        gc.closePath();
        gc.stroke();

        // Text
        drawText(gc, "0", x + width * 0.5, y + height * 0.15, BLACK, (int) (0.2 * height - 2), TextAlignment.CENTER);
        drawTextBold(gc, "M\ru\rx", x + width * 0.5, y + height * 0.5, BLACK, (int) (0.2 * height - 2), TextAlignment.CENTER);
        drawText(gc, "1", x + width * 0.5, y + height * 0.85, BLACK, (int) (0.2 * height - 2), TextAlignment.CENTER);
    }
    // HÀM VẼ CỔNG OR
    public static void drawOrGateHorizontal(GraphicsContext gc, double x, double y, double width, double height) {
        gc.setStroke(CONTROL_SIGNAL);
        gc.setFill(WHITE);
        gc.setLineWidth(1.5);
        gc.beginPath();
        gc.moveTo(x, y);
        gc.quadraticCurveTo(x + width * 0.8, y, x + width, y + height / 2);
        gc.quadraticCurveTo(x + width * 0.8, y + height, x, y + height);
        gc.quadraticCurveTo(x + width * 0.3, y + height / 2, x, y);
        gc.closePath();
        gc.fill();
        gc.stroke();
    }
    
    public static void drawText(GraphicsContext gc, String text, double x, double y, Color color, int size, TextAlignment alignment) {
    gc.setFill(color);
    gc.setFont(new Font("Arial", size));
    gc.setTextAlign(alignment);
    gc.setTextBaseline(VPos.CENTER); 
    gc.fillText(text, x, y);
    }

    public static void drawTextBold(GraphicsContext gc, String text, double x, double y, Color color, int size, TextAlignment alignment) {
        Font originalFont = gc.getFont();
        Font boldFont = Font.font(originalFont.getFamily(), FontWeight.BOLD, size);
        gc.setFont(boldFont);
        gc.setFill(color);
        gc.setTextAlign(alignment);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(text, x, y);
        gc.setFont(originalFont);
    }

    public static void drawFlagBox(GraphicsContext gc, String label, double x, double y, double size, int fontSize) {
        gc.setStroke(BLACK);
        gc.setFill(GREY);
        gc.setLineWidth(1.5);
        gc.fillRect(x, y, size, size);
        gc.strokeRect(x, y, size, size);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER); 
        drawTextBold(gc, label, x + size / 2, y + size / 2, BLACK, fontSize, TextAlignment.CENTER);
    }

        public static void drawFlagBox(GraphicsContext gc, String label, double x, double y, double size, int fontSize, boolean highlight) {
        gc.setStroke(highlight? HighlightBorder : BLACK);
        gc.setFill(highlight? HighlightFill : GREY);
        gc.setLineWidth(1.5);
        gc.fillRect(x, y, size, size);
        gc.strokeRect(x, y, size, size);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER); 
        drawTextBold(gc, label, x + size / 2, y + size / 2, BLACK, fontSize, TextAlignment.CENTER);
    }

        public static void drawPCToAdd4(GraphicsContext gc, boolean highlight) {
        drawVerticalSegment(gc, C1_PC_IM + pcWidth*1.5, R_MAIN_PATH, add4Y + add4Height*0.25, highlight? Highlight :BLACK, false, false); 
        drawRightArrow(gc, C1_PC_IM + pcWidth*1.5, add4Y + add4Height*0.25, add4X, highlight? Highlight :BLACK, false); 
        drawHorizontalSegment(gc, C1_PC_IM + pcWidth, R_MAIN_PATH, C1_PC_IM + pcWidth*1.5, highlight ? Highlight :BLACK, false, true);     
        }
    public static void drawPCToInstructionMemory(GraphicsContext gc, boolean highlight) {
        drawText(gc, "Read \naddress", instrMemX + 0.05 * instrMemWidth, instrMemY + 0.1 * instrMemHeight, highlight ? HighlightText : BLACK, portFontSize, TextAlignment.LEFT);
        drawRightArrow(gc, C1_PC_IM + pcWidth, R_MAIN_PATH, instrMemX, highlight ? Highlight : BLACK, false); // PC to Instruction Memory
    }

    public static void drawPCToAddBranch(GraphicsContext gc, boolean highlight) {
        drawHorizontalSegment(gc, C1_PC_IM + pcWidth, R_MAIN_PATH, C1_PC_IM + pcWidth*1.5, highlight ? Highlight :BLACK, false, true);     
        drawVerticalSegment(gc, C1_PC_IM + pcWidth*1.5, R_MAIN_PATH, controlY, highlight? Highlight :BLACK, false, true); 
        drawHorizontalSegment(gc, C1_PC_IM + pcWidth*1.5, controlY, add4X + add4Width*2.5, highlight ? Highlight :BLACK, true, false);
        drawVerticalSegment(gc, add4X + add4Width*2.5, controlY, addBranchY + 0.25*addBranchHeight, highlight ? Highlight :BLACK, false, false);
        drawRightArrow(gc, add4X + add4Width*2.5, addBranchY + 0.25*addBranchHeight, addBranchX, highlight ? Highlight :BLACK, false); // PC to Shift Left 2
    }
    public static void drawAdd4ToMux(GraphicsContext gc, boolean highlight) {
        drawRightArrow(gc, add4X + add4Width, add4Y + 0.5*add4Height, muxPcSourceX, highlight ? Highlight :BLACK, false); // Add 4 to MUX
    }

    public static void drawMuxToPC(GraphicsContext gc, boolean highlight) {

        drawRightArrow(gc, C1_PC_IM - pcWidth*0.5, R_MAIN_PATH, C1_PC_IM, highlight ? Highlight :BLACK, false); // To PC
        drawVerticalSegment(gc, C1_PC_IM - pcWidth*0.5, R_MAIN_PATH, R_BRANCH_LOGIC*0.3,  highlight ? Highlight :BLACK, false, false); 
        drawHorizontalSegment(gc, C1_PC_IM - pcWidth*0.5, R_BRANCH_LOGIC*0.3, C6_MUX + 0.04*width, highlight ? Highlight :BLACK, false, false);
        drawVerticalSegment(gc, C6_MUX + 0.04*width, R_BRANCH_LOGIC*0.3, R_BRANCH_LOGIC*0.9, highlight ? Highlight :BLACK, false, false); // MUX to PC
        drawHorizontalSegment(gc, C6_MUX + 0.04*width, R_BRANCH_LOGIC*0.9, C6_MUX, highlight ? Highlight :BLACK, false, false); // MUX to PC
    }

    public static void drawAddBranchToMUX(GraphicsContext gc, boolean highlight) {
        drawRightArrow(gc, C3_REGISTERS + width*0.25 + pcWidth*1.5, R_BRANCH_LOGIC*1.1, C6_MUX - 0.02*width, highlight ? Highlight :BLACK, false); // Add Branch to MUX
    }

    public static void drawAdd4(GraphicsContext gc, boolean highlight) {
        drawRightArrow(gc, C1_PC_IM + pcWidth*3, R_BRANCH_LOGIC*0.85, add4X, highlight ? Highlight : BLACK, false); // Add 4
    }

    public static void drawInstrToControl(GraphicsContext gc, boolean highlight) {
        Color highlightColor = highlight ? HighlightText : BLACK;
        drawText(gc, "Instruction\n [31 - 0]", instrMemX + 0.95 * instrMemWidth,  instrMemY + 0.45 * instrMemHeight, highlightColor, portFontSize, TextAlignment.RIGHT);
        drawText(gc, "Instruction [31 - 21]", instrMemX + instrMemWidth + 0.9*pcWidth, controlY + 0.45 *controlHeight, highlightColor, portFontSize, TextAlignment.LEFT);

        // Vẽ đường từ Instruction Memory đến Control
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight :BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, controlY + 0.5 *controlHeight, highlight ? Highlight :BLACK, true, false); 
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, controlY + 0.5 *controlHeight, C2_CONTROL, highlight ? Highlight :BLACK, false); // Instruction Memory to Control
    }
    
    public static void drawShiftLeftToAddBranch(GraphicsContext gc, boolean highlight) {
        drawRightArrow(gc, C3_REGISTERS + width*0.15 + ellipseWidth, R_BRANCH_LOGIC*1.3, C3_REGISTERS + width*0.25, highlight ? Highlight : BLACK, false); // Shift Left 2 to Add Branch
    }
    public static void drawInstrToSignExtend(GraphicsContext gc, boolean highlight) {
        Color highlightColor = highlight ? HighlightText : BLACK;
        drawText(gc, "Instruction\n [31 - 0]", instrMemX + 0.95 * instrMemWidth,  instrMemY + 0.45 * instrMemHeight, highlightColor, portFontSize, TextAlignment.RIGHT);
        drawText(gc, "Instruction [31 - 0]", instrMemX + instrMemWidth + 0.9*pcWidth, signExtendY + signExtendHeight * 0.4, highlightColor, portFontSize, TextAlignment.LEFT);
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight :BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, signExtendY + 0.5*ellipseHeight, highlight ? Highlight :BLACK, true, false); 
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, signExtendY + 0.5*ellipseHeight, signExtendX, highlight ? Highlight : BLACK, false); // Shift Left 2 to Add Branch
    }
    public static void drawInstrToRegRead1(GraphicsContext gc, boolean highlight) {
        Color highlightColor = highlight ? HighlightText : BLACK;
        drawText(gc, "Instruction\n [31 - 0]", instrMemX + 0.95 * instrMemWidth,  instrMemY + 0.45 * instrMemHeight, highlightColor, portFontSize, TextAlignment.RIGHT);
        drawText(gc, "Instruction [9 - 5]", instrMemX + instrMemWidth + 0.9*pcWidth, regY + 0.05 * regHeight, highlightColor, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Read\nregister 1", regX + 0.05 * regWidth, regY + 0.1 * regHeight, highlightColor, portFontSize, TextAlignment.LEFT);
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight : BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, regY + 0.1 * regHeight, highlight ? Highlight : BLACK, true, false); 
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.1 * regHeight, C3_REGISTERS, highlight ? Highlight : BLACK, true); // Instruction Memory to Registers
    }

    public static void drawInstrToMuxReg0(GraphicsContext gc, boolean highlight)
    {
        Color highlightColor = highlight ? HighlightText : BLACK;
        drawText(gc, "Instruction\n [31 - 0]", instrMemX + 0.95 * instrMemWidth,  instrMemY + 0.45 * instrMemHeight, highlightColor, portFontSize, TextAlignment.RIGHT);
        drawText(gc, "Instruction [20 - 16]", instrMemX + instrMemWidth + 0.9*pcWidth, muxRegInputY + 0.05 * muxHeight, highlightColor, portFontSize, TextAlignment.LEFT);
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight : BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, muxRegInputY + 0.15 * muxHeight, highlight ? Highlight : BLACK, true, false); 
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, muxRegInputY + 0.15 * muxHeight, C2_CONTROL - 0.02*width, highlight ? Highlight : BLACK, true); // Instruction Memory to MUX Register Input
    }

    public static void drawInstrToMuxReg1(GraphicsContext gc, boolean highlight)
    {
        Color highlightColor = highlight ? HighlightText : BLACK;
        drawText(gc, "Instruction\n [31 - 0]", instrMemX + 0.95 * instrMemWidth,  instrMemY + 0.45 * instrMemHeight, highlightColor, portFontSize, TextAlignment.RIGHT);
        drawText(gc, "Instruction [4 - 0]", instrMemX + instrMemWidth + 0.9*pcWidth, regY + 0.75 * regHeight, highlightColor, portFontSize, TextAlignment.LEFT);
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight : BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, regY + 0.7 *regHeight, highlight ? Highlight : BLACK, true, false); 
        drawHorizontalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.7 *regHeight, C2_CONTROL - 0.05*width, highlight ? Highlight : BLACK, false,true); // Instruction Memory to Sign Extend
        drawVerticalSegment(gc, C2_CONTROL - 0.05*width, regY + 0.7 *regHeight, muxRegInputY + 0.8*muxHeight, highlight ? Highlight : BLACK, true, false); // MUX Register Input to Register Input
        drawRightArrow(gc, C2_CONTROL - 0.05*width, muxRegInputY + 0.8*muxHeight, muxRegInputX , highlight ? Highlight : BLACK, false); // Instruction Memory to MUX Register Input
    }

    public static void drawInstrToRegWrite(GraphicsContext gc, boolean highlight) {
        Color highlightColor = highlight ? HighlightText : BLACK;
        drawText(gc, "Instruction\n [31 - 0]", instrMemX + 0.95 * instrMemWidth,  instrMemY + 0.45 * instrMemHeight, highlightColor, portFontSize, TextAlignment.RIGHT);
        drawText(gc, "Instruction [4 - 0]", instrMemX + instrMemWidth + 0.9*pcWidth, regY + 0.75 * regHeight, highlightColor, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Write\nregister", regX + 0.05 * regWidth, regY + 0.7 * regHeight, highlightColor, portFontSize, TextAlignment.LEFT);
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight : BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, regY + 0.7 * regHeight, highlight ? Highlight : BLACK, true, false); 
        drawRightArrow(gc, instrMemX + instrMemWidth + 0.8*pcWidth, regY + 0.7 * regHeight, C3_REGISTERS, highlight ? Highlight : BLACK, true); // Instruction Memory to Registers
    }

    public static void drawMUXRegToRegRead2(GraphicsContext gc, boolean highlight){
        Color highlightColor = highlight ? HighlightText : BLACK;
        drawText(gc, "Read\nregister 2", regX + 0.05 * regWidth, regY + 0.35 * regHeight, highlightColor, portFontSize, TextAlignment.LEFT);
        drawRightArrow(gc, C2_CONTROL, R_MAIN_PATH + 0.075 * height, C3_REGISTERS, highlight? Highlight : BLACK, false); // MUX Register Input to Registers
    }

    public static void drawInstrToALUControl(GraphicsContext gc, boolean highlight)
    {
        Color highlightColor = highlight ? HighlightText : BLACK;
        drawText(gc, "Instruction\n [31 - 0]", instrMemX + 0.95 * instrMemWidth,  instrMemY + 0.45 * instrMemHeight, highlightColor, portFontSize, TextAlignment.RIGHT);
        drawText(gc, "Instruction [31 - 0]", instrMemX + instrMemWidth + 0.9*pcWidth, signExtendY + signExtendHeight * 0.4, highlightColor, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Instruction [31 - 21]", signExtendX - 0.65*signExtendWidth, R_SIGN_EXTEND + 1.35*ellipseHeight, highlightColor, portFontSize, TextAlignment.LEFT);
        
        drawHorizontalSegment(gc, instrMemX + instrMemWidth, R_MAIN_PATH + 0.07 * height,instrMemX + instrMemWidth + 0.8*pcWidth, highlight ? Highlight :BLACK, false,true); 
        drawVerticalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, R_MAIN_PATH + 0.07 * height, signExtendY + 0.5*ellipseHeight, highlight ? Highlight :BLACK, true, false); 
        drawHorizontalSegment(gc, instrMemX + instrMemWidth + 0.8*pcWidth, signExtendY + 0.5*ellipseHeight, signExtendX - 0.7*signExtendWidth, highlight ? Highlight : BLACK, false, true); // Shift Left 2 to Add Branch
        drawVerticalSegment(gc, signExtendX - 0.7*signExtendWidth, R_SIGN_EXTEND + 0.5*ellipseHeight, R_SIGN_EXTEND + 1.5*ellipseHeight, highlight ? Highlight : BLACK, true, false);
        drawHorizontalSegment(gc, signExtendX - 0.7*signExtendWidth, R_SIGN_EXTEND + 1.5*ellipseHeight, C4_ALU - width*0.05, highlight ? Highlight : BLACK, false, false); // Instruction Memory to ALU Control
        drawVerticalSegment(gc, C4_ALU - width*0.05, R_SIGN_EXTEND + 1.5*ellipseHeight, R_SIGN_EXTEND + 0.8*ellipseHeight, highlight ? Highlight : BLACK, false, false); // ALU Control to Sign Extend
        drawRightArrow(gc, C4_ALU - width*0.05, R_SIGN_EXTEND + 0.8*ellipseHeight, aluControlX, highlight ? Highlight : BLACK, false); // Instruction Memory to ALU Control
    }

    public static void drawSignExtendToShiftLeft(GraphicsContext gc, boolean highlight)
    {
        drawHorizontalSegment(gc, signExtendX + ellipseWidth, signExtendY + 0.5*ellipseHeight, muxAluInputX - muxWidth, highlight ? Highlight : BLACK, false, false);
        drawVerticalSegment(gc,  muxAluInputX - muxWidth, signExtendY + 0.5*ellipseHeight, shiftLeft2Y + 0.5*ellipseHeight, highlight ? Highlight : BLACK, false, false);
        drawRightArrow(gc,  muxAluInputX - muxWidth, shiftLeft2Y + 0.5*ellipseHeight, shiftLeft2X, highlight ? Highlight : BLACK, false); // Sign Extend to Shift Left 2
    }

    public static void drawSignExtendToMuxALU(GraphicsContext gc, boolean highlight)
    {
        drawHorizontalSegment(gc, signExtendX + ellipseWidth, signExtendY + 0.5*ellipseHeight, muxAluInputX - muxWidth, highlight ? Highlight : BLACK, false, false);
        drawVerticalSegment(gc,  muxAluInputX - muxWidth, signExtendY + 0.5*ellipseHeight, muxAluInputY + 0.8*muxHeight, highlight ? Highlight : BLACK, false, false);
        drawRightArrow(gc,  muxAluInputX - muxWidth, muxAluInputY + 0.8*muxHeight, muxAluInputX, highlight ? Highlight : BLACK, false); // Sign Extend to Shift Left 2
    }

        // Registers -> MUX ALU Input
    public static void drawRegistersToMuxALU(GraphicsContext gc, boolean highlight) {
        Color colorText = highlight ? HighlightText : BLACK;
        drawText(gc, "Read\ndata 2", regX + regWidth *0.9  + 5, muxAluInputY + 0.3 * muxHeight, colorText, portFontSize, TextAlignment.RIGHT);

        drawRightArrow(gc, regX + rectWidth, muxAluInputY + 0.3 * muxHeight, muxAluInputX, highlight ? Highlight : BLACK, false);
    }

    // Registers -> Data Memory
    public static void drawRegistersToDataMemory(GraphicsContext gc, boolean highlight) {
        Color colorText = highlight ? HighlightText : BLACK;
        drawText(gc, "Write\ndata", regX + 0.05 * rectWidth, regY + 0.9 * regHeight, colorText, portFontSize, TextAlignment.LEFT);
        drawText(gc, "Read\ndata 2", regX + regWidth *0.9  + 5, muxAluInputY + 0.3 * muxHeight, colorText, portFontSize, TextAlignment.RIGHT);
        drawHorizontalSegment(gc, regX + rectWidth, muxAluInputY + 0.3 * muxHeight, regX + rectWidth * 1.1, highlight ? Highlight : BLACK, false, false);
        drawVerticalSegment(gc, regX + rectWidth * 1.1, muxAluInputY + 0.3 * muxHeight, dataMemY + 0.8 * dataMemHeight, highlight ? Highlight : BLACK, true, false);
        drawRightArrow(gc, regX + rectWidth * 1.1, dataMemY + 0.8 * dataMemHeight, dataMemX, highlight ? Highlight : BLACK, false);
    }

    // Registers -> ALU
    public static void drawRegistersToALU(GraphicsContext gc, boolean highlight) {
        Color colorTex = highlight ? HighlightText : BLACK;
        drawText(gc, "Read\ndata 1", regX + regWidth*0.95, aluY + 0.2 * aluBlockHeight, colorTex, portFontSize, TextAlignment.RIGHT);
        drawRightArrow(gc, regX + rectWidth, R_MAIN_PATH + 0.025 * height, aluX, highlight ? Highlight : BLACK, false);
    }

    // MUX ALU Input -> ALU
    public static void drawMuxALUToALU(GraphicsContext gc, boolean highlight) {
        drawRightArrow(gc, muxAluInputX + muxWidth, muxAluInputY + 0.5 * muxHeight, aluX, highlight ? Highlight : BLACK, false);
    }

    // ALU -> Data Memory
    public static void drawALUToDataMemory(GraphicsContext gc, boolean highlight) {
        Color colorText = highlight ? HighlightText : BLACK;
        drawText(gc, "Address", dataMemX + 0.05 * dataMemWidth, dataMemY + dataMemHeight * 0.18, colorText, portFontSize, TextAlignment.LEFT);
        drawRightArrow(gc, C4_ALU + aluWidth, aluY + 0.6 * aluHeight, dataMemX, highlight ? Highlight : BLACK, false);
    }

    // ALU -> Flags
    public static void drawALUToFlags(GraphicsContext gc, boolean highlight) {
        drawUpArrow(gc, aluX + 0.48 * aluWidth, aluY + 0.12 * aluHeight, flagY + flagBoxSize, highlight ? Highlight : BLACK, false);
    }

    // ALU -> MUX MemToReg
    public static void drawALUToMuxMemToReg(GraphicsContext gc, boolean highlight) {
        drawHorizontalSegment(gc, aluX + aluWidth, aluY + 0.6 * aluHeight, dataMemX - 0.25 * dataMemWidth, highlight ? Highlight : BLACK, false, false);
        drawVerticalSegment(gc, dataMemX - 0.25 * dataMemWidth, aluY + 0.6 * aluHeight, dataMemY + dataMemHeight * 1.2, highlight ? Highlight : BLACK, true, false);
        drawHorizontalSegment(gc, dataMemX - 0.25 * dataMemWidth, dataMemY + dataMemHeight * 1.2, muxMemToRegX - 0.8 * muxWidth, highlight ? Highlight : BLACK, false, false);
        drawVerticalSegment(gc, muxMemToRegX - 0.8 * muxWidth, dataMemY + dataMemHeight * 1.2, muxMemToRegY + 0.8 * muxHeight, highlight ? Highlight : BLACK, false, false);
        drawRightArrow(gc, muxMemToRegX - 0.8 * muxWidth, muxMemToRegY + 0.8 * muxHeight, muxMemToRegX, highlight ? Highlight : BLACK, false);
    }

    // Data Memory -> MUX MemToReg
    public static void drawDataMemoryToMuxMemToReg(GraphicsContext gc, boolean highlight) {
        Color colorText = highlight ? HighlightText : BLACK;
        drawText(gc, "Read\ndata", dataMemX + dataMemWidth * 0.95, dataMemY + dataMemHeight * 0.35, colorText, portFontSize, TextAlignment.RIGHT);
        drawRightArrow(gc, dataMemX + dataMemWidth, muxMemToRegY + 0.2 * muxHeight, muxMemToRegX, highlight ? Highlight : BLACK, false);
    }

    // MUX MemToReg -> Registers
    public static void drawMuxMemToRegToRegisters(GraphicsContext gc, boolean highlight) {
        Color colorText = highlight ? HighlightText : BLACK;
        drawText(gc, "Write\ndata", regX + 0.05 * rectWidth, regY + 0.9 * regHeight, colorText, portFontSize, TextAlignment.LEFT);
        drawHorizontalSegment(gc, muxMemToRegX + muxWidth, muxMemToRegY + 0.5 * muxHeight, muxMemToRegX + muxWidth * 1.8, highlight ? Highlight : BLACK, false, false);
        drawVerticalSegment(gc, muxMemToRegX + muxWidth * 1.8, muxMemToRegY + 0.5 * muxHeight, aluControlY + aluControlHeight * 2, highlight ? Highlight : BLACK, false, false);
        drawHorizontalSegment(gc, muxMemToRegX + muxWidth * 1.8, aluControlY + aluControlHeight * 2, regX - 0.3 * rectWidth, highlight ? Highlight : BLACK, false, false);
        drawVerticalSegment(gc, regX - rectWidth * 0.3, aluControlY + aluControlHeight * 2, regY + 0.9 * regHeight, highlight ? Highlight : BLACK, false, false);
        drawRightArrow(gc, regX - rectWidth * 0.3, regY + 0.9 * regHeight, regX, highlight ? Highlight : BLACK, false);
    }

    public static void drawReg2Loc(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, controlX + 0.7*controlWidth, controlY + 0.05*controlHeight, controlX + controlWidth, color, false, false);
        drawVerticalSegment(gc, controlX + controlWidth, addBranchY + addBranchHeight*0.65, controlY + 0.05*controlHeight, color, false, false);
        drawHorizontalSegment(gc, controlX + controlWidth, addBranchY + addBranchHeight*0.65, instrMemX + instrMemWidth*1.15, color, false, false);
        drawVerticalSegment(gc, instrMemX + instrMemWidth*1.15, addBranchY + addBranchHeight*0.65, muxRegInputY + muxHeight*1.75, color, false, false);
        drawHorizontalSegment(gc, instrMemX + instrMemWidth*1.15,  muxRegInputY + muxHeight*1.75, muxRegInputX + 0.5*muxWidth, color, false, false);
        drawVerticalSegment(gc, muxRegInputX + 0.5*muxWidth, muxRegInputY + muxHeight*1.75, muxRegInputY + muxHeight, color, false, false);
    }

    public static void drawUncondBranch(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, controlX + 0.85*controlWidth, controlY + 0.14*controlHeight, orGateX + 0.1*gateW, color, false, false);
    }

    public static void drawFlagBranch(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, controlX + 0.9*controlWidth, controlY + 0.23*controlHeight, andGate1X - 0.5*gateW, color, false, false);
        drawVerticalSegment(gc, andGate1X - 0.5*gateW, andGateY + 0.2*gateH, controlY + 0.23*controlHeight, color, false, false);
        drawHorizontalSegment(gc, andGate1X - 0.5*gateW, andGateY + 0.2*gateH, andGate1X, color, false, false);
    }

    public static void drawZeroBranch(GraphicsContext gc,  boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, controlX + 0.95*controlWidth, controlY + 0.32*controlHeight, andGate2X - 0.7*gateW, color, false, false);
        drawVerticalSegment(gc, andGate2X - 0.7*gateW, andGateY + 0.2*gateH, controlY + 0.32*controlHeight, color, false, false);
        drawHorizontalSegment(gc, andGate2X - 0.7*gateW, andGateY + 0.2*gateH, andGate2X, color, false, false);
    }

    public static void drawMemRead(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, controlX + 0.97*controlWidth, controlY + 0.41*controlHeight, muxMemToRegX + 2.5*muxWidth, color, false, false);
        drawVerticalSegment(gc, muxMemToRegX + 2.5*muxWidth, controlY + 0.41*controlHeight, dataMemY + 1.4*dataMemHeight, color, false, false);
        drawHorizontalSegment(gc, muxMemToRegX + 2.5*muxWidth, dataMemY + 1.4*dataMemHeight, dataMemX + dataMemWidth*0.5, color, false, false);
        drawVerticalSegment(gc, dataMemX + dataMemWidth*0.5, dataMemY + 1.4*dataMemHeight, dataMemY + dataMemHeight, color, false, false);
    }

    public static void drawMemToReg(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, controlX + 0.99*controlWidth, controlY + 0.5*controlHeight, muxMemToRegX + 0.5*muxWidth, color, false, false);
        drawVerticalSegment(gc, muxMemToRegX + 0.5*muxWidth, controlY + 0.5*controlHeight, muxMemToRegY, color, false, false);
    }

    public static void drawMemWrite(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, controlX + 0.97*controlWidth, controlY + 0.59*controlHeight, dataMemX + dataMemWidth*0.5, color, false, false);
        drawVerticalSegment(gc, dataMemX + dataMemWidth*0.5, controlY + 0.59*controlHeight, dataMemY, color, false, false);
    }

    public static void drawFlagWrite(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, controlX + 0.98*controlWidth, controlY + 0.68*controlHeight, aluX + 0.48*aluWidth, color, false, false);
        drawVerticalSegment(gc, aluX + 0.48*aluWidth, controlY + 0.68*controlHeight, flagY, color, false, false);
    }

    public static void drawALUSrc(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, controlX + 0.93*controlWidth, controlY + 0.77*controlHeight, muxAluInputX + 0.5*muxWidth, color, false, false);
        drawVerticalSegment(gc, muxAluInputX + 0.5*muxWidth, controlY + 0.77*controlHeight, muxAluInputY, color, false, false);
    }

    public static void drawALUOp(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, controlX + 0.83*controlWidth, controlY + 0.86*controlHeight, muxAluInputX - 1.5*muxWidth, color, false, false);
        drawVerticalSegment(gc, muxAluInputX - 1.5*muxWidth, controlY + 0.86*controlHeight, aluControlY + 1.7 * aluControlHeight, color, false, false);
        drawHorizontalSegment(gc, muxAluInputX - 1.5*muxWidth, aluControlY + 1.7 * aluControlHeight, aluControlX + aluControlHeight*0.5, color, false, false);
        drawVerticalSegment(gc, aluControlX + aluControlHeight*0.5, aluControlY + 1.7 * aluControlHeight, aluControlY + aluControlHeight, color, false, false);
    }

    public static void drawRegWrite(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, controlX + 0.7*controlWidth, controlY + 0.95*controlHeight, regX + 0.5 *rectWidth, color, false, false);
        drawVerticalSegment(gc, regX + 0.5 *rectWidth, controlY + 0.95*controlHeight, regY, color, false, false);
    }

    public static void drawFlagToAndGate(GraphicsContext gc,boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, flagX + 4*flagBoxSize, flagY + 0.5*flagBoxSize, andGate1X , color, false, false);
    }

    public static void drawALUControlToALU(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawVerticalSegment(gc, aluX + 0.48*aluWidth, aluY + 0.88*aluHeight, aluControlY + aluControlHeight*0.5, color, false, false);
        drawHorizontalSegment(gc, aluX + 0.48*aluWidth, aluControlY + aluControlHeight*0.5, aluControlX + aluControlWidth, color, false, false);
    }

    public static void drawALUToAndGate(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawText(gc, "Zero", aluX + aluBlockWidth*0.95, aluY + aluBlockHeight*0.4, color, portFontSize, TextAlignment.RIGHT);
        drawHorizontalSegment(gc, aluX + aluWidth, aluY + 0.4 * aluBlockHeight, andGate2X - 0.7*gateW, color, false, false);
        drawVerticalSegment(gc, andGate2X - 0.7*gateW, andGateY + 0.8*gateH, aluY + 0.4 * aluBlockHeight, color, false, false);
        drawHorizontalSegment(gc, andGate2X - 0.7*gateW, andGateY + 0.8*gateH, andGate2X, color, false, false);
    }

    public static void drawAnd1ToOrGate(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, andGate1X + 0.9*gateW, andGateY + 0.5*gateH, andGate2X - 1.2*gateW, color, false, false);
        drawVerticalSegment(gc, andGate2X - 1.2*gateW, orGateY + 0.65*gateH, andGateY + 0.5*gateH, color, false, false);
        drawHorizontalSegment(gc, andGate2X - 1.2*gateW, orGateY + 0.65*gateH, orGateX + 0.2*gateW, color, false, false);
    }

    public static void drawAnd2ToOrGate(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, andGate2X + 0.95*gateW, andGateY + 0.5*gateH, orGateX - 0.8*gateW, color, false, false);
        drawVerticalSegment(gc, orGateX - 0.8*gateW, orGateY + 0.95*gateH, andGateY + 0.5*gateH, color, false, false);
        drawHorizontalSegment(gc, orGateX - 0.8*gateW, orGateY + 0.95*gateH, orGateX + 0.1*gateW, color, false, false);
    }

    public static void drawOrGateToMux(GraphicsContext gc, boolean highlight) {
        Color color = highlight ? HighlightControl : ARM_BLUE;
        drawHorizontalSegment(gc, orGateX + 1.2*gateW, orGateY + 0.6*gateH, muxPcSourceX + 0.5*muxWidth, color, false, false);
        drawVerticalSegment(gc, muxPcSourceX + 0.5*muxWidth, orGateY + 0.6*gateH, muxPcSourceY + muxHeight, color, false, false);
    }
    public static void drawControlText(GraphicsContext gc, boolean highlight) {
    Color color = highlight ? HighlightControl : ARM_BLUE;
            String[] controlSignals = {"Reg2Loc", "UncondBranch", "FlagBranch", "ZeroBranch", "MemRead", "MemToReg", "MemWrite", "FlagWrite", "ALUSrc", "ALUOp", "RegWrite"};
        for (int i = 0; i < controlSignals.length; i++) {
            double yPos = controlY + controlHeight * (0.02 + i * 0.09) ; // Rải đều các nhãn
            drawText(gc, controlSignals[i], controlX + controlWidth*1.1, yPos, color, portFontSize-1, TextAlignment.LEFT);
        }
    }

    public static void drawPC(GraphicsContext gc, boolean highlight)
    {
        drawCompRect(gc, pcRectX, pcRectY, pcRectWidth, pcRectHeight, pcBorderColor, pcFillColor,highlight); // Program Counter
        if(!highlight) drawTextBold(gc, "PC", pcRectX + pcRectWidth / 2, pcRectY + pcRectHeight / 2, BLACK, baseFontSize + 2, TextAlignment.CENTER);
    }

    public static void drawInstructionMemory(GraphicsContext gc, boolean highlight) {
        drawCompRect(gc, instrMemX, instrMemY, instrMemWidth, instrMemHeight, instrMemBorderColor, instrMemFillColor, highlight);
        if(!highlight) drawTextBold(gc, "Instruction\nmemory", instrMemX + instrMemWidth / 2, instrMemY + instrMemHeight * 0.85, BLACK, baseFontSize + 1, TextAlignment.CENTER);
    }

    public static void drawRegisters(GraphicsContext gc, boolean highlight) {
        drawCompRect(gc, regX, regY, regWidth, regHeight, regBorderColor, regFillColor, highlight);
        if(!highlight) drawTextBold(gc, "Registers", regX + regWidth * 0.45, regY + regHeight * 0.9, BLACK, baseFontSize + 1, TextAlignment.LEFT);
    }

    public static void drawALUBlock(GraphicsContext gc, boolean highlight) {
        // Giả sử ALU chính không phải là loại 'isSmall'
        drawALU(gc, aluX, aluY, aluBlockWidth, aluBlockHeight, aluBorderColor, aluFillColor, highlight);
        if(!highlight) drawTextBold(gc, "ALU", aluX + aluBlockWidth / 2, aluY + aluBlockHeight / 2, BLACK, baseFontSize + 4, TextAlignment.CENTER);
    }

    public static void drawDataMemory(GraphicsContext gc, boolean highlight) {
        drawCompRect(gc, dataMemX, dataMemY, dataMemWidth, dataMemHeight, dataMemBorderColor, dataMemFillColor, highlight);
        if(!highlight) drawTextBold(gc, "Data\nmemory", dataMemX + dataMemWidth * 0.7, dataMemY + dataMemHeight * 0.7, BLACK, baseFontSize + 1, TextAlignment.CENTER);
    }

    public static void drawControlUnit(GraphicsContext gc, boolean highlight) {
        // Control Unit là hình ellipse
        drawCompEllipse(gc, controlX, controlY, controlWidth, controlHeight, controlBorderColor, controlFillColor, highlight);
        if(!highlight) drawTextBold(gc, "Control", controlX + controlWidth / 2, controlY + controlHeight / 2, BLACK, baseFontSize + 4, TextAlignment.CENTER);
    }

    public static void drawAdd4Block(GraphicsContext gc, boolean highlight) {
        // Khối Add được vẽ bằng hình dạng ALU, giả sử là isSmall=true
        drawALU(gc, add4X, add4Y, add4Width, add4Height, add4BorderColor, add4FillColor, highlight);
        if(!highlight) drawTextBold(gc, "Add", add4X + add4Width / 2, add4Y + add4Height / 2, BLACK, baseFontSize, TextAlignment.CENTER);
        drawText(gc, "4", C1_PC_IM + pcWidth*2.8, add4Y + add4Height*0.85, BLACK, baseFontSize, TextAlignment.RIGHT);

    }

    public static void drawShiftLeft2(GraphicsContext gc, boolean highlight) {
        // Shift left 2 là hình tròn (isCircular = true)
        drawCompEllipse(gc, shiftLeft2X, shiftLeft2Y, shiftLeft2Width, shiftLeft2Height, shiftLeft2BorderColor, shiftLeft2FillColor, highlight);
        if(!highlight) drawTextBold(gc, "Shift\nleft 2", shiftLeft2X + shiftLeft2Width / 2, shiftLeft2Y + shiftLeft2Height / 2, BLACK, baseFontSize, TextAlignment.CENTER);
    }

    public static void drawBranchAdder(GraphicsContext gc, boolean highlight) {
        // Khối Add được vẽ bằng hình dạng ALU, giả sử là isSmall=true
        drawALU(gc, addBranchX, addBranchY, addBranchWidth, addBranchHeight, addBranchBorderColor, addBranchFillColor, highlight);
        if(!highlight) drawTextBold(gc, "Add", addBranchX + addBranchWidth / 2, addBranchY + addBranchHeight / 2, BLACK, baseFontSize, TextAlignment.CENTER);
    }

    public static void drawSignExtend(GraphicsContext gc, boolean highlight) {
        // Sign-extend là hình ellipse
        drawCompEllipse(gc, signExtendX, signExtendY, signExtendWidth, signExtendHeight, signExtendBorderColor, signExtendFillColor, highlight);
        if(!highlight) drawTextBold(gc, "Sign-\nextend", signExtendX + signExtendWidth / 2, signExtendY + signExtendHeight / 2, BLACK, baseFontSize, TextAlignment.CENTER);
    }

    public static void drawAluControl(GraphicsContext gc, boolean highlight) {
        // ALU Control là hình ellipse
        drawCompEllipse(gc, aluControlX, aluControlY, aluControlWidth, aluControlHeight, aluControlBorderColor, aluControlFillColor, highlight);
        if(!highlight) drawTextBold(gc, "ALU\ncontrol", aluControlX + aluControlWidth / 2, aluControlY + aluControlHeight / 2, BLACK, baseFontSize, TextAlignment.CENTER);
    }
}