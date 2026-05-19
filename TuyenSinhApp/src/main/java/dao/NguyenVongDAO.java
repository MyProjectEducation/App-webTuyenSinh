package dao;

import entity.NguyenVong;
import org.hibernate.Session;
import org.hibernate.Transaction;
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

    public NguyenVong findById(int idnv) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(NguyenVong.class, idnv);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public NguyenVong findByCccdAndNvTt(String cccd, int nvTt) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from NguyenVong nv where nv.nnCccd = :cccd and nv.nvTt = :nvTt", NguyenVong.class)
                .setParameter("cccd", cccd.trim())
                .setParameter("nvTt", nvTt)
                .setMaxResults(1)
                .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveOrUpdate(NguyenVong nv) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            if (nv.getIdnv() == 0) {
                session.save(nv);
            } else {
                session.update(nv);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public void deleteById(int idnv) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            NguyenVong nv = session.get(NguyenVong.class, idnv);
            if (nv != null) {
                session.delete(nv);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            throw e;
        }
    }

    public long countTotalNguyenVongs() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(nv) FROM NguyenVong nv", Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Object[]> getTop8NguyenvongByNganh() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT nv.nvManganh, COUNT(nv) FROM NguyenVong nv GROUP BY nv.nvManganh ORDER BY COUNT(nv) DESC";
            return session.createQuery(hql, Object[].class)
                    .setMaxResults(8)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public java.util.Map<String, Long> getStatusStatistics() {
        java.util.Map<String, Long> stats = new java.util.HashMap<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT nv.nvKetqua, COUNT(nv) FROM NguyenVong nv GROUP BY nv.nvKetqua";
            List<Object[]> results = session.createQuery(hql, Object[].class).list();
            for (Object[] row : results) {
                String status = row[0] == null || row[0].toString().trim().isEmpty() ? "Chưa xét" : row[0].toString().trim();
                // Merge if status already exists (e.g., multiple variations of empty string)
                stats.put(status, stats.getOrDefault(status, 0L) + (Long) row[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stats;
    }
}