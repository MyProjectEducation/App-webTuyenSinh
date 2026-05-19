package dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;
import java.util.ArrayList;

import util.HibernateUtil;
import entity.ThiSinh;

public class ThiSinhDAO {
    public static void createCandidate(ThiSinh candidate) {
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction t = session.beginTransaction();

            session.merge(candidate);

            t.commit();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void updateCandidate(ThiSinh candidate) {
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction t = session.beginTransaction();

            session.merge(candidate);

            t.commit();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void deleteCandidate(ThiSinh candidate) {
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction t = session.beginTransaction();

            session.remove(candidate);

            t.commit();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static ThiSinh getCandidateById(int id) {
        ThiSinh candidate = null;
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.openSession();

            candidate = session.get(ThiSinh.class, id);

        } catch (Exception e) {
            System.out.println(e);
        }
        return candidate;
    }

    public static ThiSinh getCandidateByCCCD(String cccd) {
        ThiSinh candidate = null;
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.openSession();

            candidate = session.createQuery("FROM ThiSinh WHERE cccd = :cccd", ThiSinh.class)
                    .setParameter("cccd", cccd)
                    .uniqueResult();

        } catch (Exception e) {
            System.out.println(e);
        }
        return candidate;
    }

    public static List<ThiSinh> getAllCandidates() {
        List<ThiSinh> candidates = new ArrayList<>();
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            Session session = sessionFactory.openSession();

            candidates = session.createQuery("FROM ThiSinh", ThiSinh.class)
                    .getResultList();

        } catch (Exception e) {
            System.out.println(e);
        }
        return candidates;
    }

    public static long countTotalCandidates() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("SELECT COUNT(t) FROM ThiSinh t", Long.class).uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static List<ThiSinh> getTop5RecentCandidates() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ThiSinh ORDER BY idthisinh DESC", ThiSinh.class)
                    .setMaxResults(5)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
