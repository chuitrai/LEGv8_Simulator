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
        VBox inputPanel = createInputPanel();
        
        root.getChildren().addAll(controlPanel, statusBar, inputPanel);
        
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
        playBtn = new Button("▶ Chạy");
        playBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        playBtn.setPrefWidth(80);
        
        pauseBtn = new Button("⏸ Dừng");
        pauseBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        pauseBtn.setPrefWidth(80);
        
        resetBtn = new Button("🔄 Reset");
        resetBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        resetBtn.setPrefWidth(80);
        
        mainButtons.getChildren().addAll(playBtn, pauseBtn, resetBtn);
        
        // Step controls
        HBox stepControls = new HBox(10);
        stepControls.setAlignment(Pos.CENTER_LEFT);
        
        stepBtn = new Button("👣 Bước tiếp");
        stepBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16;");
        stepBtn.setPrefWidth(120);
        
        Label stepSizeLabel = new Label("Bước/lần:");
        stepSizeLabel.setTextFill(Color.WHITE);
        stepSizeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        
        stepSizeSpinner = new Spinner<>(1, 10, 1);
        stepSizeSpinner.setPrefWidth(70);
        
        stepControls.getChildren().addAll(stepBtn, stepSizeLabel, stepSizeSpinner);
        
        buttonBox.getChildren().addAll(mainButtons, stepControls);
        
        panel.getChildren().addAll(speedBox, new Separator(), buttonBox);
        
        return panel;
    }
    
    private HBox createStatusBar() {
        HBox statusBar = new HBox(15);
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #34495e; -fx-background-radius: 8;");
        statusBar.setAlignment(Pos.CENTER_LEFT);
        
        VBox statusBox = new VBox(5);
        statusLabel = new Label("Trạng thái: Đã dừng");
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        
        currentPointLabel = new Label("Điểm hiện tại: 0/0");
        currentPointLabel.setTextFill(Color.web("#95a5a6"));
        currentPointLabel.setFont(Font.font("Consolas", 11));
        
        progressLabel = new Label("Tiến độ: 0%");
        progressLabel.setTextFill(Color.web("#95a5a6"));
        progressLabel.setFont(Font.font("Consolas", 11));
        
        statusBox.getChildren().addAll(statusLabel, currentPointLabel, progressLabel);
        statusBar.getChildren().add(statusBox);
        
        return statusBar;
    }
    
    private VBox createInputPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color: white; -fx-border-color: #ecf0f1; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8;");
        
        // Text input section
        VBox textSection = new VBox(8);
        Label textLabel = new Label("📝 Nội dung hiển thị:");
        textLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        
        textInput = new TextField("HELLO WORLD!");
        textInput.setPromptText("Nhập text để hiển thị...");
        
        textSection.getChildren().addAll(textLabel, textInput);
        
        // Style controls section
        VBox styleSection = new VBox(10);
        Label styleLabel = new Label("🎨 Tùy chỉnh giao diện:");
        styleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        
        // Color controls
        HBox colorControls = new HBox(20);
        colorControls.setAlignment(Pos.CENTER_LEFT);
        
        VBox bgColorBox = new VBox(5);
        bgColorBox.getChildren().addAll(
            new Label("Màu nền:"), 
            bgColorPicker = new ColorPicker(Color.web("#3498db"))
        );
        
        VBox textColorBox = new VBox(5);
        textColorBox.getChildren().addAll(
            new Label("Màu chữ:"), 
            textColorPicker = new ColorPicker(Color.WHITE)
        );
        
        colorControls.getChildren().addAll(bgColorBox, textColorBox);
        
        // Size controls
        HBox sizeControls = new HBox(20);
        sizeControls.setAlignment(Pos.CENTER_LEFT);
        
        VBox fontSizeBox = new VBox(5);
        fontSizeSlider = new Slider(12, 32, 18);
        fontSizeSlider.setPrefWidth(120);
        fontSizeSlider.setShowTickMarks(true);
        fontSizeSlider.setMajorTickUnit(5);
        fontSizeValue = new Label("18px");
        
        HBox fontSizeControls = new HBox(10);
        fontSizeControls.getChildren().addAll(fontSizeSlider, fontSizeValue);
        fontSizeBox.getChildren().addAll(new Label("Kích thước chữ:"), fontSizeControls);
        
        VBox borderRadiusBox = new VBox(5);
        borderRadiusSlider = new Slider(0, 20, 8);
        borderRadiusSlider.setPrefWidth(120);
        borderRadiusSlider.setShowTickMarks(true);
        borderRadiusSlider.setMajorTickUnit(5);
        borderRadiusValue = new Label("8px");
        
        HBox borderControls = new HBox(10);
        borderControls.getChildren().addAll(borderRadiusSlider, borderRadiusValue);
        borderRadiusBox.getChildren().addAll(new Label("Bo góc:"), borderControls);
        
        sizeControls.getChildren().addAll(fontSizeBox, borderRadiusBox);
        
        styleSection.getChildren().addAll(styleLabel, colorControls, sizeControls);
        
        // Display options
        HBox displayOptions = new HBox(20);
        showPathCheckBox = new CheckBox("Hiển thị đường đi");
        showPathCheckBox.setSelected(true);
        
        showPointsCheckBox = new CheckBox("Hiển thị các điểm");
        showPointsCheckBox.setSelected(true);
        
        displayOptions.getChildren().addAll(showPathCheckBox, showPointsCheckBox);
        
        // Coordinates input section
        VBox coordSection = new VBox(8);
        Label coordLabel = new Label("📍 Tọa độ các điểm (x,y mỗi dòng):");
        coordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        
        coordinatesInput = new TextArea();
        coordinatesInput.setPrefRowCount(8);
        coordinatesInput.setText("100,100\n200,150\n300,200\n400,100\n500,300\n200,400\n100,350\n50,200");
        coordinatesInput.setPromptText("Nhập tọa độ theo định dạng: x,y");
        
                coordSection.getChildren().addAll(coordLabel, coordinatesInput);

        panel.getChildren().addAll(textSection, styleSection, displayOptions, coordSection);
        return panel;
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

    // Step size
    stepSizeSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
        animationComponent.stepSizeProperty().set(newVal);
    });

    // Text input
    textInput.textProperty().addListener((obs, oldVal, newVal) -> {
        animationComponent.setText(newVal);
    });

    // Coordinates input
    coordinatesInput.textProperty().addListener((obs, oldVal, newVal) -> {
        animationComponent.setPoints(newVal);
    });

    // Color pickers
    bgColorPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
        animationComponent.backgroundColorProperty().set(newVal);
    });
    textColorPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
        animationComponent.textColorProperty().set(newVal);
    });

    // Font size
    fontSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
        animationComponent.fontSizeProperty().set(newVal.intValue());
        fontSizeValue.setText(newVal.intValue() + "px");
    });

    // Border radius
    borderRadiusSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
        animationComponent.borderRadiusProperty().set(newVal.intValue());
        borderRadiusValue.setText(newVal.intValue() + "px");
    });

    // Display options
    showPathCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
        animationComponent.setShowPath(newVal);
    });
    showPointsCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
        animationComponent.setShowPoints(newVal);
    });

    // Trạng thái animation cập nhật lên status bar
    animationComponent.statusProperty().addListener((obs, oldVal, newVal) -> {
        statusLabel.setText("Trạng thái: " + newVal);
    });
    animationComponent.currentIndexProperty().addListener((obs, oldVal, newVal) -> {
        currentPointLabel.setText("Điểm hiện tại: " + newVal + "/" + animationComponent.getPointCount());
    });
    animationComponent.progressProperty().addListener((obs, oldVal, newVal) -> {
        progressLabel.setText("Tiến độ: " + String.format("%.0f", newVal.doubleValue()) + "%");
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