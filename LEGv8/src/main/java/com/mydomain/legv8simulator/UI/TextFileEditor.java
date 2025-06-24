package main.java.com.mydomain.legv8simulator.UI;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.java.com.mydomain.legv8simulator.assembler.Assembler;
import main.java.com.mydomain.legv8simulator.assembler.AssemblyException;
import main.java.com.mydomain.legv8simulator.core.SimulationManager;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class TextFileEditor {
    
    // Overloaded method với callback cho assemble
    public static void show(Stage owner, File file, BiConsumer<int[], List<String>> assembleCallback) {
        show(owner, file, assembleCallback, null);
    }
    
    // Overloaded method với callback và assembler
    public static void show(Stage owner, File file, BiConsumer<int[], List<String>> assembleCallback, Assembler assembler) {
        showInternal(owner, file, assembleCallback, assembler);
    }
    
    // Method gốc không có callback
    public static void show(Stage owner, File file) {
        showInternal(owner, file, null, null);
    }
    
    // Method chính với đầy đủ tham số
    private static void showInternal(Stage owner, File file, BiConsumer<int[], List<String>> assembleCallback, Assembler assembler) {
        Stage stage = new Stage();
        stage.setTitle("Assembly/Machine Code - " + file.getName());
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);

        BorderPane root = new BorderPane();
        TextArea textArea = new TextArea();
        textArea.setStyle("-fx-font-family: 'Consolas'; -fx-font-size: 14px;");

        // Đọc file vào textArea
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            textArea.setText(sb.toString());
        } catch (IOException ex) {
            textArea.setText("Lỗi đọc file!");
        }

        // MenuBar
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");

        MenuItem newFile = new MenuItem("New");
        newFile.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+N"));

        MenuItem openFile = new MenuItem("Open...");
        openFile.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+O"));

        MenuItem saveFile = new MenuItem("Save");
        saveFile.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+S"));

        MenuItem saveAs = new MenuItem("Save As...");
        MenuItem close = new MenuItem("Close");
        close.setAccelerator(javafx.scene.input.KeyCombination.keyCombination("Ctrl+C"));

        menuFile.getItems().addAll(newFile, openFile, saveFile, saveAs, close);
        menuBar.getMenus().addAll(menuFile);

        // --- Xử lý chức năng ---
        newFile.setOnAction(e -> textArea.clear());

        openFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            File open = fileChooser.showOpenDialog(stage);
            if (open != null) {
                try (BufferedReader br = new BufferedReader(new FileReader(open))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append('\n');
                    }
                    textArea.setText(sb.toString());
                    stage.setTitle("Assembly/Machine Code - " + open.getName());
                } catch (IOException ex) {
                    new Alert(Alert.AlertType.ERROR, "Lỗi đọc file!").showAndWait();
                }
            }
        });

        saveFile.setOnAction(e -> {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write(textArea.getText());
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi lưu file!").showAndWait();
            }
        });

        saveAs.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save As");
            fileChooser.setInitialFileName(file.getName());
            File saveAsFile = fileChooser.showSaveDialog(stage);
            if (saveAsFile != null) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(saveAsFile))) {
                    bw.write(textArea.getText());
                } catch (IOException ex) {
                    new Alert(Alert.AlertType.ERROR, "Lỗi lưu file!").showAndWait();
                }
            }
        });

        close.setOnAction(e -> stage.close());

        // Nút Assemble và Done
        Button btnAssemble = new Button("Assemble");
        Button btnDone = new Button("Done");
        
        HBox buttonBox = new HBox(10, btnAssemble, btnDone);
        buttonBox.setStyle("-fx-padding: 6px; -fx-alignment: center-right;");

        btnDone.setOnAction(e -> {
            // Lưu lại vào chính file gốc
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write(textArea.getText());
            } catch (IOException ex) {
                new Alert(Alert.AlertType.ERROR, "Lỗi lưu file!").showAndWait();
            }
            stage.close();
        });

        btnAssemble.setOnAction(e -> {
            try {
                // Lưu file trước khi assemble
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                    bw.write(textArea.getText());
                } catch (IOException ex) {
                    new Alert(Alert.AlertType.ERROR, "Lỗi lưu file!").showAndWait();
                    return;
                }
                
                // Thực hiện assemble
                if (assembleCallback != null) {
                    // Sử dụng SimulationManager để assemble
                    SimulationManager simManager = SimulationManager.getInstance();
                    
                    // Đọc nội dung từ textarea và split thành lines
                    String content = textArea.getText();
                    List<String> lines = Arrays.asList(content.split("\n"));
                    
                    // Thực hiện assemble thông qua SimulationManager
                    if (simManager.assembleCode(lines)) {
                        // Gọi callback với kết quả
                        assembleCallback.accept(simManager.getMachineCode(), simManager.getAssemblyLines());
                        
                        // Hiển thị thông báo thành công
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Assembly Success");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("Assembly completed successfully!\n" + 
                                                   "Generated " + simManager.getMachineCode().length + " machine code instructions.\n" +
                                                   "Status: " + simManager.getStatusString());
                        successAlert.showAndWait();
                        
                    } else {
                        // Hiển thị lỗi assembly
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Assembly Error");
                        errorAlert.setHeaderText("Failed to assemble code");
                        errorAlert.setContentText("Please check your assembly code for syntax errors.");
                        errorAlert.showAndWait();
                    }
                } else {
                    // Fallback - chỉ hiển thị thông báo
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Assemble completed!");
                    alert.showAndWait();
                }
                
            } catch (Exception ex) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Unexpected error occurred");
                errorAlert.setContentText(ex.getMessage());
                errorAlert.showAndWait();
            }
        });

        root.setTop(menuBar);
        root.setCenter(textArea);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);
        stage.show();
    }

    public static void fileToTable(File file, TableView<String> table) {
        table.getItems().clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                table.getItems().add(line);
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Lỗi đọc file!").showAndWait();
        }
    }
}