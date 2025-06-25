package main.java.com.mydomain.legv8simulator.gui;

// Import các lớp latch của bạn
import main.java.com.mydomain.legv8simulator.simulator.pipeline.*; 

/**
 * DTO chứa "ảnh chụp" của các latch trong pipeline tại một thời điểm.
 * Phiên bản này dùng để chạy demo trên console.
 */
public final class DatapathSnapshot_demo {
    public final IF_ID_Latch if_id;
    public final ID_EX_Latch id_ex;
    public final EX_MEM_Latch ex_mem;
    public final MEM_WB_Latch mem_wb;

    public DatapathSnapshot_demo(IF_ID_Latch if_id, ID_EX_Latch id_ex, EX_MEM_Latch ex_mem, MEM_WB_Latch mem_wb) {
        this.if_id = if_id;
        this.id_ex = id_ex;
        this.ex_mem = ex_mem;
        this.mem_wb = mem_wb;
    }
}