// src/main/java/com/yourdomain/legv8simulator/instruction/Instruction.java
package main.java.com.mydomain.legv8simulator.instruction;

/**
 * Base interface for all LEGv8 instructions.
 * Provides a common way to access the original machine code and its type.
 */
public interface Instruction {
    /**
     * Gets the original 32-bit machine code of the instruction.
     * @return The machine code as an integer.
     */
    int getMachineCode();

    /**
     * Gets the format type of the instruction (R, I, D, B, CB, IM).
     * @return The InstructionFormat enum value.
     */
    InstructionFormat getFormat();

    /**
     * Gets the opcode mnemonic for this instruction (e.g., "ADD", "LDUR").
     * This might be determined during decoding or execution.
     * @return The opcode mnemonic string.
     */
    String getOpcodeMnemonic();

    // You might add more common methods here later, e.g.,
    // String toAssemblyString(); // To convert back to assembly representation
}