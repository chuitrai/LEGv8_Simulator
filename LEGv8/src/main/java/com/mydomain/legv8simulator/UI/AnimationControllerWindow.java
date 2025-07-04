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
import main.java.com.mydomain.legv8simulator.UI.Animation.TextBlockController;
import main.java.com.mydomain.legv8simulator.core.SimulationManager;

/**
 * Cửa sổ điều khiển mô phỏng, bao gồm cả việc kích hoạt các giai đoạn
 * và điều khiển toàn cục (play, pause, step, speed).
 */
public class AnimationControllerWindow {

    public Stage stage;
    public TextBlockController textBlockController;
    public boolean isPaused = false;
    public SimulationManager simManager;

    // UI Controls - Global
    public Button playBtn, playPauseBtn, stepBtn, resetBtn;
    public Slider speedSlider;
    public Label speedValue;

    // UI Controls - Simulation Triggers
    public Button fetchBtn, decodeBtn, executeBtn, memoryBtn, writebackBtn;
    public Button pipelineBtn, complexBtn;
    
    // UI Controls - Status
    public Label statusLabel, activeBlocksLabel;

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
        HBox otherSimulations = createOtherSimulationsPanel();
        VBox statusBar = createStatusBar();

        root.getChildren().addAll(
                createSectionLabel("GLOBAL CONTROLS"),
                globalControls,
                new Separator(),
                createSectionLabel("SINGLE CYCLE SIMULATION"),
                simulationTriggers,
                new Separator(),
                createSectionLabel("OTHER SIMULATIONS"),
                otherSimulations,
                statusBar
        );

        Scene scene = new Scene(new ScrollPane(root), 480, 600);
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

         playBtn = new Button("▶ Play");
        playBtn.setPrefWidth(120);
        playBtn.setStyle("-fx-background-color:rgb(94, 165, 232); -fx-text-fill: white; -fx-font-weight: bold;");
        
        playPauseBtn = new Button("▶ Play");
        playPauseBtn.setPrefWidth(120);
        playPauseBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        
        stepBtn = new Button("👣 Step");
        stepBtn.setPrefWidth(100);
        stepBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");

        buttonBox.getChildren().addAll(playBtn, playPauseBtn, stepBtn);
        
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
        grid.setHgap(10);
        grid.setVgap(10);

        fetchBtn = createSimButton("1. Fetch", "#3498db", 150);
        decodeBtn = createSimButton("2. Decode", "#e74c3c", 150);
        executeBtn = createSimButton("3. Execute", "#27ae60", 150);
        memoryBtn = createSimButton("4. Memory", "#9b59b6", 150);
        writebackBtn = createSimButton("5. Writeback", "#f39c12", 150);

        grid.add(fetchBtn, 0, 0);
        grid.add(decodeBtn, 1, 0);
        grid.add(executeBtn, 0, 1);
        grid.add(memoryBtn, 1, 1);
        grid.add(writebackBtn, 0, 2);

