package dao;

import entity.TohopMon;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

public class TohopMonDAO {
    public List<TohopMon> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from TohopMon", TohopMon.class).list();
        }
    }

    public TohopMon findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TohopMon.class, id);
        }
    }

    public TohopMon findByMaToHop(String maToHop) {
        if (maToHop == null || maToHop.trim().isEmpty()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from TohopMon where lower(maToHop) = :ma",
                    TohopMon.class)
                .setParameter("ma", maToHop.trim().toLowerCase())
                .setMaxResults(1)
                .uniqueResult();
        }
    }

    public void saveOrUpdate(TohopMon tohop) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.saveOrUpdate(tohop);
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
            TohopMon tohop = session.get(TohopMon.class, id);
            if (tohop != null) session.delete(tohop);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }
}
