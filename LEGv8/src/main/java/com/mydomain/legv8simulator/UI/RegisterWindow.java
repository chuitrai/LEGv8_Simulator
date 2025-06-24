package main.java.com.mydomain.legv8simulator.UI;

import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.java.com.mydomain.legv8simulator.utils.Constants;
import main.java.com.mydomain.legv8simulator.core.SimulationManager;

public class RegisterWindow {
    // Sử dụng SimulationManager để lấy dữ liệu
    private SimulationManager simManager;
    public static class Register {
        private String reg;
        private String binVal;
        private String decVal;

        public Register(String reg, String binVal, String decVal) {
            this.reg = reg;
            this.binVal = binVal;
            this.decVal = decVal;
        }

        public String getReg() { return reg; }
        public String getBinVal() { return binVal; }
        public String getDecVal() { return decVal; }
    }

    private TableView<Register> regTable = new TableView<>();

    @SuppressWarnings({ "deprecation", "unchecked" })
    public RegisterWindow() {
        TableColumn<Register, String> regCol = new TableColumn<>("Reg");
        regCol.setCellValueFactory(new PropertyValueFactory<>("reg"));
        TableColumn<Register, String> binCol = new TableColumn<>("Bin Val");
        binCol.setCellValueFactory(new PropertyValueFactory<>("binVal"));
        TableColumn<Register, String> decCol = new TableColumn<>("Dec Val");
        decCol.setCellValueFactory(new PropertyValueFactory<>("decVal"));
        regTable.getColumns().addAll(regCol, binCol, decCol);
        regTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void addRegister(String reg, String binVal, String decVal) {
        regTable.getItems().add(new Register(reg, binVal, decVal));
    }

    public void show() {
        VBox root = new VBox(regTable);
        Stage stage = new Stage();
        stage.setTitle("Registers");
        stage.setScene(new Scene(root, 300, 200));
        updateRegisterWindow(); // Cập nhật dữ liệu register
        stage.show();
    }

    private void updateRegisterWindow() {
        // Cập nhật với dữ liệu thực từ CPU registers
        // Giả sử CPU có phương thức getRegister()
        simManager = SimulationManager.getInstance();
        regTable.getItems().clear();
        for (int i = 0; i < Constants.REGISTER_COUNT; i++) {
            String regName = "X" + i;
            String binVal = String.format("%08d", Long.parseLong(Long.toBinaryString(simManager.getCpu().getRegister(i))));
            addRegister(regName, binVal, String.valueOf(simManager.getCpu().getRegister(i)));
        }
    }

    public TableView<Register> getTable() {
        return regTable;
    }
}