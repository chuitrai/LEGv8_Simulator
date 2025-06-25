package hellofx;

import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterWindow {
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

    public RegisterWindow() {
        TableColumn<Register, String> regCol = new TableColumn<>("Reg");
        regCol.setCellValueFactory(new PropertyValueFactory<>("reg"));
        TableColumn<Register, String> binCol = new TableColumn<>("Bin Val");
        binCol.setCellValueFactory(new PropertyValueFactory<>("binVal"));
        TableColumn<Register, String> decCol = new TableColumn<>("Dec Val");
        decCol.setCellValueFactory(new PropertyValueFactory<>("decVal"));
        regTable.getColumns().addAll(regCol, binCol, decCol);
    }

    public void addRegister(String reg, String binVal, String decVal) {
        regTable.getItems().add(new Register(reg, binVal, decVal));
    }

    public void show() {
        VBox root = new VBox(regTable);
        Stage stage = new Stage();
        stage.setTitle("Registers");
        stage.setScene(new Scene(root, 300, 200));
        stage.show();
    }

    public TableView<Register> getTable() {
        return regTable;
    }
}