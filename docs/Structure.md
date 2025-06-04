```
LEGv8_Simulator/
├── .idea/                     # Cấu hình của IntelliJ IDEA 
├── .gitignore                                
├── README.md                  # Mô tả dự án, hướng dẫn cài đặt, sử dụng
├── docs/                      # Tài liệu dự án
│   ├── architecture.md        # Mô tả kiến trúc phần mềm, các quyết định thiết kế
│   ├── legv8_isa_summary.md   # Tóm tắt tập lệnh LEGv8 được hỗ trợ
│   └── user_manual.md         # Hướng dẫn sử dụng cho người dùng
├── resources/                 # Tài nguyên không phải code
│   ├── assembly_examples/     # Các file assembly LEGv8 mẫu
│   │   ├── simple_add.s
│   │   ├── load_store.s
│   │   └── branch_example.s
│   ├── icons/                 # (Cho GUI sau này) Icons cho các nút, cửa sổ
│   └── config/                # File cấu hình (nếu có)
│       └── simulator_settings.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/yourdomain/legv8simulator/  # Gói (package) gốc của bạn
│   │   │       ├── Main.java                     # Điểm khởi chạy ứng dụng
│   │   │       │
│   │   │       ├── core/                         # Lõi của trình mô phlucyện
│   │   │       │   ├── CPU.java                  # Đại diện cho CPU, điều phối các thành phần
│   │   │       │   ├── ALU.java                  # Đơn vị Số học và Logic
│   │   │       │   ├── RegisterFile.java         # Quản lý các thanh ghi
│   │   │       │   ├── Memory.java               # Quản lý bộ nhớ (Instruction & Data)
│   │   │       │   ├── ControlUnit.java          # Tạo tín hiệu điều khiển
│   │   │       │   ├── PC.java                   # Program Counter
│   │   │       │   ├── Instruction.java          # Biểu diễn một lệnh đã được giải mã
│   │   │       │   └── Datapath.java             # (Tùy chọn, nếu muốn tách riêng) Quản lý luồng dữ liệu
│   │   │       │
│   │   │       ├── instruction/                  # Liên quan đến xử lý lệnh
│   │   │       │   ├── InstructionDecoder.java   # Giải mã lệnh từ mã máy
│   │   │       │   ├── InstructionExecutor.java  # Thực thi các loại lệnh khác nhau
│   │   │       │   ├── Opcode.java               # Enum hoặc class chứa thông tin opcode
│   │   │       │   ├── InstructionFormat.java    # Enum cho các định dạng lệnh (R, I, D, B, CB, IM)
│   │   │       │   └── LegV8Instructions.java    # (Tùy chọn) Interface hoặc abstract class cho các lệnh
│   │   │       │
│   │   │       ├── assembler/                    # Trình hợp dịch (Assembly -> Machine Code)
│   │   │       │   ├── Assembler.java            # Lớp chính của trình hợp dịch
│   │   │       │   ├── Parser.java               # Phân tích cú pháp từng dòng assembly
│   │   │       │   ├── Lexer.java                # (Tùy chọn) Tách dòng assembly thành tokens
│   │   │       │   ├── SymbolTable.java          # Quản lý các nhãn (labels)
│   │   │       │   └── AssemblyException.java    # Exception cho lỗi hợp dịch
│   │   │       │
│   │   │       ├── utils/                        # Các lớp tiện ích
│   │   │       │   ├── BitUtils.java             # Các hàm xử lý bit (sign-extend, zero-extend, etc.)
│   │   │       │   ├── FileUtils.java            # Các hàm đọc/ghi file
│   │   │       │   └── Constants.java            # Các hằng số (kích thước bộ nhớ, số thanh ghi)
│   │   │       │
│   │   │       ├── gui/                          # (Để trống ban đầu, sẽ phát triển sau)
│   │   │       │   ├── MainFrame.java            # Cửa sổ chính của ứng dụng
│   │   │       │   ├── DatapathPanel.java        # Panel hiển thị trực quan CPU
│   │   │       │   ├── CodeEditorPanel.java      # Panel nhập và hiển thị mã assembly
│   │   │       │   ├── RegisterPanel.java        # Panel hiển thị trạng thái thanh ghi
│   │   │       │   ├── MemoryPanel.java          # Panel hiển thị trạng thái bộ nhớ
│   │   │       │   ├── ControlButtonPanel.java   # Panel chứa các nút điều khiển (Run, Step)
│   │   │       │   └── GuiUtils.java             # Các tiện ích liên quan đến GUI
│   │   │       │
│   │   │       └── common/                       # Các lớp/interface dùng chung
│   │   │           ├── Observable.java           # (Nếu dùng Observer pattern để GUI cập nhật)
│   │   │           └── Observer.java
│   │   │
│   │   └── resources/                # (Nếu có tài nguyên dùng trong quá trình build ở main)
│   │
│   └── test/
│       ├── java/
│       │   └── com/yourdomain/legv8simulator/
│       │       ├── core/
│       │       │   ├── CPUTest.java
│       │       │   ├── ALUTest.java
│       │       │   └── MemoryTest.java
│       │       ├── instruction/
│       │       │   ├── InstructionDecoderTest.java
│       │       │   └── InstructionExecutorTest.java
│       │       ├── assembler/
│       │       │   └── AssemblerTest.java
│       │       └── utils/
│       │           └── BitUtilsTest.java
│       │
│       └── resources/                # Tài nguyên cho testing
│           └── assembly_test_cases/
│               ├── test_add.s
│               └── test_ldur_stur.s
└── target/                      # (Nếu dùng Maven/Gradle) Thư mục chứa output của quá trình build
    ├── classes/
    └── LEGv8_Simulator-1.0.jar  # File JAR thực thi
```
**Giải thích chi tiết chức năng các thư mục và file (Tập trung Backend):**

