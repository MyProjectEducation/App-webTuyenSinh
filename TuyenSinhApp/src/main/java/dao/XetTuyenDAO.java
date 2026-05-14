package dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;
import java.util.List;

public class XetTuyenDAO {

    /**
     * Tab 1: Thống kê tổng hợp theo từng ngành học
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getSummaryData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = "SELECT n.manganh, n.tennganh, n.n_chitieu, "
                       + "COUNT(CASE WHEN nv.nv_ketqua = 'Trúng tuyển' THEN 1 END) AS so_trung_tuyen, "
                       + "COALESCE(MIN(CASE WHEN nv.nv_ketqua = 'Trúng tuyển' THEN nv.diem_xettuyen END), 0) AS min_diem, "
                       + "COALESCE(MAX(CASE WHEN nv.nv_ketqua = 'Trúng tuyển' THEN nv.diem_xettuyen END), 0) AS max_diem, "
                       + "COALESCE(AVG(CASE WHEN nv.nv_ketqua = 'Trúng tuyển' THEN nv.diem_xettuyen END), 0) AS avg_diem, "
                       + "CASE WHEN COUNT(CASE WHEN nv.nv_ketqua = 'Trúng tuyển' THEN 1 END) >= n.n_chitieu THEN 'yes' ELSE 'duoisan' END "
                       + "FROM xt_nganh n "
                       + "LEFT JOIN xt_nguyenvongxettuyen nv ON n.manganh = nv.nv_manganh "
                       + "GROUP BY n.manganh, n.tennganh, n.n_chitieu";
            return session.createNativeQuery(sql).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tab 2: Chi tiết danh sách toàn bộ nguyện vọng và kết quả thí sinh
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getDetailData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = "SELECT COALESCE(n.tennganh, 'Chưa xác định') AS ten_nganh, nv.nn_cccd, "
                       + "CONCAT(COALESCE(ts.ho, ''), ' ', COALESCE(ts.ten, '')) AS ho_ten, "
                       + "nv.nv_tt, COALESCE(nv.tt_phuongthuc, 'Mặc định') AS phuong_thuc, "
                       + "COALESCE(nv.tt_thm, 'Chưa chọn') AS to_hop, COALESCE(nv.diem_xettuyen, 0) AS diem, "
                       + "COALESCE(nv.nv_ketqua, 'Chưa xét') AS ket_qua "
                       + "FROM xt_nguyenvongxettuyen nv "
                       + "LEFT JOIN xt_thisinhxettuyen25 ts ON nv.nn_cccd = ts.cccd "
                       + "LEFT JOIN xt_nganh n ON nv.nv_manganh = n.manganh "
                       + "LIMIT 300"; // Giới hạn dòng dữ liệu để giao diện tải mượt mà
            return session.createNativeQuery(sql).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tab 3: Thống kê số lượng trúng tuyển phân rã theo từng phương thức xét tuyển
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getMethodData() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = "SELECT n.manganh, n.tennganh, "
                       + "COUNT(CASE WHEN nv.nv_ketqua = 'Trúng tuyển' AND nv.tt_phuongthuc = 'THPT' THEN 1 END) AS thpt, "
                       + "COUNT(CASE WHEN nv.nv_ketqua = 'Trúng tuyển' AND nv.tt_phuongthuc = 'VSAT' THEN 1 END) AS vsat, "
                       + "COUNT(CASE WHEN nv.nv_ketqua = 'Trúng tuyển' AND nv.tt_phuongthuc = 'DGNL' THEN 1 END) AS dgnl, "
                       + "COUNT(CASE WHEN nv.nv_ketqua = 'Trúng tuyển' THEN 1 END) AS tong "
                       + "FROM xt_nganh n "
                       + "LEFT JOIN xt_nguyenvongxettuyen nv ON n.manganh = nv.nv_manganh "
                       + "GROUP BY n.manganh, n.tennganh";
            return session.createNativeQuery(sql).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tính toán tổng quan số liệu cho 3 thẻ thống kê (Stat Cards) trên đỉnh giao diện
     */
    public Object[] getGlobalStats() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = "SELECT "
                       + "COUNT(CASE WHEN nv_ketqua = 'Trúng tuyển' THEN 1 END) AS tt, "
                       + "COUNT(CASE WHEN nv_ketqua = 'Dưới sàn' THEN 1 END) AS ds, "
                       + "COUNT(CASE WHEN nv_ketqua IS NULL OR nv_ketqua = 'Chưa xét' OR nv_ketqua = '' THEN 1 END) AS cx "
                       + "FROM xt_nguyenvongxettuyen";
            List<Object[]> res = session.createNativeQuery(sql).list();
            if (res != null && !res.isEmpty()) {
                return (Object[]) res.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Object[]{0, 0, 0};
    }

    /**
     * Thuật toán Mô phỏng Xét Tuyển Thật: Tự động tính toán cập nhật dữ liệu dưới DB 
     * để phục vụ minh họa sinh động khi chạy đồ án cho giảng viên xem.
     */
    public void executeAdmissionsSimulation() {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            // Thiết lập: Các thí sinh có điểm xét tuyển >= 22.0 sẽ được Trúng tuyển, ngược lại là Dưới sàn
            String sql1 = "UPDATE xt_nguyenvongxettuyen SET nv_ketqua = 'Trúng tuyển' WHERE diem_xettuyen >= 22";
            String sql2 = "UPDATE xt_nguyenvongxettuyen SET nv_ketqua = 'Dưới sàn' WHERE diem_xettuyen < 22 OR diem_xettuyen IS NULL";
            session.createNativeQuery(sql1).executeUpdate();
            session.createNativeQuery(sql2).executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}