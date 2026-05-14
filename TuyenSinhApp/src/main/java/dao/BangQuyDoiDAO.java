package dao;

import entity.BangQuyDoi;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import util.HibernateUtil; // Đảm bảo bạn có lớp Utility để lấy SessionFactory

public class BangQuyDoiDAO {

    /**
     * Lấy toàn bộ danh sách quy đổi từ database
     */
    public List<BangQuyDoi> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sử dụng HQL (Hibernate Query Language) - "BangQuyDoi" là tên Class Entity
            return session.createQuery("FROM BangQuyDoi", BangQuyDoi.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tìm kiếm theo mã quy đổi (Hỗ trợ chức năng search trên Panel)
     */
    public List<BangQuyDoi> searchByMa(String ma) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BangQuyDoi b WHERE b.maQuyDoi LIKE :ma";
            return session.createQuery(hql, BangQuyDoi.class)
                          .setParameter("ma", "%" + ma + "%")
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lưu hoặc cập nhật bản ghi
     */
    public void saveOrUpdate(BangQuyDoi bqd) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.saveOrUpdate(bqd);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}