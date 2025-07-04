package main.java.com.mydomain.legv8simulator.simulator.pipeline;

/**
 * Latch IF/ID: Lưu trữ dữ liệu được truyền từ giai đoạn Instruction Fetch
 * đến giai đoạn Instruction Decode.
 */
public final class IF_ID_Latch extends PipelineLatch {
    
    /** Giá trị của PC đã được tăng lên 4, sẵn sàng cho lệnh tiếp theo. */
    public final long pcIncremented;
    
    /** Mã máy 32-bit của lệnh vừa được nạp từ bộ nhớ. */
    public final int instructionMachineCode;

    public IF_ID_Latch(long pcIncremented, int instructionMachineCode) {
        this.pcIncremented = pcIncremented;
        this.instructionMachineCode = instructionMachineCode;
    }

    @Override
    public String toString() {
        return String.format("IF_ID_Latch{pcIncremented=0x%08X, instructionMachineCode=0x%08X}", 
                             pcIncremented, instructionMachineCode
                                );
    }
}