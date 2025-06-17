```markdown
# LEGv8 Instruction Set Summary (Basic Subset for Simulator)

This document summarizes the basic subset of LEGv8 instructions supported by this simulator.

## Instruction Formats

The simulator primarily supports the following LEGv8 instruction formats:

*   **R-format:** `opcode (11) | Rm (5) | shamt (6) | Rn (5) | Rd (5)`
*   **I-format:** `opcode (10) | immediate (12) | Rn (5) | Rd (5)`
*   **D-format:** `opcode (11) | address (9) | op2 (2) | Rn (5) | Rt (5)`
*   **B-format:** `opcode (6) | address (26)`
*   **CB-format:** `opcode (8) | address (19) | Rt (5)`
*   **IM-format:** `opcode (9) | hw (2) | immediate (16) | Rd (5)`

---

## I. Arithmetic Instructions (R-format & I-format)

These instructions perform arithmetic operations on register values or an immediate value.

| Mnemonic  | Format   | Operands             | Operation                                     | Description                                                                 | Flags Affected |
| :-------- | :------- | :------------------- | :-------------------------------------------- | :-------------------------------------------------------------------------- | :------------- |
| **ADD**   | R        | `Rd, Rn, Rm`         | `Rd = Rn + Rm`                                | Add                                                                         | N,Z,C,V (No)   |
| **ADDS**  | R        | `Rd, Rn, Rm`         | `Rd = Rn + Rm`                                | Add and Set Flags                                                           | N,Z,C,V (Yes)  |
| **ADDI**  | I        | `Rd, Rn, #imm12`     | `Rd = Rn + imm12`                             | Add Immediate                                                               | N,Z,C,V (No)   |
| **ADDIS** | I        | `Rd, Rn, #imm12`     | `Rd = Rn + imm12`                             | Add Immediate and Set Flags                                                 | N,Z,C,V (Yes)  |
| **SUB**   | R        | `Rd, Rn, Rm`         | `Rd = Rn - Rm`                                | Subtract                                                                    | N,Z,C,V (No)   |
| **SUBS**  | R        | `Rd, Rn, Rm`         | `Rd = Rn - Rm`                                | Subtract and Set Flags (Used for `CMP Rd, Rn` by setting `Rd` to `XZR`) | N,Z,C,V (Yes)  |
| **SUBI**  | I        | `Rd, Rn, #imm12`     | `Rd = Rn - imm12`                             | Subtract Immediate                                                          | N,Z,C,V (No)   |
| **SUBIS** | I        | `Rd, Rn, #imm12`     | `Rd = Rn - imm12`                             | Subtract Immediate and Set Flags                                            | N,Z,C,V (Yes)  |
| **MUL**   | R        | `Rd, Rn, Rm`         | `Rd = Rn * Rm` (lower 64 bits)                | Multiply (Note: LEGv8 has `SMULL`/`UMULL` for 128-bit, this is simplified) | N,Z,C,V (No)   |

---

## II. Logical Instructions (R-format & I-format)

These instructions perform bitwise logical operations.

| Mnemonic | Format | Operands             | Operation                          | Description         | Flags Affected |
| :------- | :----- | :------------------- | :--------------------------------- | :------------------ | :------------- |
| **AND**  | R      | `Rd, Rn, Rm`         | `Rd = Rn AND Rm`                   | Bitwise AND         | N,Z,C,V (No)   |
| **ANDS** | R      | `Rd, Rn, Rm`         | `Rd = Rn AND Rm`                   | Bitwise AND and Set Flags | N,Z (Yes)      |
| **ANDI** | I      | `Rd, Rn, #imm12`     | `Rd = Rn AND imm12`                | Bitwise AND Immediate | N,Z (Yes)      |
| **ORR**  | R      | `Rd, Rn, Rm`         | `Rd = Rn OR Rm`                    | Bitwise OR (Inclusive OR) | N,Z,C,V (No)   |
| **ORRI** | I      | `Rd, Rn, #imm12`     | `Rd = Rn OR imm12`                 | Bitwise OR Immediate | N,Z,C,V (No)   |
| **EOR**  | R      | `Rd, Rn, Rm`         | `Rd = Rn XOR Rm`                   | Bitwise Exclusive OR | N,Z,C,V (No)   |
| **EORI** | I      | `Rd, Rn, #imm12`     | `Rd = Rn XOR imm12`                | Bitwise Exclusive OR Immediate | N,Z,C,V (No)   |
| **LSL**  | R      | `Rd, Rn, #shamt`     | `Rd = Rn << shamt`                 | Logical Shift Left  | N,Z,C,V (No)   |
| **LSR**  | R      | `Rd, Rn, #shamt`     | `Rd = Rn >> shamt` (logical)       | Logical Shift Right | N,Z,C,V (No)   |
| **ASR**  | R      | `Rd, Rn, #shamt`     | `Rd = Rn >> shamt` (arithmetic)    | Arithmetic Shift Right | N,Z,C,V (No)   |

*Note: For LSL, LSR, ASR, `Rm` field in R-format is used for `shamt`.*

---

## III. Data Transfer Instructions (D-format)

These instructions move data between registers and memory. LEGv8 is byte-addressable.

