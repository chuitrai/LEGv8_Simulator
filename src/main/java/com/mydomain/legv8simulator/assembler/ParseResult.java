package main.java.com.mydomain.legv8simulator.assembler;

import java.util.Optional;

/**
 * Lớp chứa kết quả phân tích cú pháp hoàn chỉnh của một dòng code assembly.
 * Một dòng có thể chứa nhãn, lệnh, hoặc cả hai.
 */
public class ParseResult {
    private final String label;
    private final ParsedInstruction instruction;
    private final int lineNumber;

    public ParseResult(String label, ParsedInstruction instruction, int lineNumber) {
        this.label = label;
        this.instruction = instruction;
        this.lineNumber = lineNumber;
    }

    public Optional<String> getLabel() {
        return Optional.ofNullable(label);
    }

    public Optional<ParsedInstruction> getInstruction() {
        return Optional.ofNullable(instruction);
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
}