package main.java.com.mydomain.legv8simulator.UI;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;

/**
 * Component để tạo animation text di chuyển trên Pane
 * Có thể được tích hợp vào bất kỳ Pane nào
 */
public class TextAnimationComponent {
    
    // Animation properties - có thể bind với UI controls
    private DoubleProperty speedProperty = new SimpleDoubleProperty(50.0);
    private BooleanProperty isPlayingProperty = new SimpleBooleanProperty(false);
    private DoubleProperty progressProperty = new SimpleDoubleProperty(0.0);
    private IntegerProperty currentIndexProperty = new SimpleIntegerProperty(0);
    private IntegerProperty stepSizeProperty = new SimpleIntegerProperty(1);
    private StringProperty statusProperty = new SimpleStringProperty("Đã dừng");
    
    // Animation data
    private List<Point> points = new ArrayList<>();
    private double totalDistance = 0.0;
    private String displayText = "HELLO WORLD!";
    
    // UI Components
    private Pane targetPane;
    private Label textBlock;
    private AnimationTimer animationTimer;
    private boolean showPath = true;
    private boolean showPoints = true;
    
    // Style properties
    private ObjectProperty<Color> backgroundColorProperty = new SimpleObjectProperty<>(Color.web("#3498db"));
    private ObjectProperty<Color> textColorProperty = new SimpleObjectProperty<>(Color.WHITE);
    private DoubleProperty fontSizeProperty = new SimpleDoubleProperty(18);
    private DoubleProperty borderRadiusProperty = new SimpleDoubleProperty(8);
    
    // Lớp Point
    public static class Point {
        public final double x, y;
        
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
        
        public double distance(Point other) {
            double dx = this.x - other.x;
            double dy = this.y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
        
        @Override
        public String toString() {
            return String.format("(%.0f, %.0f)", x, y);
        }
    }
    
    /**
     * Constructor
     * @param targetPane Pane để hiển thị animation
     */
    public TextAnimationComponent(Pane targetPane) {
        this.targetPane = targetPane;
        initializeTextBlock();
        setupPropertyBindings();
    }
    
    private void initializeTextBlock() {
        textBlock = new Label(displayText);
        updateTextBlockStyle();
        textBlock.setAlignment(Pos.CENTER);
        textBlock.setMinWidth(120);
        textBlock.setMinHeight(40);
        
        // Thêm vào target pane
        targetPane.getChildren().add(textBlock);
    }
    
    private void setupPropertyBindings() {
        // Lắng nghe thay đổi style properties
        backgroundColorProperty.addListener((obs, oldVal, newVal) -> updateTextBlockStyle());
        textColorProperty.addListener((obs, oldVal, newVal) -> updateTextBlockStyle());
        fontSizeProperty.addListener((obs, oldVal, newVal) -> updateTextBlockStyle());
        borderRadiusProperty.addListener((obs, oldVal, newVal) -> updateTextBlockStyle());
    }
    
    /**
     * Cập nhật style cho text block
     */
    private void updateTextBlockStyle() {
        if (textBlock == null) return;
        
        Color bgColor = backgroundColorProperty.get();
        Color textColor = textColorProperty.get();
        double fontSize = fontSizeProperty.get();
        double borderRadius = borderRadiusProperty.get();
        
        String style = String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: %s; " +
            "-fx-font-size: %.0fpx; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 12 20 12 20; " +
            "-fx-background-radius: %.0fpx; " +
            "-fx-border-color: white; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: %.0fpx; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 4);",
            toHexString(bgColor), toHexString(textColor), fontSize, borderRadius, borderRadius
        );
        
