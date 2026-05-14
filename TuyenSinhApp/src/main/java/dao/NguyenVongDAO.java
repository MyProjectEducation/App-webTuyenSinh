package dao;

import org.hibernate.Session;
import util.HibernateUtil;
import java.util.List;

public class NguyenVongDAO {

    /**
     * Truy vấn Native SQL kết hợp bảng Nguyện Vọng và bảng Thí Sinh để lấy Họ Tên
     */
    @SuppressWarnings("unchecked")
    public List<Object[]> getAllForPanel() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String sql = "SELECT nv.nn_cccd, CONCAT(COALESCE(ts.ho, ''), ' ', COALESCE(ts.ten, '')) AS ho_ten, "
                       + "nv.nv_tt, nv.nv_manganh, nv.tt_thm, nv.diem_thxt, nv.diem_cong, nv.diem_utqd, "
                       + "nv.diem_xettuyen, nv.tt_phuongthuc, nv.nv_ketqua, nv.idnv "
                       + "FROM xt_nguyenvongxettuyen nv "
                       + "LEFT JOIN xt_thisinhxettuyen25 ts ON nv.nn_cccd = ts.cccd";
            return session.createNativeQuery(sql).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}