*   **`LEGv8_Simulator/`**: Thư mục gốc của dự án.
    *   **`.idea/`**: Dành cho cấu hình của IntelliJ IDEA. Nếu bạn dùng IDE khác (Eclipse, NetBeans), nó sẽ có thư mục cấu hình tương ứng.
    *   **`.gitignore`**: Quan trọng khi dùng Git. Liệt kê các file và thư mục không muốn đưa lên repository (ví dụ: thư mục `target/`, file cấu hình IDE cá nhân).
    *   **`pom.xml` / `build.gradle`**: File cấu hình cho hệ thống build tự động (Maven hoặc Gradle). Giúp quản lý thư viện phụ thuộc, quá trình biên dịch, đóng gói. *Ban đầu có thể chưa cần ngay nếu dự án nhỏ và không có thư viện ngoài, nhưng nên có cho dự án chuyên nghiệp.*
    *   **`README.md`**: File Markdown mô tả tổng quan về dự án, mục tiêu, cách cài đặt môi trường, cách build và chạy ứng dụng.
    *   **`docs/`**: Chứa các tài liệu liên quan đến thiết kế và sử dụng.
        *   `architecture.md`: Mô tả kiến trúc tổng thể của phần mềm, các lựa chọn thiết kế quan trọng, luồng dữ liệu chính.
        *   `legv8_isa_summary.md`: Liệt kê và mô tả ngắn gọn các lệnh LEGv8 mà trình mô phỏng của bạn hỗ trợ (bao gồm định dạng mã máy nếu có).
        *   `user_manual.md`: Hướng dẫn người dùng cuối cách sử dụng các tính năng của trình mô phỏng.
    *   **`resources/`**: Chứa các file không phải là mã nguồn nhưng cần thiết cho ứng dụng.
        *   `assembly_examples/`: Các file mã assembly LEGv8 mẫu để người dùng có thể nhanh chóng nạp và chạy thử.
        *   `icons/`: (Sẽ dùng khi làm GUI) Chứa các file hình ảnh icon cho các nút bấm, cửa sổ.
        *   `config/`: (Nếu cần) Chứa các file cấu hình, ví dụ như kích thước bộ nhớ mặc định, tốc độ mô phỏng.
    *   **`src/`**: Thư mục chứa mã nguồn chính của dự án.
        *   **`main/java/com/yourdomain/legv8simulator/`**: Gói (package) Java gốc. Thay `com.yourdomain` bằng tên miền hoặc định danh của bạn.
            *   **`Main.java`**: Lớp chứa phương thức `main()`, là điểm bắt đầu của ứng dụng. Ban đầu, nó có thể chỉ khởi tạo các thành phần backend và chạy một kịch bản thử nghiệm đơn giản trên console. Sau này, nó sẽ khởi tạo `MainFrame` của GUI.
            *   **`core/`**: **Đây là trái tim của backend.** Chứa các lớp mô phỏng các thành phần phần cứng và chức năng cốt lõi của CPU.
                *   `CPU.java`: Lớp trung tâm, điều phối hoạt động của các thành phần khác (ALU, RegisterFile, Memory, ControlUnit, PC). Quản lý chu kỳ fetch-decode-execute.
                *   `ALU.java`: Thực hiện các phép toán số học (cộng, trừ) và logic (AND, OR, XOR, dịch bit). Nhận đầu vào và tín hiệu điều khiển từ `ControlUnit`, xuất kết quả và cờ trạng thái.
                *   `RegisterFile.java`: Mô phỏng tập thanh ghi X0-XZR. Cung cấp phương thức đọc và ghi giá trị vào thanh ghi.
                *   `Memory.java`: Mô phỏng bộ nhớ. Có thể tách thành `InstructionMemory` và `DataMemory` nếu cần, hoặc gộp chung và phân vùng. Cung cấp phương thức đọc và ghi dữ liệu tại một địa chỉ cụ thể.
                *   `ControlUnit.java`: Nhận opcode từ lệnh, tạo ra tất cả các tín hiệu điều khiển cần thiết cho datapath (ví dụ: `RegWrite`, `MemRead`, `MemWrite`, `ALUSrc`, `ALUOp`, `Branch`, `PCSrc`).
                *   `PC.java`: Mô phỏng thanh ghi Program Counter. Giữ địa chỉ của lệnh tiếp theo.
                *   `Instruction.java`: Một lớp (hoặc record trong Java 14+) để biểu diễn một lệnh đã được giải mã, chứa các trường như opcode, Rd, Rn, Rm, immediate, shamt, address, v.v., tùy theo định dạng lệnh.
                *   `Datapath.java`: (Tùy chọn) Nếu bạn muốn tách bạch rõ ràng hơn, lớp này có thể chứa logic kết nối các thành phần phần cứng và điều khiển luồng dữ liệu dựa trên tín hiệu từ `ControlUnit`. `CPU.java` sẽ sử dụng `Datapath.java`.
            *   **`instruction/`**: Chứa các lớp liên quan đến việc xử lý lệnh.
                *   `InstructionDecoder.java`: Chịu trách nhiệm phân tích mã máy 32-bit thành đối tượng `Instruction` (hoặc các trường riêng lẻ). Xác định định dạng lệnh.
                *   `InstructionExecutor.java`: Chứa logic để thực thi từng loại lệnh LEGv8. Có thể có các phương thức riêng cho mỗi lệnh hoặc nhóm lệnh (ví dụ: `executeRType()`, `executeDType()`, `executeLoad()`, `executeStore()`, `executeBranch()`). Lớp này sẽ tương tác chặt chẽ với `CPU.java` và các thành phần trong `core/`.
                *   `Opcode.java`: Một enum hoặc một lớp chứa các hằng số ánh xạ tên lệnh (ví dụ: `ADD`, `LDUR`) sang mã opcode nhị phân/thập phân của chúng và ngược lại, có thể cả thông tin về định dạng lệnh.
                *   `InstructionFormat.java`: Một enum để định nghĩa các loại định dạng lệnh (R, I, D, B, CB, IM).
                *   `LegV8Instructions.java`: (Tùy chọn, thiết kế nâng cao) Một interface hoặc abstract class mà mỗi lệnh cụ thể (ví dụ: `AddInstruction`, `LdurInstruction`) có thể implement/extend. Điều này giúp module hóa việc thực thi lệnh.
            *   **`assembler/`**: Chứa các lớp để xây dựng trình hợp dịch (chuyển mã assembly LEGv8 thành mã máy 32-bit).
                *   `Assembler.java`: Lớp điều phối chính của quá trình hợp dịch. Nhận đầu vào là mã assembly (chuỗi hoặc file), xuất ra mã máy.
                *   `Parser.java`: Phân tích từng dòng mã assembly. Tách nhãn, tên lệnh, các toán hạng. Kiểm tra cú pháp.
                *   `Lexer.java`: (Tùy chọn, cho parser phức tạp hơn) Tách dòng assembly thành các "token" (từ khóa, định danh, số, ký tự đặc biệt).
                *   `SymbolTable.java`: Lưu trữ ánh xạ giữa các nhãn (labels) trong mã assembly và địa chỉ bộ nhớ tương ứng của chúng. Cần thiết cho việc xử lý các lệnh rẽ nhánh và gọi thủ tục.
                *   `AssemblyException.java`: Lớp exception tùy chỉnh để báo lỗi trong quá trình hợp dịch (sai cú pháp, nhãn không xác định, v.v.).
            *   **`utils/`**: Chứa các lớp tiện ích được sử dụng ở nhiều nơi trong dự án.
                *   `BitUtils.java`: Các phương thức tĩnh để thực hiện các thao tác bit phổ biến như sign-extension, zero-extension, trích xuất các bit cụ thể từ một số nguyên.
                *   `FileUtils.java`: Các phương thức tiện ích để đọc nội dung từ file (ví dụ: file assembly) và ghi nội dung ra file (ví dụ: file mã máy, log).
                *   `Constants.java`: Định nghĩa các hằng số dùng chung trong toàn bộ dự án, ví dụ: `MEMORY_SIZE`, `NUM_REGISTERS`, `WORD_SIZE`.
            *   **`gui/`**: (Sẽ phát triển sau) Chứa tất cả các lớp liên quan đến giao diện người dùng Java Swing.
            *   **`common/`**: (Nếu cần) Chứa các interface hoặc lớp cơ sở được nhiều module khác sử dụng, ví dụ như trong mẫu thiết kế Observer để cập nhật GUI khi trạng thái backend thay đổi.
        *   **`main/resources/`**: Thư mục này thường dùng để chứa các tài nguyên được đóng gói cùng với file JAR, ví dụ như file cấu hình mặc định, hình ảnh.
        *   **`test/java/com/yourdomain/legv8simulator/`**: Cấu trúc gói tương tự như `src/main/java/` nhưng chứa các lớp kiểm thử đơn vị (Unit Tests) sử dụng JUnit hoặc TestNG. **Viết test cho backend là cực kỳ quan động**.
            *   Các file test nên tương ứng với các lớp trong `src/main/java`. Ví dụ, `CPUTest.java` sẽ kiểm thử các chức năng của `CPU.java`.
        *   **`test/resources/`**: Chứa các tài nguyên cần thiết cho việc kiểm thử, ví dụ như các file assembly đơn giản để kiểm tra trình hợp dịch và trình mô phỏng.
    *   **`target/`**: Thư mục được tạo bởi Maven/Gradle, chứa các file đã biên dịch (`.class`), file JAR đã đóng gói, và các output khác của quá trình build. Bạn không cần tạo thư mục này thủ công.

