package hellofx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import hellofx.datapath.LEGv8Datapath;    



public class SimulatorApp extends Application {

@Override
public void start(Stage primaryStage) {
    // Cửa sổ chính khởi động
   Stage mainStage = new Stage();
    VBox mainRoot = new VBox(20);
    mainRoot.setPadding(new javafx.geometry.Insets(30));
    mainRoot.setAlignment(javafx.geometry.Pos.TOP_CENTER);
    Label titleLabel = new Label("LEGv8 Simulator");
    titleLabel.setId("title-label");
    Button btnStart = new Button("Start");
        btnStart.setId("start-button");

        Button btnLoad = new Button("Select file");
        btnLoad.setId("file-button");

        Label fileLabel = new Label("File has not been selected yet");
        fileLabel.setId("status-label");
    
    // Bố cục lại
    mainRoot.getChildren().addAll(titleLabel, btnStart, btnLoad, fileLabel);
    mainRoot.getStylesheets().add(getClass().getResource("style/style.css").toExternalForm()); // Thêm dòng này

    Scene mainScene = new Scene(mainRoot, 350, 240);
    mainStage.setTitle("LEGv8");
    mainStage.setScene(mainScene);
    mainStage.show();
    TableView<String> fileContentTable = new TableView<>();

    javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
    fileChooser.setTitle("Open Assembly File");
    fileChooser.getExtensionFilters().addAll(
        new javafx.stage.FileChooser.ExtensionFilter("Text Files", "*.asm", "*.txt"),
        new javafx.stage.FileChooser.ExtensionFilter("All Files", "*.*")
    );
    final java.io.File[] selectedFile = {null};
    

    btnLoad.setOnAction(e -> {
        // Kiểm tra nếu đã có file được chọn
        if (selectedFile[0] != null) {
            fileLabel.setText("File already selected: " + selectedFile[0].getName());
            // Hiển thị cửa sổ chỉnh sửa file
            TextFileEditor.show(mainStage, selectedFile[0]);
            return;
        }
        java.io.File file = fileChooser.showOpenDialog(mainStage);
        if (file != null) {
            selectedFile[0] = file;
            fileLabel.setText("Selected: " + file.getName());
            // Hiển thị cửa sổ chỉnh sửa file
            TextFileEditor.show(mainStage, file);
        }
    });



    // Khi nhấn Start, mở các cửa sổ chính và truyền dữ liệu vào Instruction Memory
    btnStart.setOnAction(e -> {
        mainStage.close();

        RegisterWindow regWin = new RegisterWindow();
        regWin.addRegister("$0", "0000", "0");
        regWin.addRegister("$s1", "0001", "5");
        regWin.addRegister("$s2", "0010", "10");
        regWin.show();

        InstructionMemoryWindow instrWin = new InstructionMemoryWindow();
        int addr = 0;
        for (String line : fileContentTable.getItems()) {
            instrWin.addInstruction(String.valueOf(addr), line, "");
            addr += 4;
        }
        instrWin.show();

        LEGv8Datapath datapathPane = new LEGv8Datapath();
        Scene scene = new Scene(datapathPane, 1200, 800);
        primaryStage.setTitle("LEGv8 Datapath Visualization");
        primaryStage.setScene(scene);
        primaryStage.show();
    });
}

    
    public static void main(String[] args) {
        launch(args);
    }
}