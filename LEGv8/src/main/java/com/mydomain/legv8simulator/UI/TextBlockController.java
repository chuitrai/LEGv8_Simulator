package main.java.com.mydomain.legv8simulator.UI;
import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.com.mydomain.legv8simulator.UI.datapath.LEGv8Datapath;
import static main.java.com.mydomain.legv8simulator.UI.datapath.DatapathGraphicsFX.*;
import main.java.com.mydomain.legv8simulator.core.SimulationManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import main.java.com.mydomain.legv8simulator.UI.MovingTextBlock.*;


// Text Block Controller
public class TextBlockController extends StackPane{
    private LEGv8Datapath datapath;
    private List<MovingTextBlock> activeBlocks;
    public static SimulationManager simManager;

    
    public TextBlockController(LEGv8Datapath datapath) {
        this.datapath = datapath;
        this.activeBlocks = new ArrayList<>();
    }
    
    // Trong file TextBlockController.java

    public int getActiveBlockCount() {
        return activeBlocks.size();
    }
    public void simulateFetch() {
        simManager = SimulationManager.getInstance();
        String adrr = String.format("0x%04X", simManager.getCpu().getPC().getValue());
        MovingTextBlock block1 = new MovingTextBlock(adrr, "#3498db");
        MovingTextBlock block2 = new MovingTextBlock(adrr, "#2ecc71");

        // Định nghĩa path: PC -> Instruction Memory -> Control Unit
        List<PathSegment> fetchPath1 = new ArrayList<>();
        fetchPath1.add(new PathSegment(C1_PC_IM + pcWidth, R_MAIN_PATH, instrMemX, R_MAIN_PATH, 1.0));  // PC to Instruction Memory
        block1.setPath(fetchPath1);

        addAndStartBlock(block1);

        // Định nghĩa path: PC -> Instruction Memory -> Control Unit
        List<PathSegment> fetchPath2 = new ArrayList<>();
        fetchPath2.add(new PathSegment(C1_PC_IM + pcWidth, R_MAIN_PATH, C1_PC_IM + pcWidth*1.5, R_MAIN_PATH, 1.0));  // PC to Instruction Memory
        fetchPath2.add(new PathSegment(C1_PC_IM + pcWidth*1.5, R_MAIN_PATH, C1_PC_IM + pcWidth*1.5, add4Y + add4Height*0.25, 1.0));   // To Control Unit
        fetchPath2.add(new PathSegment(C1_PC_IM + pcWidth*1.5, add4Y + add4Height*0.25, add4X, add4Y + add4Height*0.25, 1.0));  // Instruction Memory to decode
        block2.setPath(fetchPath2);
        addAndStartBlock(block2);
        simManager.getSimulator().step(1);

        String newAddr = String.format("0x%04X", simManager.getCpu().getPC().getValue() + 4);
        MovingTextBlock block3 = new MovingTextBlock(newAddr);
        List<PathSegment> fetchPath3 = new ArrayList<>();
        fetchPath3.add(new PathSegment(add4X + add4Width, add4Y + 0.5*add4Height, muxPcSourceX, add4Y + 0.5*add4Height, 1.0));  // PC to Instruction Memory
        block3.setPath(fetchPath3);

        addAndStartBlock(block3);

    }
    
    public void simulateDecode() {
        MovingTextBlock opcode = new MovingTextBlock("ADD", "#e74c3c");
        MovingTextBlock reg1 = new MovingTextBlock("X1", "#f39c12");
        MovingTextBlock reg2 = new MovingTextBlock("X2", "#f39c12");
        
        // Opcode path: Control Unit -> ALU Control
        List<PathSegment> opcodePath = new ArrayList<>();
        opcodePath.add(new PathSegment(470, 90, 470, 150, 0.8));
        opcodePath.add(new PathSegment(470, 150, 600, 200, 1.0));
        opcode.setPath(opcodePath);
        
        // Register paths: Instruction -> Register File
        List<PathSegment> regPath1 = new ArrayList<>();
        regPath1.add(new PathSegment(260, 130, 220, 300, 1.0));
        regPath1.add(new PathSegment(220, 300, 220, 350, 0.5));
        reg1.setPath(regPath1);
        
        List<PathSegment> regPath2 = new ArrayList<>();
        regPath2.add(new PathSegment(260, 130, 220, 300, 1.2));
        regPath2.add(new PathSegment(220, 300, 220, 380, 0.5));
        reg2.setPath(regPath2);
        
        addAndStartBlock(opcode);
        addAndStartBlock(reg1);
        addAndStartBlock(reg2);
    }
    
