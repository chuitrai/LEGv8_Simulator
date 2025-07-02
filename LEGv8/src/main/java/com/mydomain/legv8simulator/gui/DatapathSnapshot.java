package main.java.com.mydomain.legv8simulator.gui;

import main.java.com.mydomain.legv8simulator.simulator.control.*;
import main.java.com.mydomain.legv8simulator.instruction.Instruction;
import main.java.com.mydomain.legv8simulator.simulator.pipeline.*;

/**
 * DTO bất biến chứa "ảnh chụp" đầy đủ của Datapath.
 * Nó được tạo bởi Simulator và gửi cho GUI để trực quan hóa.
 */
public final class DatapathSnapshot {

    // --- Dữ liệu trong các Latch ---
    public final IF_ID_Latch if_id_latch;
    public final ID_EX_Latch id_ex_latch;
    public final EX_MEM_Latch ex_mem_latch;
    public final MEM_WB_Latch mem_wb_latch;
    
    // --- Các giá trị tính toán thêm cho tiện lợi ---
    public final ControlSignals activeControlSignals;
    public final long writeBackData;

    /**
     * Constructor chính, nhận vào tất cả các latch của pipeline.
     */
    public DatapathSnapshot(IF_ID_Latch if_id, ID_EX_Latch id_ex, EX_MEM_Latch ex_mem, MEM_WB_Latch mem_wb) {
        this.if_id_latch = if_id;
        this.id_ex_latch = id_ex;
        this.ex_mem_latch = ex_mem;
        this.mem_wb_latch = mem_wb;

        // Trích xuất tín hiệu điều khiển của lệnh đang ở giai đoạn cuối cùng có thể
        if (mem_wb != null) {
            this.activeControlSignals = mem_wb.controlSignals;
        } else if (ex_mem != null) {
            this.activeControlSignals = ex_mem.controlSignals;
        } else if (id_ex != null) {
            this.activeControlSignals = id_ex.controlSignals;
        } else {
            this.activeControlSignals = new ControlSignals.Builder().build(); // NOP
        }
        
        // Tính toán dữ liệu ghi lại cuối cùng
        long wbData = 0;
        if (mem_wb != null && mem_wb.controlSignals.regWrite) {
            wbData = mem_wb.controlSignals.memToReg ? 
                     mem_wb.dataReadFromMemory : 
                     mem_wb.aluResult;
        }
        this.writeBackData = wbData;
    }
}