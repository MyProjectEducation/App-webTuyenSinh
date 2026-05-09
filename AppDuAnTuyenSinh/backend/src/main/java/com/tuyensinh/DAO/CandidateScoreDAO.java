package com.tuyensinh.DAO;

import com.tuyensinh.models.CandidateScore;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import com.tuyensinh.services.SessionFactoryProvider;

import java.util.List;
import java.util.ArrayList;

public class CandidateScoreDAO {
    public static void createCandidateScore(CandidateScore score) {
        try {
            SessionFactory sessionFactory = SessionFactoryProvider
                    .provideSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction t = session.beginTransaction();

            session.persist(score);

            t.commit();

            sessionFactory.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void updateCandidateScore(CandidateScore score) {
        try {
            SessionFactory sessionFactory = SessionFactoryProvider
                    .provideSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction t = session.beginTransaction();

            session.merge(score);

            t.commit();

            sessionFactory.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void deleteCandidateScore(CandidateScore score) {
        try {
            SessionFactory sessionFactory = SessionFactoryProvider
                    .provideSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction t = session.beginTransaction();

            session.remove(score);

            t.commit();

            sessionFactory.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static List<CandidateScore> getAllCandidateScores() {
        List<CandidateScore> scores = new ArrayList<>();
        try {
            SessionFactory sessionFactory = SessionFactoryProvider
                    .provideSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction t = session.beginTransaction();

            scores = session.createQuery("FROM CandidateScore", CandidateScore.class).list();

            t.commit();

            sessionFactory.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return scores;
    }

    public static CandidateScore getCandidateScoreByCCCD(String cccd) {
        CandidateScore score = null;
        try {
            SessionFactory sessionFactory = SessionFactoryProvider
                    .provideSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction t = session.beginTransaction();

            score = session.createQuery("FROM CandidateScore WHERE cccd = :cccd", CandidateScore.class)
                    .setParameter("cccd", cccd)
                    .uniqueResult();

            t.commit();

            sessionFactory.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return score;
    }
}
