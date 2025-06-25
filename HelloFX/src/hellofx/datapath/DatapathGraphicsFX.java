package hellofx.datapath;

import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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

    /**
     * Vẽ một hình ellipse thành phần với highlight tùy chọn.
     */
    public static void drawCompEllipse(GraphicsContext gc, double x, double y,
                                       double width, double height, Color lineColor, Color fillColor, boolean highlight) {
        drawEllipse(gc, x, y, width, height, highlight ? RED : lineColor, fillColor);
    }
    
    /**
     * Vẽ một hình ellipse. Logic sử dụng đường cong bezier được giữ nguyên.
     */
    public static void drawEllipse(GraphicsContext gc, double x, double y, double width, double height,
                                   Color stroke, Color fill) {
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
        gc.fill();
        gc.stroke();
    }

    /**
     * Vẽ một ALU (Arithmetic Logic Unit).
     */
    public static void drawALU(GraphicsContext gc, double x, double y, double width, double height, Color colorLine, Color colorFill, boolean highlight) {
        gc.setStroke(highlight ? RED : colorLine);
        gc.setFill(colorFill);
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
        gc.fill();
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

        gc.setFill(highlightTop ? RED : GREY);
        gc.beginPath();
        gc.moveTo(x, y + height / 2);
        gc.lineTo(x, topArcCenterY);
        gc.arc(x + radius, topArcCenterY, radius, radius, 180, -180);
        gc.lineTo(x + width, y + height / 2);
        gc.closePath();
        gc.fill();

        gc.setFill(highlightBottom ? RED : GREY);
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
        gc.setFill(fillColor);
        gc.beginPath();
        gc.moveTo(x, y + height / 2);
        gc.lineTo(x, topArcCenterY);
        gc.arc(x + radius, topArcCenterY, radius, radius, 180, -180);
        gc.lineTo(x + width, y + height / 2);
        gc.closePath();
        gc.fill();

        // Bottom half
        gc.setFill(fillColor);
        gc.beginPath();
        gc.moveTo(x + width, y + height / 2);
        gc.lineTo(x + width, bottomArcCenterY);
        gc.arc(x + radius, bottomArcCenterY, radius, radius, 0, -180);
        gc.lineTo(x, y + height / 2);
        gc.closePath();
        gc.fill();

        // Border - highlight all if needed
        gc.setLineWidth(2);
        gc.setStroke(highlight ? RED : borderColor);
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
}