package dao;

import entity.DiemCong;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public class DiemCongDAO {

    public List<DiemCong> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DiemCong d order by d.id", DiemCong.class).list();
        }
    }

    /** Tìm theo CCCD (ts_cccd) chứa chuỗi, không phân biệt hoa thường */
    public List<DiemCong> findByCccdContaining(String fragment) {
        if (fragment == null || fragment.trim().isEmpty()) {
            return findAll();
        }
        String q = "%" + fragment.trim().toLowerCase() + "%";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from DiemCong d where lower(d.cccd) like :q order by d.id",
                    DiemCong.class)
                .setParameter("q", q)
                .list();
        }
    }

    public void deleteById(Integer id) {
        if (id == null) return;
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            DiemCong row = session.get(DiemCong.class, id);
            if (row != null) session.delete(row);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }
}