| Mnemonic   | Format | Operands             | Operation                                   | Description                                                                                                |
| :--------- | :----- | :------------------- | :------------------------------------------ | :--------------------------------------------------------------------------------------------------------- |
| **LDUR**   | D      | `Rt, [Rn, #offset]`  | `Rt = Memory[Rn + offset]`                  | Load Doubleword (64-bit) from memory. `offset` is a 9-bit signed immediate, scaled by 1 (unscaled offset).  |
| **STUR**   | D      | `Rt, [Rn, #offset]`  | `Memory[Rn + offset] = Rt`                  | Store Doubleword (64-bit) to memory. `offset` is a 9-bit signed immediate, scaled by 1 (unscaled offset).    |
| **LDURSW** | D      | `Rt, [Rn, #offset]`  | `Rt = SignExtend(Memory[Rn + offset])`      | Load Word (32-bit) from memory, sign-extend to 64-bit.                                                     |
| **STURW**  | D      | `Rt, [Rn, #offset]`  | `Memory[Rn + offset] = Rt[31:0]`            | Store Word (lower 32-bit of Rt) to memory.                                                                 |
| **LDURH**  | D      | `Rt, [Rn, #offset]`  | `Rt = ZeroExtend(Memory[Rn + offset])`      | Load Halfword (16-bit) from memory, zero-extend to 64-bit.                                                 |
| **STURH**  | D      | `Rt, [Rn, #offset]`  | `Memory[Rn + offset] = Rt[15:0]`            | Store Halfword (lower 16-bit of Rt) to memory.                                                             |
| **LDURB**  | D      | `Rt, [Rn, #offset]`  | `Rt = ZeroExtend(Memory[Rn + offset])`      | Load Byte (8-bit) from memory, zero-extend to 64-bit.                                                      |
| **STURB**  | D      | `Rt, [Rn, #offset]`  | `Memory[Rn + offset] = Rt[7:0]`             | Store Byte (lower 8-bit of Rt) to memory.                                                                  |

---

## IV. Conditional Branch Instructions (CB-format & B-format with condition)

These instructions change the flow of control based on a condition. `address` is PC-relative.

| Mnemonic | Format | Operands        | Operation                                      | Description                                                              |
| :------- | :----- | :-------------- | :--------------------------------------------- | :----------------------------------------------------------------------- |
| **CBZ**  | CB     | `Rt, label`     | `if (Rt == 0) branch to label`                 | Compare and Branch if Zero. `label` is a 19-bit signed offset from PC.   |
| **CBNZ** | CB     | `Rt, label`     | `if (Rt != 0) branch to label`                 | Compare and Branch if Not Zero. `label` is a 19-bit signed offset from PC. |
| **B.cond** | B (variant) | `label`   | `if (condition true) branch to label`          | Branch on condition. `label` is a 19-bit signed offset from PC (uses a CB-like encoding for this subset). |

**Supported Conditions for `B.cond`:**

*   `EQ`: Equal (Z=1)
*   `NE`: Not Equal (Z=0)
*   `HS` / `CS`: Higher or Same / Carry Set (C=1) (unsigned >=)
*   `LO` / `CC`: Lower / Carry Clear (C=0) (unsigned <)
*   `MI`: Minus / Negative (N=1)
*   `PL`: Plus / Positive or zero (N=0)
*   `VS`: Overflow Set (V=1)
*   `VC`: Overflow Clear (V=0)
*   `HI`: Higher (C=1 AND Z=0) (unsigned >)
*   `LS`: Lower or Same (C=0 OR Z=1) (unsigned <=)
*   `GE`: Greater or Equal (N==V) (signed >=)
*   `LT`: Less Than (N!=V) (signed <)
*   `GT`: Greater Than (Z=0 AND N==V) (signed >)
*   `LE`: Less or Equal (Z=1 OR N!=V) (signed <=)

---

## V. Unconditional Branch Instructions (B-format)

These instructions change the flow of control unconditionally.

| Mnemonic | Format | Operands        | Operation                      | Description                                                              |
| :------- | :----- | :-------------- | :----------------------------- | :----------------------------------------------------------------------- |
| **B**    | B      | `label`         | `branch to label`              | Branch unconditionally. `label` is a 26-bit signed offset from PC.        |
| **BL**   | B      | `label`         | `LR = PC+4; branch to label`   | Branch and Link. Saves return address to `LR` (X30). `label` is a 26-bit signed offset. |
| **BR**   | R      | `Rn`            | `PC = Rn`                      | Branch to Register. (Uses R-format: `opcode | XZR | 0 | Rn | XZR`)   |

*Note: `BR LR` is commonly used to return from a subroutine.*

---

## VI. Move Wide Instructions (IM-format)

Used for loading large immediate values into a register.

| Mnemonic | Format | Operands                  | Operation                                     | Description                                                                |
| :------- | :----- | :------------------------ | :-------------------------------------------- | :------------------------------------------------------------------------- |
| **MOVZ** | IM     | `Rd, #imm16, LSL #shift`  | `Rd = (imm16 << shift)` (zeros other bits)  | Move Wide with Zero. `shift` can be 0, 16, 32, 48. `imm16` is a 16-bit immediate. |
| **MOVK** | IM     | `Rd, #imm16, LSL #shift`  | `Rd[N+15:N] = imm16` (keeps other bits)   | Move Wide with Keep. `shift` can be 0, 16, 32, 48. `imm16` is a 16-bit immediate. N is the shift amount. |

## VII. System and Pseudo-Instructions

| Mnemonic | Type   | Operands             | Equivalent / Operation                 | Description                                        |
| :------- | :----- | :------------------- | :------------------------------------- | :------------------------------------------------- |
| **HALT** | System |                      | (Special simulator instruction)        | Stops the simulation. (Not a standard LEGv8 instruction, but useful for simulators) |

```