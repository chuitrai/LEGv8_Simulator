package main.java.com.mydomain.legv8simulator.UI;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
import main.java.com.mydomain.legv8simulator.UI.Animation.MovingTextBlock;
import main.java.com.mydomain.legv8simulator.UI.Animation.TextBlockController;
import static main.java.com.mydomain.legv8simulator.UI.Animation.MovingTextBlock.*;
import main.java.com.mydomain.legv8simulator.core.SimulationManager;

import static main.java.com.mydomain.legv8simulator.UI.SimulatorApp.instrWin;
import static main.java.com.mydomain.legv8simulator.UI.SimulatorApp.regWin;

/**
 * Cửa sổ điều khiển mô phỏng, bao gồm cả việc kích hoạt các giai đoạn
 * và điều khiển toàn cục (play, pause, step, speed).
 */
public class AnimationControllerWindow {

    public Stage stage;
    public TextBlockController textBlockController;
    public boolean isPaused = false;
    public SimulationManager prevManager, simManager;

    // UI Controls - Global
    public Button playBtn, playPauseBtn, stepBtn, resetBtn;
    public Slider speedSlider;
    public Label speedValue;

    // UI Controls - Single Cycle
    public Button backCycleBtn, nextCycleBtn;

    public static double rate = 1.0; // Tốc độ mặc định là 1x

    // UI Controls - Simulation Triggers
    public Button fetchBtn, decodeBtn, executeBtn, memoryBtn, writebackBtn, backBtn;
    public Button pipelineBtn;
    
    // UI Controls - Status
    public Label statusLabel, activeBlocksLabel;

    private long cycleCount = 0;

    public enum CpuStage {
        FETCH, DECODE, EXECUTE, MEMORY_ACCESS, WRITEBACK
    }

    private CpuStage currentStage = CpuStage.FETCH;

    public AnimationControllerWindow(TextBlockController textBlockController) {
        this.textBlockController = textBlockController;
        createWindow();
        bindActions();
        startStatusUpdater();
        updatePlayPauseButtonState();
    }

    private void createWindow() {
        stage = new Stage();
        stage.setTitle("Simulation Controller");
        stage.setResizable(true);
        stage.setX(100);
        stage.setY(200);

        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #ecf0f1;");
        
        // Tạo các panel
        VBox globalControls = createGlobalControlPanel();
        GridPane simulationTriggers = createSimulationTriggersPanel();
        HBox otherSimulations = createSingleCyclePanel();
        VBox statusBar = createStatusBar();

        root.getChildren().addAll(
                createSectionLabel("GLOBAL CONTROLS"),
                globalControls,
                new Separator(),
                createSectionLabel("SINGLE STAGE SIMULATION"),
                simulationTriggers,
                new Separator(),
                createSectionLabel("SINGLE CYCLE SIMULATION"),
                otherSimulations,
                statusBar
        );

        Scene scene = new Scene(new ScrollPane(root), 480, 550);
        stage.setScene(scene);
        stage.setOnCloseRequest(this::handleWindowClose);
    }
    
    private VBox createGlobalControlPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #bdc3c7; -fx-background-radius: 8;");
        
        // Play/Pause/Step buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        resetBtn = createSimButton("↺ Reset All", "#c0392b", 120);
        
        playPauseBtn = new Button("▶ Play");
        playPauseBtn.setPrefWidth(120);
        playPauseBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        

        buttonBox.getChildren().addAll(playPauseBtn, resetBtn);
        
        // Speed slider
        HBox speedBox = new HBox(10);
        speedBox.setAlignment(Pos.CENTER_LEFT);
        Label speedLabel = new Label("Tốc độ:");
        speedSlider = new Slider(0.1, 5.0, 1.0);
        speedSlider.setPrefWidth(180);
        speedValue = new Label("1.0x");
        speedValue.setMinWidth(40);
        
        speedBox.getChildren().addAll(speedLabel, speedSlider, speedValue);
        