        textBlock.setStyle(style);
    }
    
    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X", 
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }
    
    /**
     * Thiết lập dữ liệu animation
     */
    public void setAnimationData(String text, List<Point> points) {
        this.displayText = text;
        this.points = new ArrayList<>(points);
        
        if (textBlock != null) {
            textBlock.setText(text);
        }
        
        calculateTotalDistance();
        
        if (showPath || showPoints) {
            drawPath();
        }
        
        reset();
    }
    
    /**
     * Thiết lập các điểm từ string
     */
    public void setPoints(String coordinatesText) {
        List<Point> newPoints = parseCoordinates(coordinatesText);
        if (newPoints.size() >= 2) {
            this.points = newPoints;
            calculateTotalDistance();
            if (showPath || showPoints) {
                drawPath();
            }
            reset();
        }
    }
    
    private List<Point> parseCoordinates(String coordinatesText) {
        List<Point> result = new ArrayList<>();
        String[] lines = coordinatesText.trim().split("\n");
        
        for (String line : lines) {
            String[] coords = line.trim().split(",");
            if (coords.length == 2) {
                try {
                    double x = Double.parseDouble(coords[0].trim());
                    double y = Double.parseDouble(coords[1].trim());
                    result.add(new Point(x, y));
                } catch (NumberFormatException e) {
                    // Bỏ qua dòng lỗi
                }
            }
        }
        
        return result;
    }
    
    private void calculateTotalDistance() {
        totalDistance = 0.0;
        for (int i = 1; i < points.size(); i++) {
            totalDistance += points.get(i-1).distance(points.get(i));
        }
    }
    
    /**
     * Vẽ đường đi và các điểm
     */
    private void drawPath() {
        // Xóa đường đi cũ
        targetPane.getChildren().removeIf(node -> 
            (node instanceof Circle || node instanceof Line) && node != textBlock);
        
        if (showPoints) {
            // Vẽ các điểm
            for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);
                Circle circle = new Circle(point.x, point.y, 4);
                circle.setFill(Color.web("#3498db"));
                circle.setStroke(Color.WHITE);
                circle.setStrokeWidth(1);
                
                // Tooltip
                Tooltip tooltip = new Tooltip(String.format("Điểm %d: %s", i + 1, point.toString()));
                Tooltip.install(circle, tooltip);
                
                targetPane.getChildren().add(circle);
            }
        }
        
        if (showPath) {
            // Vẽ các đường nối
            for (int i = 0; i < points.size() - 1; i++) {
                Point start = points.get(i);
                Point end = points.get(i + 1);
                
                Line line = new Line(start.x, start.y, end.x, end.y);
                line.setStroke(Color.web("#bdc3c7"));
                line.setStrokeWidth(2);
                line.getStyleClass().add("animation-path");
                
                targetPane.getChildren().add(line);
            }
        }
        
        // Đảm bảo text block ở trên cùng
        textBlock.toFront();
    }
    
    /**
     * Bắt đầu animation
     */
    public void play() {
        if (points.size() < 2) return;
        
        isPlayingProperty.set(true);
        statusProperty.set("Đang chạy");
        
        if (animationTimer != null) {
            animationTimer.stop();
        }
        
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPlayingProperty.get()) {
                    stop();
                    return;
                }
                
                double speed = speedProperty.get();
                double speedFactor = speed / 50.0;
                double newProgress = progressProperty.get() + speedFactor * 0.5;
                
                if (newProgress >= 100) {
                    newProgress = 100;
                    isPlayingProperty.set(false);
                    statusProperty.set("Hoàn thành");
                    stop();
                }
                
                progressProperty.set(newProgress);
                updatePositionByProgress();
            }
        };
        
        animationTimer.start();
    }
    
    /**
     * Tạm dừng animation
     */
    public void pause() {
        isPlayingProperty.set(false);
        statusProperty.set("Tạm dừng");
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
    
    /**
     * Reset animation
     */
    public void reset() {
        pause();
        currentIndexProperty.set(0);
        progressProperty.set(0.0);
        statusProperty.set("Đã dừng");
        updatePosition();
    }
    
    /**
     * Di chuyển theo bước
     */
    public void step() {
        if (points.size() < 2) return;
        
        pause();
        
        int currentIndex = currentIndexProperty.get();
        int stepSize = stepSizeProperty.get();
        int newIndex = Math.min(currentIndex + stepSize, points.size() - 1);
        
        if (newIndex > currentIndex) {
            animateStepMovement(currentIndex, newIndex);
        }
        
        statusProperty.set("Bước tiếp (" + stepSize + " điểm)");
    }
    
    private void animateStepMovement(int fromIndex, int toIndex) {
        Timeline stepTimeline = new Timeline();
        
        double totalDuration = 0.8;
        double stepDuration = totalDuration / (toIndex - fromIndex);
        
        for (int i = fromIndex; i <= toIndex; i++) {
            final int index = i;
            double time = (i - fromIndex) * stepDuration;
            
            KeyFrame keyFrame = new KeyFrame(Duration.seconds(time), e -> {
                currentIndexProperty.set(index);
                updatePosition();
                highlightCurrentPoint();
            });
            
            stepTimeline.getKeyFrames().add(keyFrame);
        }
        
        stepTimeline.play();
    }
    
    private void highlightCurrentPoint() {
        // Xóa highlight cũ
        targetPane.getChildren().removeIf(node -> 
            node instanceof Circle && ((Circle)node).getRadius() > 6);
        
        int currentIndex = currentIndexProperty.get();
        if (currentIndex < points.size()) {
            Point currentPoint = points.get(currentIndex);
            Circle highlight = new Circle(currentPoint.x, currentPoint.y, 8);
            highlight.setFill(Color.TRANSPARENT);
            highlight.setStroke(Color.web("#e74c3c"));
            highlight.setStrokeWidth(3);
            
            Timeline highlightAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, 
                    new javafx.animation.KeyValue(highlight.radiusProperty(), 8),
                    new javafx.animation.KeyValue(highlight.opacityProperty(), 1.0)),
                new KeyFrame(Duration.seconds(0.5), 
                    new javafx.animation.KeyValue(highlight.radiusProperty(), 12),
                    new javafx.animation.KeyValue(highlight.opacityProperty(), 0.0))
            );
            
            targetPane.getChildren().add(highlight);
            highlightAnimation.setOnFinished(e -> targetPane.getChildren().remove(highlight));
            highlightAnimation.play();
        }
    }
    
    private void updatePositionByProgress() {
        if (totalDistance <= 0) return;
        
        double progress = progressProperty.get();
        double targetDistance = (progress / 100.0) * totalDistance;
        double currentDist = 0.0;
        int segmentIndex = 0;
        
        for (int i = 1; i < points.size(); i++) {
            double segmentLength = points.get(i-1).distance(points.get(i));
            
            if (currentDist + segmentLength >= targetDistance) {
                segmentIndex = i - 1;
                break;
            }
            currentDist += segmentLength;
            segmentIndex = i - 1;
        }
        
        if (segmentIndex >= points.size() - 1) {
            segmentIndex = points.size() - 2;
        }
        
        if (segmentIndex < points.size() - 1) {
            Point start = points.get(segmentIndex);
            Point end = points.get(segmentIndex + 1);
            double segmentLength = start.distance(end);
            double segmentProgress = segmentLength > 0 ? (targetDistance - currentDist) / segmentLength : 0;
            
            segmentProgress = Math.max(0, Math.min(1, segmentProgress));
            
            double x = start.x + (end.x - start.x) * segmentProgress;
            double y = start.y + (end.y - start.y) * segmentProgress;
            
            updateTextBlockPosition(x, y);
            currentIndexProperty.set(segmentIndex);
        }
    }
    
    private void updatePosition() {
        if (points.isEmpty()) return;
        
        int currentIndex = currentIndexProperty.get();
        if (currentIndex >= points.size()) return;
        
        Point point = points.get(currentIndex);
        updateTextBlockPosition(point.x, point.y);
        
        double progress = points.size() > 1 ? 
            (double) currentIndex / (points.size() - 1) * 100 : 0;
        progressProperty.set(progress);
    }
    
    private void updateTextBlockPosition(double centerX, double centerY) {
        double x = Math.max(0, Math.min(targetPane.getWidth() - textBlock.getWidth(), 
                           centerX - textBlock.getWidth() / 2));
        double y = Math.max(0, Math.min(targetPane.getHeight() - textBlock.getHeight(), 
                           centerY - textBlock.getHeight() / 2));
        
        textBlock.setLayoutX(x);
        textBlock.setLayoutY(y);
    }
    
    /**
     * Dọn dẹp resources
     */
    public void cleanup() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        if (targetPane != null && textBlock != null) {
            targetPane.getChildren().remove(textBlock);
            targetPane.getChildren().removeIf(node -> 
                node instanceof Circle || node instanceof Line);
        }
    }
    
    // Getters cho properties để bind với UI
    public DoubleProperty speedProperty() { return speedProperty; }
    public BooleanProperty isPlayingProperty() { return isPlayingProperty; }
    public DoubleProperty progressProperty() { return progressProperty; }
    public IntegerProperty currentIndexProperty() { return currentIndexProperty; }
    public IntegerProperty stepSizeProperty() { return stepSizeProperty; }
    public StringProperty statusProperty() { return statusProperty; }
    public ObjectProperty<Color> backgroundColorProperty() { return backgroundColorProperty; }
    public ObjectProperty<Color> textColorProperty() { return textColorProperty; }
    public DoubleProperty fontSizeProperty() { return fontSizeProperty; }
    public DoubleProperty borderRadiusProperty() { return borderRadiusProperty; }
    
    // Getters/Setters thông thường
    public void setText(String text) {
        this.displayText = text;
        if (textBlock != null) {
            textBlock.setText(text);
        }
    }
    
    public String getText() { return displayText; }
    public List<Point> getPoints() { return new ArrayList<>(points); }
    public int getPointCount() { return points.size(); }
    public double getTotalDistance() { return totalDistance; }
    
    public void setShowPath(boolean showPath) {
        this.showPath = showPath;
        if (points != null && !points.isEmpty()) {
            drawPath();
        }
    }
    
    public void setShowPoints(boolean showPoints) {
        this.showPoints = showPoints;
        if (points != null && !points.isEmpty()) {
            drawPath();
        }
    }
    
    public boolean isShowPath() { return showPath; }
    public boolean isShowPoints() { return showPoints; }
}