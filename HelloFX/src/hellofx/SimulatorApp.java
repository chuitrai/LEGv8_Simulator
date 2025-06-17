package hellofx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import hellofx.datapath.LEGv8Datapath;    



public class SimulatorApp extends Application {
    // Model cho Register
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

    // Model cho Instruction
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

    @Override
    public void start(Stage primaryStage) {
        // Cửa sổ Registers
        TableView<Register> regTable = new TableView<>();
        TableColumn<Register, String> regCol = new TableColumn<>("Reg");
        regCol.setCellValueFactory(new PropertyValueFactory<>("reg"));
        TableColumn<Register, String> binCol = new TableColumn<>("Bin Val");
        binCol.setCellValueFactory(new PropertyValueFactory<>("binVal"));
        TableColumn<Register, String> decCol = new TableColumn<>("Dec Val");
        decCol.setCellValueFactory(new PropertyValueFactory<>("decVal"));
        regTable.getColumns().addAll(regCol, binCol, decCol);
        regTable.getItems().add(new Register("$0", "0000", "0"));
        regTable.getItems().add(new Register("$s1", "0001", "5"));
        regTable.getItems().add(new Register("$s2", "0010", "10"));
        VBox regRoot = new VBox(regTable);

        Stage regStage = new Stage();
        regStage.setTitle("Registers");
        Scene regScene = new Scene(regRoot, 300, 200);
        regStage.setScene(regScene);
        regStage.show();

        // Cửa sổ Instruction Memory
        TableView<Instruction> instrTable = new TableView<>();
        TableColumn<Instruction, String> addrCol = new TableColumn<>("Addr");
        addrCol.setCellValueFactory(new PropertyValueFactory<>("addr"));
        TableColumn<Instruction, String> instrCol = new TableColumn<>("Instruction");
        instrCol.setCellValueFactory(new PropertyValueFactory<>("instruction"));
        TableColumn<Instruction, String> commentCol = new TableColumn<>("Comment");
        commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
        instrTable.getColumns().addAll(addrCol, instrCol, commentCol);
        instrTable.getItems().add(new Instruction("0", "add $s1, $s1, $s1", "#$5+$5"));
        instrTable.getItems().add(new Instruction("4", "add $s4, $s1, $s0", "#$5+10"));
        instrTable.getItems().add(new Instruction("8", "sub $s5, $s1, $s2", "#$5-3"));

        // ...các cột và dữ liệu...
        VBox instrRoot = new VBox(instrTable);

        Stage instrStage = new Stage();
        instrStage.setTitle("Instruction Memory");
        Scene instrScene = new Scene(instrRoot, 400, 200);
        instrStage.setScene(instrScene);
        instrStage.show();

        // Hiển thị cửa sổ LEGv8 Datapath khi chạy SimulatorApp
        LEGv8Datapath datapathPane = new LEGv8Datapath();

        // 2. Đặt component đó vào Scene.
        Scene scene = new Scene(datapathPane, 1200, 800);
        primaryStage.setTitle("LEGv8 Datapath Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    
    public static void main(String[] args) {
        launch(args);
    }
}