        panel.getChildren().addAll(buttonBox, speedBox);
        return panel;
    }

    private GridPane createSimulationTriggersPanel() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        fetchBtn = createSimButton("1. Fetch", "#3498db", 150);
        decodeBtn = createSimButton("2. Decode", "#e74c3c", 150);
        executeBtn = createSimButton("3. Execute", "#27ae60", 150);
        memoryBtn = createSimButton("4. Memory", "#9b59b6", 150);
        writebackBtn = createSimButton("5. Writeback", "#f39c12", 150);
        backBtn = createSimButton("◀ Back Stage", "#e67e22", 150);
        stepBtn = createSimButton("Next Stage ▶", "#2980b9", 150);

        grid.add(fetchBtn, 0, 0);
        grid.add(decodeBtn, 1, 0);
        grid.add(executeBtn, 0, 1);
        grid.add(memoryBtn, 1, 1);
        grid.add(writebackBtn, 0, 2);
        grid.add(backBtn, 0, 3);
        grid.add(stepBtn, 1, 3);

        return grid;
    }

    private HBox createSingleCyclePanel() {
        HBox panel = new HBox(10);
        panel.setAlignment(Pos.CENTER);
        playBtn = new Button("▶ Play");
        playBtn.setPrefWidth(120);
        playBtn.setStyle("-fx-background-color:rgb(94, 165, 232); -fx-text-fill: white; -fx-font-weight: bold;");

        backCycleBtn = new Button("⏪ Back Cycle");
        backCycleBtn.setPrefWidth(120);
        backCycleBtn.setStyle("-fx-background-color:rgb(230, 152, 133); -fx-text-fill: white; -fx-font-weight: bold;");

        nextCycleBtn = new Button("Next Cycle ⏩");
        nextCycleBtn.setPrefWidth(120);
        nextCycleBtn.setStyle("-fx-background-color:rgb(100, 232, 164); -fx-text-fill: white; -fx-font-weight: bold;");

        panel.getChildren().addAll( backCycleBtn, playBtn,nextCycleBtn);
        return panel;
    }

    private VBox createStatusBar() {
        VBox statusBar = new VBox(5);
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #34495e; -fx-background-radius: 8;");

        statusLabel = new Label("Trạng thái: Sẵn sàng");
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setFont(Font.font("Consolas", FontWeight.BOLD, 12));

        activeBlocksLabel = new Label("Block đang hoạt động: 0");
        activeBlocksLabel.setTextFill(Color.web("#95a5a6"));
        activeBlocksLabel.setFont(Font.font("Consolas", 11));

        statusBar.getChildren().addAll(statusLabel);
        return statusBar;
    }

    private void bindActions() {
        if (textBlockController == null) return;
        
        // Global Controls
        playBtn.setOnAction(e -> togglePlay());
        playPauseBtn.setOnAction(e -> togglePlayPause());
        stepBtn.setOnAction(e -> {
            nextStage(currentStage);
        });
        
        speedSlider.valueProperty().addListener((obs, oldV, newV) -> {
            rate = newV.doubleValue();
            textBlockController.setAllRates(rate);
            speedValue.setText(String.format("%.1fx", rate));
        });
        textBlockController.setAllRates(speedSlider.getValue());
        // Đảm bảo tốc độ được set ngay khi tạo cửa sổ
        speedValue.setText(String.format("%.1fx", speedSlider.getValue()));
        
        resetBtn.setOnAction(e -> {
            textBlockController.clearAllBlocks();
            simManager = SimulationManager.getInstance();
            simManager.getSimulator().reset();
            cycleCount = 0;
            currentStage = CpuStage.FETCH; // Reset giai đoạn về FETCH
            isPaused = false;
            updatePlayPauseButtonState();
            updateStatus("Đã reset tất cả mô phỏng.");
            regWin.updateRegisterWindow();
            instrWin.loadInstructions();
        });

        backCycleBtn.setOnAction(e -> backCycle());
        nextCycleBtn.setOnAction(e -> { nextCycle();});
        backBtn.setOnAction(e -> backStage());

        fetchBtn.setOnAction(e -> runSingleStage(() -> textBlockController.simulateFetch(null), "Đang mô phỏng FETCH...", CpuStage.FETCH));
        decodeBtn.setOnAction(e -> runSingleStage(() -> textBlockController.simulateDecode(null), "Đang mô phỏng DECODE...", CpuStage.DECODE));
        executeBtn.setOnAction(e -> runSingleStage(() -> textBlockController.simulateExecute(null), "Đang mô phỏng EXECUTE...", CpuStage.EXECUTE));
        memoryBtn.setOnAction(e -> runSingleStage(() -> textBlockController.simulateMemoryAccess(null), "Đang mô phỏng MEMORY...", CpuStage.MEMORY_ACCESS));
        writebackBtn.setOnAction(e -> runSingleStage(() -> textBlockController.simulateWriteback(null), "Đang mô phỏng WRITEBACK...", CpuStage.WRITEBACK));
    }

    private void runSingleStage(Runnable simulation, String statusMessage, CpuStage stage) {
        updateStatus(statusMessage);
        currentStage = stage; // Cập nhật giai đoạn hiện tại
        simulation.run();
        regWin.updateRegisterWindow();
        textBlockController.setAllRates(rate);
    }
    
    private void togglePlayPause() {
        isPaused = !isPaused;
        if (isPaused) {
            textBlockController.pauseAll();
            updateStatus("Đã tạm dừng.");
        } else {
            textBlockController.resumeAll();
            updateStatus("Đang tiếp tục chạy...");
        }
        updatePlayPauseButtonState();
    }

    private void togglePlay() {
    isPaused = false;
    textBlockController.clearAllBlocks();
    simManager = SimulationManager.getInstance();
    simManager.getSimulator().reset();
    updateStatus("Bắt đầu Fetch...");
    runCycle();
    updatePlayPauseButtonState();
    }
    private void nextCycle() {
        textBlockController.clearAllBlocks();
        SimulationManager.getInstance().getSimulator().reset();
        simManager = SimulationManager.getInstance();
        cycleCount++;
        for (int i = 0; i < cycleCount; i++) {
            simManager.cycleSimulation();
        }
        instrWin.loadInstructions();
        runCycle();
        System.out.println("Đã hoàn thành chu kỳ " + cycleCount + ".");
    }

    private void backCycle()
    {
        if (cycleCount <= 0) {
            updateStatus("Không thể quay lại chu kỳ trước, đã ở chu kỳ đầu tiên.");
            return;
        }
        SimulationManager.getInstance().getSimulator().reset();
        simManager = SimulationManager.getInstance();
        textBlockController.clearAllBlocks();
        cycleCount--;        
        for (int i = 0; i < cycleCount; i++) {
            simManager.cycleSimulation();
        }
        instrWin.loadInstructions();
        regWin.updateRegisterWindow();
        runCycle();
        System.out.println("Back Cycle: " + cycleCount);
    }

    private void nextStage(CpuStage currentStage)
    {
        simManager = SimulationManager.getInstance();
        if(cycleCount == 0 && currentStage == CpuStage.FETCH) {
            simManager.stepSimulation(1);
        }
        switch (currentStage) {
            case FETCH:
                this.currentStage = CpuStage.DECODE; // Chuyển sang giai đoạn tiếp theo
                updateStatus("Bắt đầu Decode...");
                textBlockController.simulateDecode(null);
                break;
            case DECODE:
                this.currentStage = CpuStage.EXECUTE; // Chuyển sang giai đoạn tiếp theo
                updateStatus("Bắt đầu Execute...");
                textBlockController.simulateExecute(null);
                break;
            case EXECUTE:
                this.currentStage = CpuStage.MEMORY_ACCESS; // Chuyển sang giai đoạn tiếp theo
                updateStatus("Bắt đầu Memory Access...");
                textBlockController.simulateMemoryAccess(null);
                break;
            case MEMORY_ACCESS:
                this.currentStage = CpuStage.WRITEBACK; // Chuyển sang giai đoạn tiếp theo
                updateStatus("Bắt đầu Writeback...");
                textBlockController.simulateWriteback(null);
                break;
            case WRITEBACK:
                this.currentStage = CpuStage.FETCH; // Reset về FETCH để bắt đầu chu kỳ mới
                updateStatus("Writeback xong. Kết thúc chu kỳ.");
                instrWin.loadInstructions();
                updateStatus("Bắt đầu Fetch...");
                textBlockController.simulateFetch(null);
                cycleCount++;
                break;
        }
        regWin.updateRegisterWindow();
    }

    private void backStage() {
    
    }

    private void runCycle() {
        regWin.updateRegisterWindow();
        instrWin.loadInstructions();
        textBlockController.setAllRates(rate);
        currentStage = CpuStage.FETCH;
        executeCurrentStage();
    }

    private void executeCurrentStage() {
        regWin.updateRegisterWindow();
        switch (currentStage) {
            case FETCH:
                updateStatus("Bắt đầu Fetch...");
                textBlockController.simulateFetch(this::onFetchComplete);
                break;
            case DECODE:
                updateStatus("Bắt đầu Decode...");
                textBlockController.simulateDecode(this::onDecodeComplete);
                break;
            case EXECUTE:
                updateStatus("Bắt đầu Execute...");
                textBlockController.simulateExecute(this::onExecuteComplete);
                break;
            case MEMORY_ACCESS:
                updateStatus("Bắt đầu Memory Access...");
                textBlockController.simulateMemoryAccess(this::onMemoryAccessComplete);
                break;
            case WRITEBACK:
                updateStatus("Bắt đầu Writeback...");
                textBlockController.simulateWriteback(this::onWritebackComplete);
                break;
        }
    }

    private void onFetchComplete() {
        updateStatus("Fetch xong.");
        currentStage = CpuStage.DECODE;
        executeCurrentStage();
    }

    private void onDecodeComplete() {
        updateStatus("Decode xong.");
        currentStage = CpuStage.EXECUTE;
        executeCurrentStage();
    }

    private void onExecuteComplete() {
        updateStatus("Execute xong.");
        currentStage = CpuStage.MEMORY_ACCESS;
        executeCurrentStage();
    }

    private void onMemoryAccessComplete() {
        updateStatus("Memory Access xong.");
        currentStage = CpuStage.WRITEBACK;
        executeCurrentStage();
    }

    private void onWritebackComplete() {
        updateStatus("Writeback xong. Kết thúc chu kỳ.");
        currentStage = CpuStage.FETCH; // Reset về FETCH để bắt đầu chu kỳ mới
        cycleCount++;
        runCycle();
    }

    private void updatePlayPauseButtonState() {
        if (isPaused) {
            playPauseBtn.setText("▶ Resume");
            playPauseBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;"); // Green for resume
        } else {
            playPauseBtn.setText("⏸ Pause");
            playPauseBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;"); // Orange for pause
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText("Trạng thái: " + message);
    }
    
    private void startStatusUpdater() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), e -> {
            if (textBlockController != null) {
                activeBlocksLabel.setText("Block đang hoạt động: " + textBlockController.getActiveBlockCount());
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    private Label createSectionLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        label.setTextFill(Color.web("#2c3e50"));
        return label;
    }
    
    private Button createSimButton(String text, String color, double width) {
        Button btn = new Button(text);
        btn.setPrefWidth(width);
        btn.setPrefHeight(40);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 5;");
        return btn;
    }

    private void handleWindowClose(WindowEvent event) {
        if (textBlockController != null) {
            textBlockController.clearAllBlocks();
        }
        stage.hide();
        event.consume();
    }

    public void show() {
        if (stage != null && !stage.isShowing()) {
            stage.show();
        }
    }

    public void hide() {
        if (stage != null) {
            stage.hide();
        }
    }
}