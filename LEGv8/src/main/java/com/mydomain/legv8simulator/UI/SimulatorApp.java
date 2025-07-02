package main.java.com.mydomain.legv8simulator.UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.com.mydomain.legv8simulator.UI.datapath.LEGv8Datapath;
import main.java.com.mydomain.legv8simulator.core.SimulationManager;



import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class SimulatorApp extends Application {
    
    // Sử dụng SimulationManager thay vì các biến riêng lẻ
    private SimulationManager simManager;
    
    // Method để assembler có thể gọi từ TextFileEditor

    @Override
    public void start(Stage primaryStage) {
        
        simManager = SimulationManager.getInstance();
        
        // Cửa sổ chính khởi động
        Stage mainStage = new Stage();
        VBox mainRoot = new VBox(20);
        mainRoot.setPadding(new javafx.geometry.Insets(30));
        mainRoot.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        
        Label titleLabel = new Label("LEGv8 Simulator");
        titleLabel.setId("title-label");
        
        Button btnStart = new Button("Start Simulation");
        btnStart.setId("start-button");

        Button btnLoad = new Button("Select Assembly File");
        btnLoad.setId("file-button");

        Label fileLabel = new Label("No file selected");
        fileLabel.setId("status-label");
        
        Label statusLabel = new Label("Ready to load assembly file");
        statusLabel.setId("status-label");
    
        // Bố cục lại
        mainRoot.getChildren().addAll(titleLabel, btnStart, btnLoad, fileLabel, statusLabel);
        mainRoot.getStylesheets().add(getClass().getResource("style/style.css").toExternalForm());

        Scene mainScene = new Scene(mainRoot, 400, 300);
        mainStage.setTitle("LEGv8 Simulator");
        mainStage.setScene(mainScene);
        mainStage.show();

        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Open Assembly File");
        fileChooser.getExtensionFilters().addAll(
            new javafx.stage.FileChooser.ExtensionFilter("Assembly Files", "*.s", "*.asm"),
            new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new javafx.stage.FileChooser.ExtensionFilter("All Files", "*.*")
        );
        
        final java.io.File[] selectedFile = {null};
        java.io.File file = new java.io.File("resources/assembly_examples/example_1.s");
        if (file != null) {
                selectedFile[0] = file;
                simManager.setCurrentFileName(file.getName());
                fileLabel.setText("Selected: " + file.getName());
                statusLabel.setText("File loaded. Please edit and assemble in editor.");
                
                // Hiển thị cửa sổ chỉnh sửa file và truyền callback với assembler
                TextFileEditor.show(mainStage, file, (assembledCode, originalLines) -> {
                    // Callback được gọi khi assemble thành công
                    simManager.setMachineCode(assembledCode);
                    simManager.setAssemblyLines(originalLines);
                    statusLabel.setText("Assembly successful! Ready to simulate.");
                    btnStart.setDisable(false); // Kích hoạt nút Start
                }, simManager.getAssembler());
            }

        btnLoad.setOnAction(e -> {
            // java.io.File file = fileChooser.showOpenDialog(mainStage);
            if (file != null) {
                selectedFile[0] = file;
                simManager.setCurrentFileName(file.getName());
                fileLabel.setText("Selected: " + file.getName());
                statusLabel.setText("File loaded. Please edit and assemble in editor.");
                
                // Hiển thị cửa sổ chỉnh sửa file và truyền callback với assembler
                TextFileEditor.show(mainStage, file, (assembledCode, originalLines) -> {
                    // Callback được gọi khi assemble thành công
                    simManager.setMachineCode(assembledCode);
                    simManager.setAssemblyLines(originalLines);
                    statusLabel.setText("Assembly successful! Ready to simulate.");
                    btnStart.setDisable(false); // Kích hoạt nút Start
                }, simManager.getAssembler());
            }
            else {
                showErrorDialog("File Error", "No file selected");
            }
        });

        // Khi nhấn Start, mở các cửa sổ chính và chạy simulation
        btnStart.setOnAction(e -> {
            if (simManager.isAssembled()) {
                try {
                    // Load chương trình vào simulator
                    if (simManager.loadProgram(0x00)) {
                        // Đóng cửa sổ chính
                        mainStage.close();
                        
                        // Mở các cửa sổ hiển thị
                        // Chạy simulation
                        runSimulation();

                        openSimulationWindows(primaryStage);
                        
                    } else {
                        showErrorDialog("Load Error", "Failed to load program into simulator");
                    }
                } catch (Exception ex) {
                    showErrorDialog("Simulation Error", "Failed to start simulation: " + ex.getMessage());
                }
            }
        });
    }
    
    private void openSimulationWindows(Stage primaryStage) {
        // Mở cửa sổ hiển thị registers
        RegisterWindow regWin = new RegisterWindow();
        // Cập nhật với dữ liệu thực từ CPU
        regWin.show();

        // Mở cửa sổ hiển thị instruction memory
        InstructionMemoryWindow instrWin = new InstructionMemoryWindow();
        // Cập nhật với dữ liệu thực từ memory
        // updateInstructionWindow(instrWin);
        instrWin.show();

        // Mở cửa sổ datapath visualization
        LEGv8Datapath datapathPane = new LEGv8Datapath();
        Scene scene = new Scene(datapathPane, 1200, 800);

        TextBlockController textBlockController = new TextBlockController(datapathPane);


        AnimationControllerWindow animController = new AnimationControllerWindow(textBlockController);
        animController.show();
        primaryStage.setTitle("LEGv8 Datapath Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    
    
    
    private void runSimulation() {
        try {
            // Chạy simulation với số bước giới hạn
            if (simManager.runSimulation(100)) {
                System.out.println("Simulation completed successfully!");
            } else {
                showErrorDialog("Simulation Error", "Failed to run simulation");
            }
        } catch (Exception e) {
            System.err.println("Simulation error: " + e.getMessage());
            showErrorDialog("Simulation Error", "Error during simulation: " + e.getMessage());
        }
    }
    
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}