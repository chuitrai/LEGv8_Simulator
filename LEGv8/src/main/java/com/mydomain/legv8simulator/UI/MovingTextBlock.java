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
import main.java.com.mydomain.legv8simulator.UI.datapath.DatapathGraphicsFX.*;
import main.java.com.mydomain.legv8simulator.core.SimulationManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Point class để định nghĩa tọa độ
class Point {
    public double x, y;
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

// PathSegment class để định nghĩa từng đoạn đường
class PathSegment {
    public Point start, end;
    public double duration;
    
    public PathSegment(Point start, Point end, double duration) {
        this.start = start;
        this.end = end;
        this.duration = duration;
    }

    public PathSegment(Point start, Point end) {
        this(start, end, 2.0); // Mặc định duration là 2 giây
    }
    
    public PathSegment(double x1, double y1, double x2, double y2, double duration) {
        this(new Point(x1, y1), new Point(x2, y2), duration);
    }
}

// Moving Text Block với khả năng di chuyển theo nhiều đoạn thẳng
public class MovingTextBlock extends StackPane {
    private String content;
    private Rectangle background;
    private Text text;
    private List<PathSegment> path;
    private int currentSegmentIndex;
    private Timeline currentAnimation;
    
    public MovingTextBlock(String content, String color) {
        this.content = content;
        this.path = new ArrayList<>();
        this.currentSegmentIndex = 0;
        
        background = new Rectangle();
        background.setFill(Color.web(color));
        background.setStroke(Color.WHITE);
        background.setStrokeWidth(2);
        background.setArcWidth(8);
        background.setArcHeight(8);
        
        text = new Text(content);
        text.setFill(Color.WHITE);
        text.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        // Tự động điều chỉnh kích thước
        updateSize();
        
        getChildren().addAll(background, text);
    }
    
    private void updateSize() {
        double textWidth = text.getBoundsInLocal().getWidth();
        double textHeight = text.getBoundsInLocal().getHeight();
        background.setWidth(textWidth + 20);
        background.setHeight(textHeight + 12);
    }
    
    public void updateContent(String newContent) {
        this.content = newContent;
        text.setText(newContent);
        updateSize();
    }
    
    public String getContent() {
        return content;
    }
    
    // Thiết lập đường đi bằng List các PathSegment
    public void setPath(List<PathSegment> path) {
        this.path = new ArrayList<>(path);
        this.currentSegmentIndex = 0;
    }
    
    // Thêm một đoạn đường vào path
    public void addPathSegment(PathSegment segment) {
        path.add(segment);
    }
    
    public void addPathSegment(double x1, double y1, double x2, double y2, double duration) {
        path.add(new PathSegment(x1, y1, x2, y2, duration));
    }
    
    // Bắt đầu di chuyển theo path đã định sẵn
    public void startMoving() {
        if (path.isEmpty()) return;
        
        currentSegmentIndex = 0;
        // Đặt vị trí ban đầu
        PathSegment firstSegment = path.get(0);
        setLayoutX(firstSegment.start.x);
        setLayoutY(firstSegment.start.y);
        
        moveToNextSegment();
    }
    
    private void moveToNextSegment() {
        if (currentSegmentIndex >= path.size()) {
            onPathCompleted();
            return;
        }
        
        PathSegment segment = path.get(currentSegmentIndex);
        
        // Tạo animation cho đoạn hiện tại
        TranslateTransition move = new TranslateTransition(Duration.seconds(segment.duration), this);
        
        // Tính toán vị trí relative từ vị trí hiện tại
        double currentX = getLayoutX() + getTranslateX();
        double currentY = getLayoutY() + getTranslateY();
        
        move.setToX(getTranslateX() + (segment.end.x - currentX));
        move.setToY(getTranslateY() + (segment.end.y - currentY));
        
        move.setOnFinished(e -> {
            // Cập nhật vị trí thực tế
            setLayoutX(segment.end.x);
            setLayoutY(segment.end.y);
            setTranslateX(0);
            setTranslateY(0);
            
            currentSegmentIndex++;
            
            // Pause nhỏ giữa các đoạn (tùy chọn)
            Timeline pause = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                moveToNextSegment();
            }));
            pause.play();
        });
        
        currentAnimation = new Timeline();
        currentAnimation.getKeyFrames().add(new KeyFrame(Duration.seconds(segment.duration), 
            new KeyValue(this.translateXProperty(), move.getToX()),
            new KeyValue(this.translateYProperty(), move.getToY())
        ));
        currentAnimation.play();
    }
    
    // Được gọi khi hoàn thành toàn bộ path
    protected void onPathCompleted() {
        // Override để xử lý khi hoàn thành path
    }
    
    // Dừng animation hiện tại
    public void stopMoving() {
        if (currentAnimation != null) {
            currentAnimation.stop();
        }
    }
    
    // Reset về vị trí ban đầu
    public void reset() {
        stopMoving();
        currentSegmentIndex = 0;
        setTranslateX(0);
        setTranslateY(0);
        if (!path.isEmpty()) {
            PathSegment firstSegment = path.get(0);
            setLayoutX(firstSegment.start.x);
            setLayoutY(firstSegment.start.y);
        }
    }

        // Thêm những phương thức này vào trong lớp MovingTextBlock

    /**
     * Tạm dừng animation.
     */
    public void pause() {
        if (currentAnimation != null) {
            currentAnimation.pause();
        }
    }

    /**
     * Tiếp tục animation đã bị tạm dừng.
     */
    public void resume() {
        if (currentAnimation != null) {
            currentAnimation.play();
        }
    }

    /**
     * Điều chỉnh tốc độ animation.
     * @param rate Tốc độ mới (1.0 là bình thường, 2.0 là nhanh gấp đôi).
     */
    public void setRate(double rate) {
        if (currentAnimation != null) {
            currentAnimation.setRate(rate);
        }
    }

    /**
     * Di chuyển animation tới một khoảng thời gian nhỏ (step).
     * Animation phải ở trạng thái paused để step có hiệu quả.
     * @param duration Khoảng thời gian để nhảy tới.
     */
    public void step(Duration duration) {
        if (currentAnimation != null) {
            // Luôn pause trước khi jump để đảm bảo hành vi nhất quán
            currentAnimation.pause();
            // Lấy thời gian hiện tại và cộng thêm một khoảng nhỏ
            Duration newTime = currentAnimation.getCurrentTime().add(duration);
            // Nhảy đến thời gian đó
            currentAnimation.jumpTo(newTime);
        }
    }
}

