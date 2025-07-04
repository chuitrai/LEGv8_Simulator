package main.java.com.mydomain.legv8simulator.simulator.pipeline;

import main.java.com.mydomain.legv8simulator.instruction.Instruction;
import main.java.com.mydomain.legv8simulator.simulator.control.ControlSignals;

/**
 * Latch ID/EX: Lưu trữ dữ liệu được truyền từ giai đoạn Decode
 * đến giai đoạn Execute. Đây là latch chứa nhiều thông tin nhất.
 */
public final class ID_EX_Latch extends PipelineLatch {

    /** 
     * [CONTROL] Đối tượng chứa tất cả các tín hiệu điều khiển cho các giai đoạn sau.
     * Được truyền qua các latch tiếp theo.
     */
    public final ControlSignals controlSignals;

    /** 
     * [DATA] Giá trị của PC + 4, cần cho việc tính toán địa chỉ rẽ nhánh tương đối.
     */
    public final long pcIncremented;

    /** 
     * [DATA] Dữ liệu đọc từ cổng đọc 1 của Register File (thường là giá trị của Rn).
     */
    public final long readData1;

    /** 
     * [DATA] Dữ liệu đọc từ cổng đọc 2 của Register File (giá trị của Rm).
     */
    public final long readData2;

    /** 
     * [DATA] Giá trị immediate đã được trích xuất và mở rộng dấu thành 64-bit.
     */
    public final long signExtendedImmediate;

    /** 
     * [INFO] Chỉ số của thanh ghi Rt (được dùng bởi LDUR/STUR).
     * Được truyền đi để sử dụng trong các giai đoạn sau.
     */
    public final int rt;

    /** 
     * [INFO] Chỉ số của thanh ghi Rd (được dùng bởi R-Type/I-Type).
     * Được truyền đi để sử dụng trong các giai đoạn sau.
     */
    public final int rd;

    /**
     * [INFO] Toàn bộ đối tượng lệnh đã được giải mã, hữu ích cho việc debug và lấy các thông tin khác.
     */
    public final Instruction instruction;


    public ID_EX_Latch(ControlSignals controlSignals, long pcIncremented, long readData1,
                       long readData2, long signExtendedImmediate, int rt, int rd, Instruction instruction) {
        this.controlSignals = controlSignals;
        this.pcIncremented = pcIncremented;
        this.readData1 = readData1;
        this.readData2 = readData2;
        this.signExtendedImmediate = signExtendedImmediate;
        this.rt = rt;
        this.rd = rd;
        this.instruction = instruction;
    }

    @Override
    public void print() {
        System.out.println("\nID_EX_Latch{\n" +
                            "  pcIncremented=0x" + Long.toHexString(pcIncremented) + ",\n" +
                            "  readData1=0x" + Long.toHexString(readData1) + ",\n" +
                            "  readData2=0x" + Long.toHexString(readData2) + ",\n" +
                            "  signExtendedImmediate=0x" + Long.toHexString(signExtendedImmediate) + ",\n" +
                            "  rt=" + rt + ",\n" +
                            "  rd=" + rd + ",\n" +
                            "  instruction=" + instruction +
                            "  \ncontrolSignals = \n");
        controlSignals.printSignals();
        System.out.println("}\n"); 
    }
}