# Welcome to my repo

## This is my Folder Structure


```
LEGv8_Simulator/
├── .idea/                     # Cấu hình của IntelliJ IDEA (hoặc IDE tương tự)
├── .gitignore                 # Các file và thư mục bỏ qua bởi Git
├── pom.xml                    # (Nếu dùng Maven) hoặc build.gradle (Nếu dùng Gradle)
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