# Hướng Dẫn Chạy Dự Án App Tuyển Sinh

Dự án này ứng dụng cấu trúc **Multi-module Maven** được cấu hình chuyên biệt để tách biệt giữa Phần Lõi/Cơ sở dữ liệu (`backend`) và Giao diện Người dùng (`frontend`). 

- **IDE khuyên dùng**: IntelliJ IDEA, Eclipse, hoặc VS Code.
- **Yêu cầu hệ thống**:
  - Java Development Kit (JDK) 17 trở lên.
  - Apache Maven.
  - MySQL Server (Có cấu hình Database tương ứng trong thư mục `backend/src/main/resources`).

## Cấu Trúc Dự Án
- **`backend/`**: Chứa toàn bộ các class nghiệp vụ kinh doanh, dữ liệu, (Models, Services, DAOs) và tệp cấu hình Hibernate. Các Entity model tự động ánh xạ với Database thông qua `hibernate.cfg.xml`.
- **`frontend/`**: Chứa gói `views` xử lý Giao diện người dùng đồ họa bằng thư viện FlatLaf và có chứa tệp khởi chạy ứng dụng `App.java`.  Mô-đun Front-End được liên kết để truy xuất trực tiếp các lớp của mô-đun Back-End.

---

## Bước Chuẩn Bị (Rất Quan Trọng)
Trước khi chạy ứng dụng, bạn bắt buộc phải có Database `xettuyen2026`:
1. Mở MySQL Console hoặc công cụ quản lý như phpMyAdmin.
2. Thiết lập chạy file SQL `xettuyen2026_empty.sql` (Có trong mục tài liệu/đặc tả) để cấu trúc sẵn các bảng như `xt_thisinhxettuyen25`, `xt_nganh`, v.v.
3. Chắc chắn rằng mật khẩu của user `root` kết nối MySQL tại localhost (port 3306) là mật khẩu **RỖNG** (Blank). Nếu có mật khẩu, hãy tự chỉnh lại tại file `backend/src/main/resources/hibernate.cfg.xml`.

---

## Cách 1: Chạy trực tiếp từ môi trường IDE (Cách khuyên dùng)

Hầu hết các trình biên tập mã hiện đại sẽ tự nhận diện đây là một thiết kế Maven đa thư mục.

1. **Import/Open** toàn bộ thư mục `AppDuAnTuyenSinh` (cấp cha) dưới dạng Project.
2. Tìm đến file **`App.java`** trong thư mục đường dẫn tại nhánh: 
   `frontend/src/main/java/com/tuyensinh/App.java`
3. Nhấn chuột phải vào `App.java` và chọn **Run 'App.main()'** (trên IntelliJ/Eclipse/VSCode).

Hệ thống IDE sẽ tự động biên dịch `backend` và áp dụng vào cấu hình của `frontend` để khởi động giao diện.

---

## Cách 2: Chạy thông qua Commmand Line (Terminal) bằng Maven

Nếu bạn muốn chạy ứng dụng độc lập qua Script hay Command Prompt:

### Bước 1: Build (Biên dịch) Toàn bộ Hệ Thống
Mở Terminal hoặc Command Prompt, trỏ đường dẫn tới thư mục gốc (Thư mục chứa file `README.md` này), nơi có `pom.xml` cấu trúc cha, sau đó gõ:

```bash
mvn clean install
```
*Lệnh này sẽ dọn dẹp các tệp build trước và đóng gói `backend` ra thành dạng `jar`, sau đó cài vào Repository Maven dưới nền để `frontend` có thể sử dụng.*

### Bước 2: Khởi Động Ứng dụng
Di chuyển vào vị trí của thư mục `frontend` để chạy App:
```bash
cd frontend
mvn exec:java -D"exec.mainClass"="com.tuyensinh.App"
```

## Giải quyết lỗi biên dịch tiềm ẩn
Nếu IntelliJ hoặc các IDE báo lỗi đỏ phần "Dependency backend không tồn tại", hãy chạy lại lệnh `mvn clean install` trên Terminal một lần để IDE nhận lại được nhánh module `backend`.
