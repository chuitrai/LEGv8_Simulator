package main.java.com.mydomain.legv8simulator.gui;

import main.java.com.mydomain.legv8simulator.instruction.*;

/**
 * Lớp này đóng vai trò như một "chốt" (latch) trong pipeline,
 * lưu trữ trạng thái của một chu trình lệnh khi nó đi qua các giai đoạn
 * Fetch, Decode, và Execute.
 */
public class CycleState {
    private long pcAtFetch;          // PC tại thời điểm fetch
    private int fetchedMachineCode;  // Mã máy đã được nạp
    private Instruction decodedInstruction; // Lệnh đã được giải mã

    public CycleState() {
        reset();
    }

    public void reset() {
        pcAtFetch = 0;
        fetchedMachineCode = 0; // Thường là mã lệnh NOP
        decodedInstruction = null;
    }

    // --- Getters and Setters ---
    public long getPcAtFetch() {
        return pcAtFetch;
    }

    public void setPcAtFetch(long pcAtFetch) {
        this.pcAtFetch = pcAtFetch;
    }

    public int getFetchedMachineCode() {
        return fetchedMachineCode;
    }

    public void setFetchedMachineCode(int fetchedMachineCode) {
        this.fetchedMachineCode = fetchedMachineCode;
    }

    public Instruction getDecodedInstruction() {
        return decodedInstruction;
    }

    public void setDecodedInstruction(Instruction decodedInstruction) {
        this.decodedInstruction = decodedInstruction;
    }
}