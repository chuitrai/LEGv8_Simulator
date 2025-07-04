package main.java.com.mydomain.legv8simulator.UI;

import java.util.List;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import main.java.com.mydomain.legv8simulator.core.SimulationManager;
import main.java.com.mydomain.legv8simulator.utils.BitUtils;

public class InstructionMemoryWindow {

    // Sử dụng SimulationManager để lấy dữ liệu
    private SimulationManager simManager;
    private boolean showBinary = false;
    public static class Instruction {
    
        private String addr;
        private String instruction;
        private String comment;


        public Instruction(String addr, String instruction, String comment) {
            this.addr = addr;
            this.instruction = instruction;
            this.comment = comment;
        }

        public String getAddr() { return addr; }
        public String getInstruction() { return instruction; }
        public String getComment() { return comment; }
    }

    private TableView<Instruction> instrTable = new TableView<>();

    @SuppressWarnings({ "deprecation", "unchecked" })
    public InstructionMemoryWindow() {
        TableColumn<Instruction, String> addrCol = new TableColumn<>("Addr");
        addrCol.setCellValueFactory(new PropertyValueFactory<>("addr"));
        TableColumn<Instruction, String> instrCol = new TableColumn<>("Instruction");
        instrCol.setCellValueFactory(new PropertyValueFactory<>("instruction"));
        TableColumn<Instruction, String> commentCol = new TableColumn<>("Comment");
        commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
        instrTable.getColumns().addAll(addrCol, instrCol, commentCol);
        instrTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void addInstruction(String addr, String instr, String comment) {
        instrTable.getItems().add(new Instruction(addr, instr, comment));
    }

    private void loadInstructions() {

        instrTable.getItems().clear();

        List<String> lines = simManager.getAssemblyLines();
        int[] machineCode = simManager.getMachineCode();
        
        if (lines == null || lines.isEmpty()) {
            return;
        }
        
        int addr = 0;
        for (String line : lines) {
            if (!line.trim().isEmpty() && !line.trim().startsWith("#")) {
                String instruction;
                if (showBinary && machineCode != null && addr/4 < machineCode.length) {
                    // Hiển thị mã máy dưới dạng binary
                    instruction = String.format("%32s", BitUtils.toBinaryString32(machineCode[addr / 4])).replace(' ', '0');
                } else {
                    // Hiển thị assembly code
                    instruction = line.trim();
                }
                
                addInstruction(
                    String.format("0x%08X", addr),
                    instruction,
                    ""
                );
                addr += 4;
            }
        }
    }

    public void show() {
        simManager = SimulationManager.getInstance();
        
        // Tiêu đề vàng
        Label title = new Label("Instruction Memory");
        title.setStyle("-fx-background-color: orange; -fx-font-weight: bold; -fx-font-size: 12px;");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(javafx.geometry.Pos.CENTER);

        // Thanh trạng thái vàng hiển thị comment
        Label status = new Label();
        status.setStyle("-fx-background-color: yellow; -fx-font-size: 12px; -fx-alignment: center;");
        status.setMaxWidth(Double.MAX_VALUE);
        status.setMinHeight(20);

        instrTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) status.setText(newVal.getComment());
            else status.setText("");
        });

        // Nút Machine Code và Done
        Button btnMachine = new Button("Machine Code");
        Button btnDone = new Button("Done");
        HBox buttonBox = new HBox(10, btnMachine, btnDone);
        buttonBox.setStyle("-fx-alignment: center; -fx-padding: 6px;");
        
        // Load instructions ban đầu (assembly code)
        loadInstructions();
        
        // Event handler cho nút Machine Code
        btnMachine.setOnAction(e -> {
            showBinary = !showBinary;
            if (showBinary) {
                btnMachine.setText("String Code");
            } else {
                btnMachine.setText("Machine Code");
            }
            // Reload instructions với format mới
            loadInstructions();
        });

        btnDone.setOnAction(e -> ((Stage)btnDone.getScene().getWindow()).close());

        VBox root = new VBox(title, instrTable, status, buttonBox);
        VBox.setVgrow(instrTable, Priority.ALWAYS);

        Stage stage = new Stage();
        stage.setTitle("Instruction Memory");
        stage.setScene(new Scene(root, 350, 250));
        stage.show();

    }
    public TableView<Instruction> getTable() {
        return instrTable;
    }
} 