        return grid;
    }

    private HBox createOtherSimulationsPanel() {
        HBox panel = new HBox(10);
        panel.setAlignment(Pos.CENTER_LEFT);

        pipelineBtn = createSimButton("Pipeline", "#16a085", 120);
        complexBtn = createSimButton("Complex Path", "#e67e22", 120);
        resetBtn = createSimButton("Reset All", "#c0392b", 120);

        panel.getChildren().addAll(pipelineBtn, complexBtn, resetBtn);
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

        statusBar.getChildren().addAll(statusLabel, activeBlocksLabel);
        return statusBar;
    }

    private void bindActions() {
        if (textBlockController == null) return;
        
        // Global Controls
        playBtn.setOnAction(e -> togglePlay());
        playPauseBtn.setOnAction(e -> togglePlayPause());
        stepBtn.setOnAction(e -> {
            if (!isPaused) {
                togglePlayPause(); // Pause first before stepping
            }
            textBlockController.stepAll();
            updateStatus("Đã thực hiện một bước (step).");
        });
        
        speedSlider.valueProperty().addListener((obs, oldV, newV) -> {
            double rate = newV.doubleValue();
            textBlockController.setAllRates(rate);
            speedValue.setText(String.format("%.1fx", rate));
        });
        
        resetBtn.setOnAction(e -> {
            textBlockController.clearAllBlocks();
            simManager = SimulationManager.getInstance();
            simManager.reset();

            isPaused = false;
            updatePlayPauseButtonState();
            updateStatus("Đã reset tất cả mô phỏng.");
        });

<<<<<<< HEAD
        fetchBtn.setOnAction(e -> runSingleStage(() -> textBlockController.simulateFetch(null), "Đang mô phỏng FETCH..."));
        decodeBtn.setOnAction(e -> runSingleStage(() -> textBlockController.simulateDecode(null), "Đang mô phỏng DECODE..."));
        executeBtn.setOnAction(e -> runSingleStage(() -> textBlockController.simulateExecute(null), "Đang mô phỏng EXECUTE..."));
        memoryBtn.setOnAction(e -> runSingleStage(() -> textBlockController.simulateMemoryAccess(null), "Đang mô phỏng MEMORY..."));
        writebackBtn.setOnAction(e -> runSingleStage(() -> textBlockController.simulateWriteback(null), "Đang mô phỏng WRITEBACK..."));
    }

    private void runSingleStage(Runnable simulation, String statusMessage) {
        updateStatus(statusMessage);
        simulation.run();
        // Khi một mô phỏng mới bắt đầu, đảm bảo nó đang chạy
        if (isPaused) {
            togglePlayPause();
        }
=======
        // Simulation Triggers
        fetchBtn.setOnAction(e -> runSimulation(textBlockController::simulateFetch, "Đang mô phỏng FETCH..."));
        decodeBtn.setOnAction(e -> runSimulation(textBlockController::simulateDecode, "Đang mô phỏng DECODE..."));
        executeBtn.setOnAction(e -> runSimulation(textBlockController::simulateExecute, "Đang mô phỏng EXECUTE..."));
        memoryBtn.setOnAction(e -> runSimulation(textBlockController::simulateMemoryAccess, "Đang mô phỏng MEMORY..."));
        writebackBtn.setOnAction(e -> runSimulation(textBlockController::simulateWriteback, "Đang mô phỏng WRITEBACK..."));
        
>>>>>>> 1b3341c828bf2b0bd623d9aa89d89e98ba6ddfae
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
        updateStatus("Bắt đầu Fetch...");
        do {
        textBlockController.simulateFetch(() -> {
            updateStatus("Fetch xong. Bắt đầu Decode...");
            
            textBlockController.simulateDecode(() -> {
                updateStatus("Decode xong. Bắt đầu Execute...");
                
                textBlockController.simulateExecute(() -> {
                    updateStatus("Execute xong. Bắt đầu Memory Access...");
                    
                    textBlockController.simulateMemoryAccess(() -> {
                        updateStatus("Memory Access xong. Bắt đầu Writeback...");
                        
                        textBlockController.simulateWriteback(() -> {
                            updateStatus("Writeback xong. Kết thúc chu kỳ...");
                        });
                    });
                });
            });
        });
        simManager = SimulationManager.getInstance();
        } while(simManager.getSimulator().id_ex_latch != null);
        updatePlayPauseButtonState();
    }
    
    private void updatePlayPauseButtonState() {
        if (isPaused) {
            playPauseBtn.setText("▶ Resume");
            playPauseBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;"); // Green for resume
            stepBtn.setDisable(false);
        } else {
            playPauseBtn.setText("⏸ Pause");
            playPauseBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;"); // Orange for pause
            stepBtn.setDisable(true); // Can only step when paused
        }
    }

    private void runSimulation(Runnable simulation, String statusMessage) {
        updateStatus(statusMessage);
        simulation.run();
        // Khi một mô phỏng mới bắt đầu, đảm bảo nó đang chạy (không bị pause)
        if (isPaused) {
            isPaused = false;
            textBlockController.resumeAll();
            updatePlayPauseButtonState();
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