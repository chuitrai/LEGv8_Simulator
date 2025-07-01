package main.java.com.mydomain.legv8simulator.UI;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PathHighlightDemo extends Application {

    @Override
    public void start(Stage stage) {
        // Tạo khối di chuyển
        Rectangle rect = new Rectangle(0, 0, 100, 50);
        rect.setFill(Color.DODGERBLUE);
        Text label = new Text(20, 30, "Xin chào!");
        label.setFill(Color.WHITE);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Group block = new Group(rect, label);

        // Gộp đường thành một Path duy nhất
        Path bigPath = new Path(
            new MoveTo(100, 100),
            new LineTo(300, 100),
            new LineTo(300, 200),
            new LineTo(100, 200)
        );
        bigPath.setStroke(Color.GRAY);
        bigPath.setStrokeWidth(5);
        bigPath.setId("mainPath");

        // Cho khối chạy trên đường
        PathTransition transition = new PathTransition();
        transition.setNode(block);
        transition.setPath(bigPath);
        transition.setDuration(javafx.util.Duration.seconds(6));
        transition.setCycleCount(1);
        transition.setAutoReverse(false);

        // Nút chạy lại
        Button runBtn = new Button("Chạy lại");
        runBtn.setOnAction(e -> {
            block.setTranslateX(0); block.setTranslateY(0);
            transition.playFromStart();
        });

        // Nút highlight cả đường
        Button highlightBtn = new Button("Highlight đường");
        highlightBtn.setOnAction(e -> {
            bigPath.setStroke(Color.ORANGERED);
            bigPath.getStrokeDashArray().setAll(10.0, 5.0); // gạch gạch
        });

        // Nút bỏ highlight
        Button resetBtn = new Button("Bỏ highlight");
        resetBtn.setOnAction(e -> {
            bigPath.setStroke(Color.GRAY);
            bigPath.getStrokeDashArray().clear();
        });

        Pane pane = new Pane(bigPath, block);
        pane.setPrefSize(500, 300);

        VBox root = new VBox(10, pane, runBtn, highlightBtn, resetBtn);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Gộp 3 đường thành 1 và highlight");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
