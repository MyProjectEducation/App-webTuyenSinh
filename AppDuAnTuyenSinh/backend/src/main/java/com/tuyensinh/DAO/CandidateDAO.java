package com.tuyensinh.DAO;

import com.tuyensinh.models.Candidate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import com.tuyensinh.services.SessionFactoryProvider;

import java.util.List;
import java.util.ArrayList;

public class CandidateDAO {
    public static void createCandidate(Candidate candidate) {
        try {
            SessionFactory sessionFactory = SessionFactoryProvider
                    .provideSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction t = session.beginTransaction();

            session.merge(candidate);

            t.commit();

            sessionFactory.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void updateCandidate(Candidate candidate) {
        try {
            SessionFactory sessionFactory = SessionFactoryProvider
                    .provideSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction t = session.beginTransaction();

            session.merge(candidate);

            t.commit();

            sessionFactory.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void deleteCandidate(Candidate candidate) {
        try {
            SessionFactory sessionFactory = SessionFactoryProvider
                    .provideSessionFactory();
            Session session = sessionFactory.openSession();
            Transaction t = session.beginTransaction();

            session.remove(candidate);

            t.commit();

            sessionFactory.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static Candidate getCandidateById(int id) {
        Candidate candidate = null;
        try {
            SessionFactory sessionFactory = SessionFactoryProvider
                    .provideSessionFactory();
            Session session = sessionFactory.openSession();

            candidate = session.get(Candidate.class, id);

            sessionFactory.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return candidate;
    }

    public static Candidate getCandidateByCCCD(String cccd) {
        Candidate candidate = null;
        try {
            SessionFactory sessionFactory = SessionFactoryProvider
                    .provideSessionFactory();
            Session session = sessionFactory.openSession();

            candidate = session.createQuery("FROM Candidate WHERE cccd = :cccd", Candidate.class)
                    .setParameter("cccd", cccd)
                    .uniqueResult();

            sessionFactory.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return candidate;
    }

    public static List<Candidate> getAllCandidates() {
        List<Candidate> candidates = new ArrayList<>();
        try {
            SessionFactory sessionFactory = SessionFactoryProvider
                    .provideSessionFactory();
            Session session = sessionFactory.openSession();

            candidates = session.createQuery("FROM Candidate", Candidate.class)
                    .getResultList();

            sessionFactory.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return candidates;
    }
}