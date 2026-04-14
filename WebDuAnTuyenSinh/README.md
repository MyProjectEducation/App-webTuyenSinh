# Hướng Dẫn Chạy Dự Án Web Tuyển Sinh (WebDuAnTuyenSinh)

Dự án này là một hệ thống Web hoàn chỉnh, bao gồm giao diện React (Client) và hệ thống API chạy bằng Node.js/Express (Server). Hệ thống kết nối chung vào MySQL Database (`xettuyen2026`) để đồng bộ với phần mềm Desktop (App).

## Yêu cầu Hệ thống
- **Node.js**: Phiên bản 18 trở lên.
- **Trình quản lý gói**: `npm` (có sẵn khi cài Node.js).
- **MySQL Server**: Đang chạy ở localhost port `3306`.
  - Database: `xettuyen2026` (Đã import file SQL cài đặt).

---

## 1. Khởi động Backend Server (Node.js)

Backend chịu trách nhiệm giao tiếp với Database qua cổng 5000 và cung cấp các API cho giao diện React.

```bash
# 1. Cấp quyền truy cập vào thư mục server
cd server

# 2. (Chỉ chạy lần đầu) Cài đặt tất cả các package/thư viện cần thiết
npm install

# 3. Chạy Server
npm start
```

**Thành công**: Terminal sẽ hiện lên thông báo: `Node.js Backend is running on http://localhost:5000`. Cửa sổ Terminal này cần được **mở liên tục** trong lúc chạy Web.

---

## 2. Khởi động Frontend Client (React Vite)

Client chịu trách nhiệm hiển thị giao diện người dùng dựa trên framework Vite + TailwindCSS. Bạn cần **mở một cửa sổ Terminal/Command Prompt thứ 2** để khởi chạy frontend song song với backend.

```bash
# 1. Cấp quyền truy cập vào thư mục client
cd client

# 2. (Chỉ chạy lần đầu) Cài đặt toàn bộ package (bao gồm react, lucide, axios...)
npm install

# 3. Chạy môi trường Dev server của React
npm run dev
```

**Thành công**: Terminal sẽ cung cấp một đường link (mặc định thường là `http://localhost:5173/`). Bạn dùng trình duyệt web bấm vào liên kết đó để mở ứng dụng Web.

---

## Ghi chú Quan trọng

- Web App và Java Desktop App đều xài chung **một Database MySQL**. Bất cứ thao tác Thêm/Sửa/Xóa bên nền tảng nào cũng sẽ được tự động đồng bộ sang nền tảng kia khi tải lại trang/form.
- Với tính năng Đăng nhập Admin: Mật khẩu lưu dưới Database phải tuân thủ chuẩn mã hóa **BCrypt**. Backend Node.js cũng sẽ dùng BCrypt để đối chiếu. 
