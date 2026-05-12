package dao;

import entity.NganhTohop;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public class NganhTohopDAO {
    public List<NganhTohop> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from NganhTohop", NganhTohop.class).list();
        }
    }

    public NganhTohop findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(NganhTohop.class, id);
        }
    }

    public void saveOrUpdate(NganhTohop nganhTohop) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(nganhTohop);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }

    public void deleteById(Integer id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            NganhTohop nganhTohop = session.get(NganhTohop.class, id);
            if (nganhTohop != null) session.delete(nganhTohop);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }

    public long countByMaNganh(String maNganh) {
        if (maNganh == null || maNganh.trim().isEmpty()) return 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select count(n) from NganhTohop n where lower(n.maNganh) = :ma",
                    Long.class)
                .setParameter("ma", maNganh.trim().toLowerCase())
                .uniqueResult();
        }
    }

    public long countByMaToHop(String maToHop) {
        if (maToHop == null || maToHop.trim().isEmpty()) return 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select count(n) from NganhTohop n where lower(n.maToHop) = :ma",
                    Long.class)
                .setParameter("ma", maToHop.trim().toLowerCase())
                .uniqueResult();
        }
    }
}
