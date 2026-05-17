package dao;

import entity.DiemThiSinh;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;
import java.util.ArrayList;

public class DiemThiSinhDAO {

    public static void createCandidateScore(DiemThiSinh score) {
        Transaction t = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            t = session.beginTransaction();
            session.merge(score);
            t.commit();
        } catch (Exception e) {
            // KIỂM TRA AN TOÀN: Chỉ rollback khi transaction tồn tại và đang hoạt động
            if (t != null && t.isActive()) {
                try {
                    t.rollback();
                } catch (Exception ex) {
                    System.err.println("Không thể rollback transaction: " + ex.getMessage());
                }
            }
            // QUAN TRỌNG: Ném lỗi ra ngoài để hàm đọc Excel biết được lý do lỗi thực sự là
            // gì
            throw e;
        }
    }

    public static void updateCandidateScore(DiemThiSinh score) {
        Transaction t = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            t = session.beginTransaction();
            session.merge(score);
            t.commit();
        } catch (Exception e) {
            if (t != null)
                t.rollback();
            e.printStackTrace();
        }
    }

    public static void deleteCandidateScore(DiemThiSinh score) {
        Transaction t = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            t = session.beginTransaction();
            session.remove(score);
            t.commit();
        } catch (Exception e) {
            if (t != null)
                t.rollback();
            e.printStackTrace();
        }
    }

    public static List<DiemThiSinh> getAllCandidateScores() {
        List<DiemThiSinh> scores = new ArrayList<>();
        // Đối với thao tác đọc (Read), không nhất thiết phải xử lý Transaction quá phức
        // tạp
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            scores = session.createQuery("FROM DiemThiSinh", DiemThiSinh.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scores;
    }

    public static DiemThiSinh getCandidateScoreByCCCD(String cccd) {
        DiemThiSinh score = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            score = session.createQuery("FROM DiemThiSinh WHERE cccd = :cccd", DiemThiSinh.class)
                    .setParameter("cccd", cccd)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return score;
    }
}