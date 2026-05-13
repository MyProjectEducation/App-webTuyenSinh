package dao;

import entity.Nganh;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public class NganhDAO {
    public List<Nganh> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Nganh", Nganh.class).list();
        }
    }

    public Nganh findByMaNganh(String maNganh) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Nganh where maNganh = :ma", Nganh.class)
                .setParameter("ma", maNganh)
                .uniqueResult();
        }
    }

    public void saveOrUpdate(Nganh nganh) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(nganh);
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
            Nganh nganh = session.get(Nganh.class, id);
            if (nganh != null) session.delete(nganh);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }

    public long countByToHopGoc(String maToHop) {
        if (maToHop == null || maToHop.trim().isEmpty()) return 0;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "select count(n) from Nganh n where lower(n.toHopGoc) = :ma",
                    Long.class)
                .setParameter("ma", maToHop.trim().toLowerCase())
                .uniqueResult();
        }
    }
}