**Cách tiếp cận "Backend First":**

1.  **Tập trung vào `src/main/java/com/yourdomain/legv8simulator/core/` và `src/main/java/com/yourdomain/legv8simulator/instruction/` trước tiên.**
2.  **Xây dựng `RegisterFile.java`, `Memory.java`, `PC.java`**: Đây là các thành phần lưu trữ trạng thái cơ bản.
3.  **Xây dựng `ALU.java`**: Implement các phép toán cơ bản.
4.  **Xây dựng `Instruction.java`, `Opcode.java`, `InstructionFormat.java`**: Định nghĩa cách biểu diễn lệnh.
5.  **Xây dựng `InstructionDecoder.java`**: Có khả năng giải mã một vài lệnh LEGv8 cơ bản (ví dụ: `ADD`, `LDUR`, `STUR`) từ một chuỗi bit đầu vào thành đối tượng `Instruction`.
6.  **Xây dựng `ControlUnit.java`**: Tạo tín hiệu điều khiển cho các lệnh đã giải mã ở trên.
7.  **Xây dựng `CPU.java` và `InstructionExecutor.java`**: Kết hợp các thành phần trên để thực hiện chu kỳ fetch-decode-execute cho một vài lệnh cơ bản.
8.  **Viết Unit Tests (`src/test/java/`) song song**: Cho mỗi thành phần và chức năng bạn xây dựng, hãy viết test để đảm bảo nó hoạt động đúng. Ví dụ, kiểm tra ALU cộng đúng, Memory lưu và đọc đúng, Decoder giải mã đúng.
9.  **Xây dựng `Assembler` (`src/main/java/com/yourdomain/legv8simulator/assembler/`)**: Sau khi lõi CPU có thể thực thi mã máy (dạng bit), bạn có thể xây dựng trình hợp dịch để chuyển từ assembly sang mã máy đó. Bắt đầu với các lệnh đơn giản, không có nhãn. Sau đó thêm xử lý nhãn.
10. **Kiểm thử tích hợp**: Viết các file assembly đơn giản, dùng `Assembler` để dịch ra mã máy, rồi nạp vào `Memory` và cho `CPU` thực thi. Kiểm tra kết quả (giá trị thanh ghi, bộ nhớ) có đúng như mong đợi không.
11. **Mở rộng dần**: Thêm hỗ trợ cho nhiều lệnh hơn, các định dạng lệnh khác, xử lý rẽ nhánh, thủ tục, stack.
12. **Sau khi backend đã ổn định và được kiểm thử kỹ lưỡng, bạn mới bắt đầu xây dựng phần GUI (`src/main/java/com/yourdomain/legv8simulator/gui/`)**. GUI sẽ tương tác với backend thông qua các phương thức công khai của `CPU.java` hoặc các lớp quản lý mô phỏng khác.

Cấu trúc này giúp dự án của bạn được tổ chức tốt, dễ bảo trì, dễ mở rộng và dễ dàng cho việc kiểm thử từng phần riêng biệt. Chúc bạn thành công với dự án thú vị này!