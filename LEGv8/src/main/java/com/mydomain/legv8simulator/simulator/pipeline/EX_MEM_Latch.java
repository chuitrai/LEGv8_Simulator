package main.java.com.mydomain.legv8simulator.simulator.pipeline;

import main.java.com.mydomain.legv8simulator.simulator.control.ControlSignals;

/**
 * Latch EX/MEM: Lưu trữ dữ liệu được truyền từ giai đoạn Execute
 * đến giai đoạn Memory Access.
 */
public final class EX_MEM_Latch extends PipelineLatch {

    /** [CONTROL] Tín hiệu điều khiển cho giai đoạn MEM và WB. */
    public final ControlSignals controlSignals;

    /** [DATA] Kết quả từ ALU (một giá trị tính toán hoặc một địa chỉ bộ nhớ). */
    public final long aluResult;
    
    /** [DATA] Dữ liệu cần ghi vào bộ nhớ (giá trị của thanh ghi Rt trong lệnh Store). */
    public final long dataToWriteToMemory;

    /** [INFO] Địa chỉ của thanh ghi đích (Rd hoặc Rt) sẽ nhận kết quả cuối cùng. */
    public final int writeRegisterAddress;

    public EX_MEM_Latch(ControlSignals controlSignals, long aluResult, long dataToWriteToMemory, int writeRegisterAddress) {
        this.controlSignals = controlSignals;
        this.aluResult = aluResult;
        this.dataToWriteToMemory = dataToWriteToMemory;
        this.writeRegisterAddress = writeRegisterAddress;
    }
}