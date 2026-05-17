package dao;

import entity.BangQuyDoi;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.util.List;

public class BangQuyDoiDAO {

    /** Toàn bộ bản ghi `xt_bangquydoi`, cùng thứ tự với web API. */
    public List<BangQuyDoi> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from BangQuyDoi b order by b.phuongThuc, b.mon, b.diemA",
                    BangQuyDoi.class)
                .list();
        }
    }

    /**
     * Lọc theo từ khóa (mã quy đổi, môn, tổ hợp, phương thức) và phương thức.
     * {@code phuongThucChoice} = {@code null} / rỗng / {@code "Tất cả"} → không lọc PT.
     */
    public List<BangQuyDoi> findFiltered(String keyword, String phuongThucChoice) {
        boolean hasKw = keyword != null && !keyword.trim().isEmpty();
        boolean hasPt = phuongThucChoice != null && !phuongThucChoice.trim().isEmpty()
            && !phuongThucChoice.trim().startsWith("Tất cả");
        String ptNorm = hasPt ? phuongThucChoice.trim().toLowerCase() : "";
        String kwNorm = hasKw ? keyword.trim().toLowerCase() : "";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("from BangQuyDoi b where 1=1");
            if (hasKw) {
                hql.append(" and (lower(b.maQuyDoi) like :kw")
                   .append(" or lower(b.mon) like :kw")
                   .append(" or lower(b.toHop) like :kw")
                   .append(" or lower(b.phuongThuc) like :kw)");
            }
            if (hasPt) {
                hql.append(" and lower(trim(b.phuongThuc)) = :pt");
            }
            hql.append(" order by b.phuongThuc, b.mon, b.diemA");

            Query<BangQuyDoi> query = session.createQuery(hql.toString(), BangQuyDoi.class);
            if (hasKw) query.setParameter("kw", "%" + kwNorm + "%");
            if (hasPt) query.setParameter("pt", ptNorm);
            return query.list();
        }
    }

    public BangQuyDoi findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(BangQuyDoi.class, id);
        }
    }

    public void deleteById(int id) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            BangQuyDoi row = session.get(BangQuyDoi.class, id);
            if (row != null) session.delete(row);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }

    public BangQuyDoi findByMaQuyDoi(String ma) {
        if (ma == null || ma.trim().isEmpty()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from BangQuyDoi b where b.maQuyDoi = :ma", BangQuyDoi.class)
                .setParameter("ma", ma.trim())
                .uniqueResult();
        }
    }

    /** {@code isNew=true} → insert; ngược lại cập nhật theo {@code idqd}. */
    public void save(BangQuyDoi fromForm, boolean isNew) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            if (isNew) {
                session.save(fromForm);
            } else {
                BangQuyDoi managed = session.get(BangQuyDoi.class, fromForm.getIdqd());
                if (managed == null) {
                    throw new IllegalArgumentException("Không tìm thấy bản ghi id=" + fromForm.getIdqd());
                }
                managed.setPhuongThuc(fromForm.getPhuongThuc());
                managed.setToHop(fromForm.getToHop());
                managed.setMon(fromForm.getMon());
                managed.setDiemA(fromForm.getDiemA());
                managed.setDiemB(fromForm.getDiemB());
                managed.setDiemC(fromForm.getDiemC());
                managed.setDiemD(fromForm.getDiemD());
                managed.setMaQuyDoi(fromForm.getMaQuyDoi());
                managed.setPhanVi(fromForm.getPhanVi());
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }
}
