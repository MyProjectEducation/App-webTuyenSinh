# Hệ thống Tuyển sinh TDTU 2025 — Frontend Java Swing

## Stack công nghệ
- **UI**: Java Swing (JFrame, JTable, JPanel, JDialog)
- **ORM**: Hibernate 5.6
- **DB**: MySQL
- **Build**: Maven
- **Excel**: Apache POI

---

## Cấu trúc project

```
TuyenSinhApp/
├── pom.xml
└── src/main/java/
    └── ui/
        ├── MainApp.java                  ← Entry point
        ├── LoginFrame.java               ← Màn hình đăng nhập
        ├── MainFrame.java                ← Frame chính + Sidebar nav
        ├── components/
        │   ├── AppTheme.java             ← Màu sắc, font, kích thước
        │   └── UIComponents.java         ← Button, Badge, Table helper
        ├── panels/
        │   ├── BasePanel.java            ← Abstract base cho mọi panel
        │   ├── DashboardPanel.java       ← Dashboard tổng quan
        │   ├── ThiSinhPanel.java         ← xt_thisinhxettuyen25
        │   ├── NganhPanel.java           ← xt_nganh
        │   ├── TohopMonPanel.java        ← xt_tohop_monthi
        │   ├── NganhTohopPanel.java      ← xt_nganh_tohop
        │   ├── DiemThiPanel.java         ← xt_diemthixettuyen
        │   ├── DiemCongPanel.java        ← xt_diemcongxetuyen
        │   └── AllPanels.java            ← NguyenVong, XetTuyen,
        │                                    BangQuyDoi, NguoiDung
        └── dialogs/
            ├── ThiSinhDialog.java        ← Form thêm/sửa thí sinh
            └── NganhDialog.java          ← Form thêm/sửa ngành
```

---

## Các màn hình đã làm

| # | Màn hình | Bảng DB | Chức năng |
|---|----------|---------|-----------|
| 1 | Dashboard | Tổng hợp | Stat cards, biểu đồ ngành, thí sinh gần nhất |
| 2 | Thí sinh | `xt_thisinhxettuyen25` | Xem DS (20 row/page), tìm kiếm cccd/họ tên, thêm/sửa |
| 3 | Ngành | `xt_nganh` | CRUD, lọc theo phương thức |
| 4 | Tổ hợp môn | `xt_tohop_monthi` | CRUD: matohop, mon1/2/3, tentohop |
| 5 | Ngành–Tổ hợp | `xt_nganh_tohop` | CRUD: th_mon1/2/3, hsmon1/2/3, dolech, tb_keys |
| 6 | Điểm thi | `xt_diemthixettuyen` | 3 loại: PT4/PT2/PT3, import theo loại |
| 7 | Điểm cộng | `xt_diemcongxetuyen` | diemCC, diemUtxt, diemTong, dc_keys |
| 8 | Nguyện vọng | `xt_nguyenvongxettuyen` | nv_tt, diem_xettuyen, nv_ketqua |
| 9 | Xét tuyển | Tổng hợp NV | Chạy xét tuyển, thống kê kết quả |
| 10 | Bảng quy đổi | `xt_bangquydoi` | DGNL/VSAT: d_diema→d_diemb → d_diemc→d_diemd |
| 11 | Người dùng | — | admin/user, enable/disable, đổi password, đổi quyền |

---

## Cách build & chạy

### 1. Yêu cầu
- Java 11+
- Maven 3.6+
- MySQL 8.0

### 2. Build fat JAR
```bash
cd TuyenSinhApp
mvn clean package
```

### 3. Chạy
```bash
java -jar target/TuyenSinhApp.jar
```

---

## Bước tiếp theo (Backend)

Cần implement các lớp sau để kết nối thật với MySQL:

```
src/main/java/
├── entity/
│   ├── ThiSinh.java          ← @Entity xt_thisinhxettuyen25
│   ├── Nganh.java            ← @Entity xt_nganh
│   ├── TohopMon.java         ← @Entity xt_tohop_monthi
│   ├── NganhTohop.java       ← @Entity xt_nganh_tohop
│   ├── DiemThi.java          ← @Entity xt_diemthixettuyen
│   ├── DiemCong.java         ← @Entity xt_diemcongxetuyen
│   ├── NguyenVong.java       ← @Entity xt_nguyenvongxettuyen
│   └── BangQuyDoi.java       ← @Entity xt_bangquydoi
├── dao/
│   ├── ThiSinhDAO.java
│   ├── NganhDAO.java
│   ├── DiemThiDAO.java
│   ├── NguyenVongDAO.java
│   └── BangQuyDoiDAO.java
├── service/
│   ├── XetTuyenService.java  ← Logic: diem_xettuyen = thxt + utqd + cong
│   ├── ImportService.java    ← Apache POI: đọc Excel → save Hibernate
│   └── ExportService.java    ← Apache POI: xuất kết quả → Excel
└── util/
    ├── HibernateUtil.java    ← SessionFactory singleton
    └── PasswordUtil.java     ← BCrypt hash/verify
```

### hibernate.cfg.xml (đặt trong src/main/resources/)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
    "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
    "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
  <session-factory>
    <property name="connection.driver_class">com.mysql.cj.jdbc.Driver</property>
    <property name="connection.url">jdbc:mysql://localhost:3306/tuyensinh2025?useSSL=false&amp;serverTimezone=Asia/Ho_Chi_Minh</property>
    <property name="connection.username">root</property>
    <property name="connection.password">yourpassword</property>
    <property name="dialect">org.hibernate.dialect.MySQL8Dialect</property>
    <property name="show_sql">true</property>
    <property name="hbm2ddl.auto">validate</property>
    <!-- Entity mapping -->
    <mapping class="entity.ThiSinh"/>
    <mapping class="entity.Nganh"/>
    <mapping class="entity.TohopMon"/>
    <mapping class="entity.NganhTohop"/>
    <mapping class="entity.DiemThi"/>
    <mapping class="entity.DiemCong"/>
    <mapping class="entity.NguyenVong"/>
    <mapping class="entity.BangQuyDoi"/>
  </session-factory>
</hibernate-configuration>
```
