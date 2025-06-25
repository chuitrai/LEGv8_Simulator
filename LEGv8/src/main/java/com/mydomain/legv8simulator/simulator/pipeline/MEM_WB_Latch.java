package main.java.com.mydomain.legv8simulator.simulator.pipeline;

import main.java.com.mydomain.legv8simulator.simulator.control.ControlSignals;

/**
 * Latch MEM/WB: Lưu trữ dữ liệu được truyền từ giai đoạn Memory Access
 * đến giai đoạn Register Write-back.
 */
public final class MEM_WB_Latch extends PipelineLatch {
    
    /** [CONTROL] Tín hiệu điều khiển cho giai đoạn WB (chủ yếu là RegWrite và MemToReg). */
    public final ControlSignals controlSignals;

    /** [DATA] Dữ liệu được đọc từ bộ nhớ (chỉ có ý nghĩa trong lệnh Load). */
    public final long dataReadFromMemory;
    
    /** [DATA] Kết quả từ ALU được truyền qua từ latch trước. */
    public final long aluResult;

    /** [INFO] Địa chỉ của thanh ghi đích sẽ nhận kết quả cuối cùng. */
    public final int writeRegisterAddress;

    public MEM_WB_Latch(ControlSignals controlSignals, long dataReadFromMemory, long aluResult, int writeRegisterAddress) {
        this.controlSignals = controlSignals;
        this.dataReadFromMemory = dataReadFromMemory;
        this.aluResult = aluResult;
        this.writeRegisterAddress = writeRegisterAddress;
    }
}