    public void simulateExecute() {
        MovingTextBlock data1 = new MovingTextBlock("0x1234", "#27ae60");
        MovingTextBlock data2 = new MovingTextBlock("0x5678", "#27ae60");
        
        // Data paths: Register File -> MUX -> ALU
        List<PathSegment> dataPath1 = new ArrayList<>();
        dataPath1.add(new PathSegment(290, 350, 350, 230, 1.0));  // Register to MUX
        dataPath1.add(new PathSegment(350, 230, 600, 310, 1.0));  // MUX to ALU
        data1.setPath(dataPath1);
        
        List<PathSegment> dataPath2 = new ArrayList<>();
        dataPath2.add(new PathSegment(290, 380, 350, 230, 1.2));  // Register to MUX
        dataPath2.add(new PathSegment(350, 230, 600, 330, 1.0));  // MUX to ALU
        data2.setPath(dataPath2);
        
        addAndStartBlock(data1);
        addAndStartBlock(data2);
        
        // Result xuất hiện sau khi inputs đến ALU
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(2.5), e -> {
            MovingTextBlock result = new MovingTextBlock("0x68AC", "#27ae60");
            List<PathSegment> resultPath = new ArrayList<>();
            resultPath.add(new PathSegment(650, 320, 750, 300, 1.0));  // ALU to output
            result.setPath(resultPath);
            addAndStartBlock(result);
        }));
        delay.play();
    }
    
    public void simulateMemoryAccess() {
        MovingTextBlock address = new MovingTextBlock("0x1000", "#9b59b6");
        MovingTextBlock data = new MovingTextBlock("Data", "#9b59b6");
        
        // Address path: ALU -> Data Memory
        List<PathSegment> addressPath = new ArrayList<>();
        addressPath.add(new PathSegment(700, 320, 750, 300, 1.0));
        addressPath.add(new PathSegment(750, 300, 820, 290, 0.8));
        address.setPath(addressPath);
        
        // Data path: Memory -> Register File (writeback)
        List<PathSegment> dataPath = new ArrayList<>();
        dataPath.add(new PathSegment(820, 320, 820, 400, 1.0));
        dataPath.add(new PathSegment(820, 400, 500, 400, 1.5));
        dataPath.add(new PathSegment(500, 400, 220, 400, 1.0));
        data.setPath(dataPath);
        
        addAndStartBlock(address);
        
        // Data xuất hiện sau khi address đến Memory
        Timeline delay = new Timeline(new KeyFrame(Duration.seconds(1.8), e -> {
            addAndStartBlock(data);
        }));
        delay.play();
    }
    
    public void simulateWriteback() {
        MovingTextBlock result = new MovingTextBlock("Final Result", "#f39c12");
        
        // Writeback path: Memory/ALU -> Register File
        List<PathSegment> writebackPath = new ArrayList<>();
        writebackPath.add(new PathSegment(800, 320, 600, 350, 1.0));
        writebackPath.add(new PathSegment(600, 350, 500, 380, 1.0));
        writebackPath.add(new PathSegment(500, 380, 290, 380, 1.5));
        result.setPath(writebackPath);
        
        addAndStartBlock(result);
    }
    
    public void simulatePipeline() {
        clearAllBlocks();
        
        // Simulate pipeline với nhiều instructions
        String[] instructions = {"ADD X1,X2,X3", "SUB X4,X5,X6", "LD X7,[X8]", "ST X9,[X10]"};
        String[] colors = {"#3498db", "#e74c3c", "#27ae60", "#9b59b6"};
        
        for (int i = 0; i < instructions.length; i++) {
            final int index = i;
            Timeline delay = new Timeline(new KeyFrame(Duration.seconds(i * 0.8), e -> {
                MovingTextBlock inst = new MovingTextBlock(instructions[index], colors[index]);
                
                // Pipeline path: PC -> IM -> Decode -> Execute -> Memory -> Writeback
                List<PathSegment> pipelinePath = new ArrayList<>();
                pipelinePath.add(new PathSegment(100, 130 + index * 30, 200, 130 + index * 30, 1.0));
                pipelinePath.add(new PathSegment(200, 130 + index * 30, 350, 200 + index * 20, 1.0));
                pipelinePath.add(new PathSegment(350, 200 + index * 20, 600, 280 + index * 15, 1.0));
                pipelinePath.add(new PathSegment(600, 280 + index * 15, 750, 280 + index * 15, 1.0));
                pipelinePath.add(new PathSegment(750, 280 + index * 15, 220, 350 + index * 10, 1.5));
                
                inst.setPath(pipelinePath);
                addAndStartBlock(inst);
            }));
            delay.play();
        }
    }
    
    public void simulateComplexPath() {
        MovingTextBlock block = new MovingTextBlock("Complex Path", "#e67e22");
        
        // Đường đi phức tạp qua nhiều components
        List<PathSegment> complexPath = new ArrayList<>();
        complexPath.add(new PathSegment(100, 130, 200, 130, 1.0));    // PC to IM
        complexPath.add(new PathSegment(200, 130, 470, 90, 1.0));     // IM to Control
        complexPath.add(new PathSegment(470, 90, 470, 200, 0.8));     // Control down
        complexPath.add(new PathSegment(470, 200, 350, 230, 0.8));    // Control to MUX
        complexPath.add(new PathSegment(350, 230, 220, 300, 1.0));    // MUX to Register
        complexPath.add(new PathSegment(220, 300, 220, 380, 0.5));    // Register move
        complexPath.add(new PathSegment(220, 380, 350, 230, 1.0));    // Register to MUX
        complexPath.add(new PathSegment(350, 230, 600, 320, 1.2));    // MUX to ALU
        complexPath.add(new PathSegment(600, 320, 750, 300, 1.0));    // ALU to Memory
        complexPath.add(new PathSegment(750, 300, 820, 290, 0.8));    // Memory access
        complexPath.add(new PathSegment(820, 290, 500, 380, 1.5));    // Memory to MUX
        complexPath.add(new PathSegment(500, 380, 290, 380, 1.0));    // MUX to Register
        
        block.setPath(complexPath);
        addAndStartBlock(block);
    }
    
    private void addAndStartBlock(MovingTextBlock block) {
        datapath.getChildren().add(block);
        activeBlocks.add(block);
        block.startMoving();
    }
    
    private void removeBlock(MovingTextBlock block) {
        block.stopMoving();
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), block);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            datapath.getChildren().remove(block);
            activeBlocks.remove(block);
        });
        fade.play();
    }
    
    public void clearAllBlocks() {
        List<MovingTextBlock> blocksToRemove = new ArrayList<>(activeBlocks);
        for (MovingTextBlock block : blocksToRemove) {
            removeBlock(block);
        }
    }

    // Thêm những phương thức này vào trong lớp TextBlockController

    public void pauseAll() {
        for (MovingTextBlock block : activeBlocks) {
            block.pause();
        }
    }

    public void resumeAll() {
        for (MovingTextBlock block : activeBlocks) {
            block.resume();
        }
    }

    public void setAllRates(double rate) {
        for (MovingTextBlock block : activeBlocks) {
            block.setRate(rate);
        }
    }

    public void stepAll() {
        // Định nghĩa một bước nhảy nhỏ, ví dụ 16ms ~ 1 frame ở 60fps
        Duration stepDuration = Duration.millis(16);
        for (MovingTextBlock block : activeBlocks) {
            block.step(stepDuration);
        }
    }
}