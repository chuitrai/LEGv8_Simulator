package main.java.com.mydomain.legv8simulator.UI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import main.java.com.mydomain.legv8simulator.UI.TextAnimationComponent;

/**
 * Cửa sổ điều khiển cho TextAnimationComponent
 * Có thể điều khiển animation trên bất kỳ Pane nào
 */
public class AnimationControllerWindow {
    
    private Stage stage;
    private TextAnimationComponent animationComponent;
    
    // UI Controls
    private Slider speedSlider;
    private Label speedValue;
    private Button playBtn, pauseBtn, resetBtn, stepBtn;
    private Label statusLabel, currentPointLabel, progressLabel;
    private TextField textInput;
    private TextArea coordinatesInput;
    private ColorPicker bgColorPicker, textColorPicker;
    private Slider fontSizeSlider, borderRadiusSlider;
    private Label fontSizeValue, borderRadiusValue;
    private Spinner<Integer> stepSizeSpinner;
    private CheckBox showPathCheckBox, showPointsCheckBox;
    
    /**
     * Constructor
     * @param animationComponent Component animation để điều khiển
     */
    public AnimationControllerWindow(TextAnimationComponent animationComponent) {
        this.animationComponent = animationComponent;
        createWindow();
        bindProperties();
    }
    
    private void createWindow() {
        stage = new Stage();
        stage.setTitle("Animation Controller");
        stage.setResizable(true);
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        
        // Tạo các panel
        HBox controlPanel = createControlPanel();
        HBox statusBar = createStatusBar();
        
        root.getChildren().addAll(controlPanel, statusBar);
        
        Scene scene = new Scene(new ScrollPane(root), 500, 600);
        stage.setScene(scene);
        
        // Xử lý khi đóng cửa sổ
        stage.setOnCloseRequest(this::handleWindowClose);
    }
    
    private HBox createControlPanel() {
        HBox panel = new HBox(15);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 8;");
        panel.setAlignment(Pos.CENTER_LEFT);
        
        // Speed control
        VBox speedBox = new VBox(5);
        Label speedLabel = new Label("Tốc độ:");
        speedLabel.setTextFill(Color.WHITE);
        speedLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        speedSlider = new Slider(1, 100, 50);
        speedSlider.setPrefWidth(150);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(25);
        
        speedValue = new Label("50");
        speedValue.setTextFill(Color.WHITE);
        speedValue.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        speedValue.setMinWidth(30);
        speedValue.setAlignment(Pos.CENTER);
        
        HBox speedControls = new HBox(10);
        speedControls.setAlignment(Pos.CENTER_LEFT);
        speedControls.getChildren().addAll(speedSlider, speedValue);
        
        speedBox.getChildren().addAll(speedLabel, speedControls);
        
        // Control buttons
        VBox buttonBox = new VBox(10);
        
        HBox mainButtons = new HBox(10);
        playBtn = new Button("▶ Run");
        playBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        playBtn.setPrefWidth(80);

        pauseBtn = new Button("⏸ Pause");
        pauseBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        pauseBtn.setPrefWidth(80);
        
        resetBtn = new Button("🔄 Reset");
        resetBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        resetBtn.setPrefWidth(80);
        
        mainButtons.getChildren().addAll(playBtn, pauseBtn, resetBtn);
        
        // Step controls
        HBox stepControls = new HBox(10);
        stepControls.setAlignment(Pos.CENTER_LEFT);
        
        stepBtn = new Button("👣 Next Step");
        stepBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        stepBtn.setPrefWidth(120);
        
        
        stepSizeSpinner = new Spinner<>(1, 10, 1);
        stepSizeSpinner.setPrefWidth(70);
        
        stepControls.getChildren().addAll(stepBtn, stepSizeSpinner);
        
        buttonBox.getChildren().addAll(mainButtons, stepControls);
        
        panel.getChildren().addAll(speedBox, new Separator(), buttonBox);
        
        return panel;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(15);
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #34495e; -fx-background-radius: 8;");
        statusBar.setAlignment(Pos.CENTER_LEFT);
        
        return statusBar;
    }

    private void bindProperties() {
    if (animationComponent == null) return;

    // Tốc độ
    speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
        animationComponent.speedProperty().set(newVal.doubleValue());
        speedValue.setText(String.format("%.0f", newVal.doubleValue()));
    });

    // Play/Pause/Reset/Step
    playBtn.setOnAction(e -> animationComponent.play());
    pauseBtn.setOnAction(e -> animationComponent.pause());
    resetBtn.setOnAction(e -> animationComponent.reset());
    stepBtn.setOnAction(e -> animationComponent.step());

    // Coordinates input
    coordinatesInput.textProperty().addListener((obs, oldVal, newVal) -> {
        animationComponent.setPoints(newVal);
    });

}

    private void handleWindowClose(WindowEvent event) {
        // Khi đóng cửa sổ controller, có thể dừng animation hoặc làm gì đó nếu muốn
        animationComponent.pause();
        stage.hide();
    }

    public void show() {
        if (stage != null) stage.show();
    }

    public void hide() {
        if (stage != null) stage.hide();
    }
}