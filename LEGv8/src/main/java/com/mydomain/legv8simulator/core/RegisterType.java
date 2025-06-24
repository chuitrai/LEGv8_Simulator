package main.java.com.mydomain.legv8simulator.core;

public enum RegisterType {
     // Thanh ghi mục đích chung 64-bit (X0-X30, XZR)
    GENERAL_PURPOSE,

    // Thanh ghi Program Counter
    PROGRAM_COUNTER,

    // Thanh ghi cờ trạng thái (PSTATE)
    PROCESSOR_STATE,

    // Thanh ghi Link Register (là một loại thanh ghi mục đích chung)
    LINK_REGISTER,

    // Thanh ghi Stack Pointer (là một loại thanh ghi mục đích chung)
    STACK_POINTER,

    // Thanh ghi Zero (là một loại thanh ghi mục đích chung đặc biệt)
    ZERO_REGISTER
}
