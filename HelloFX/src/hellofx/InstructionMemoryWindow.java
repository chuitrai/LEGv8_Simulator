package hellofx;

import hellofx.InstructionMemoryWindow.Instruction;
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

public class InstructionMemoryWindow {
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

    public InstructionMemoryWindow() {
        TableColumn<Instruction, String> waddrCol = new TableColumn<>("WordAddr");
        waddrCol.setCellValueFactory(new PropertyValueFactory<>("waddr"));
        TableColumn<Instruction, String> addrCol = new TableColumn<>("Addr");
        addrCol.setCellValueFactory(new PropertyValueFactory<>("addr"));
        TableColumn<Instruction, String> instrCol = new TableColumn<>("Instruction");
        instrCol.setCellValueFactory(new PropertyValueFactory<>("instruction"));
        TableColumn<Instruction, String> commentCol = new TableColumn<>("Comment");
        commentCol.setCellValueFactory(new PropertyValueFactory<>("comment"));
        instrTable.getColumns().addAll(waddrCol, addrCol, instrCol, commentCol);
        instrTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public void addInstruction(String addr, String instr, String comment) {
        instrTable.getItems().add(new Instruction(addr, instr, comment));
    }

    public void show() {
         // Tiêu đề vàng
        Label title = new Label("Instruction Memory");
        title.setStyle("-fx-background-color: orange; -fx-font-weight: bold; -fx-font-size: 12px;");
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(javafx.geometry.Pos.CENTER);

        // Thanh trạng thái vàng hiển thị comment
        Label status = new Label();
        status.setStyle("-fx-background-color: yellow; -fx-font-size: 12px;");
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

        btnMachine.setOnAction(e -> {
            showBinary = !showBinary;
            if (showBinary) {
                btnMachine.setText("String Code");
                // Giả lập chuyển instruction sang mã nhị phân (ở đây chỉ demo, bạn thay bằng mã thực tế nếu có)
                for (Instruction ins : instrTable.getItems()) {
                    // Ví dụ: chuyển từng ký tự thành mã nhị phân, thực tế bạn nên thay bằng mã hóa thực sự
                    StringBuilder bin = new StringBuilder();
                    for (char c : ins.getInstruction().toCharArray()) {
                        bin.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0')).append(" ");
                    }
                    ins.instruction = bin.toString().trim();
                }
            } else {
                btnMachine.setText("Machine Code");
                // Không có bản gốc thì không chuyển lại được, thực tế bạn nên lưu lại bản gốc để chuyển lại
                // Ở đây chỉ để demo, bạn nên cải tiến thêm!
            }
            instrTable.refresh();
        });

        btnDone.setOnAction(e -> ((Stage)btnDone.getScene().getWindow()).close());

        VBox root = new VBox(title, instrTable, status, buttonBox);
        VBox.setVgrow(instrTable, Priority.ALWAYS);

        Stage stage = new Stage();
        stage.setTitle("Instruction Memory");
        stage.setScene(new Scene(root, 350,250));
        stage.show();
    }

    public TableView<Instruction> getTable() {
        return instrTable;
    }
} 
