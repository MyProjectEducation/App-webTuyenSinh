package dao;

import entity.DiemCong;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.util.List;

public class DiemCongDAO {

    public List<DiemCong> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DiemCong d order by d.id", DiemCong.class).list();
        }
    }

    /**
     * Lọc theo CCCD (tuỳ chọn) và phương thức xét tuyển: {@code null} / rỗng / {@code "Tất cả"} = không lọc PT.
     * Giá trị PT so khớp không phân biệt hoa thường (VD: VSAT, THPT).
     */
    public List<DiemCong> findFiltered(String cccdFragment, String phuongThucChoice) {
        boolean hasCccd = cccdFragment != null && !cccdFragment.trim().isEmpty();
        boolean hasPt = phuongThucChoice != null && !phuongThucChoice.trim().isEmpty()
            && !"Tất cả".equals(phuongThucChoice.trim());
        String ptNorm = hasPt ? phuongThucChoice.trim().toLowerCase() : "";

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("from DiemCong d where 1=1");
            if (hasCccd) hql.append(" and lower(d.cccd) like :q");
            if (hasPt) hql.append(" and lower(trim(d.phuongThuc)) = :pt");
            hql.append(" order by d.id");
            Query<DiemCong> query = session.createQuery(hql.toString(), DiemCong.class);
            if (hasCccd) query.setParameter("q", "%" + cccdFragment.trim().toLowerCase() + "%");
            if (hasPt) query.setParameter("pt", ptNorm);
            return query.list();
        }
    }

    /** Giữ tương thích: chỉ lọc CCCD, không lọc phương thức. */
    public List<DiemCong> findByCccdContaining(String fragment) {
        return findFiltered(fragment, "Tất cả");
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

    public DiemCong findById(Integer id) {
        if (id == null) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(DiemCong.class, id);
        }
    }

    public DiemCong findByDcKeys(String dcKeys) {
        if (dcKeys == null || dcKeys.trim().isEmpty()) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DiemCong d where d.dcKeys = :k", DiemCong.class)
                .setParameter("k", dcKeys.trim())
                .uniqueResult();
        }
    }

    public DiemCong findByCccdToHopAndMethod(String cccd, String tohop, String phuongThuc) {
        if (cccd == null || tohop == null || phuongThuc == null) return null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                "from DiemCong d where d.cccd = :cccd and d.maToHop = :tohop and lower(trim(d.phuongThuc)) = :pt", 
                DiemCong.class)
                .setParameter("cccd", cccd.trim())
                .setParameter("tohop", tohop.trim())
                .setParameter("pt", phuongThuc.trim().toLowerCase())
                .setMaxResults(1)
                .uniqueResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /** Thêm mới hoặc cập nhật theo {@code id} (null = insert). */
    public void saveOrUpdate(DiemCong fromForm) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            if (fromForm.getId() == null) {
                session.save(fromForm);
            } else {
                DiemCong managed = session.get(DiemCong.class, fromForm.getId());
                if (managed == null) {
                    throw new IllegalArgumentException("Không tìm thấy bản ghi id=" + fromForm.getId());
                }
                managed.setCccd(fromForm.getCccd());
                managed.setMaNganh(fromForm.getMaNganh());
                managed.setMaToHop(fromForm.getMaToHop());
                managed.setPhuongThuc(fromForm.getPhuongThuc());
                managed.setDiemCC(fromForm.getDiemCC());
                managed.setDiemThuong(fromForm.getDiemThuong());
                managed.setDiemUtxt(fromForm.getDiemUtxt());
                managed.setDiemTong(fromForm.getDiemTong());
                managed.setGhiChu(fromForm.getGhiChu());
                managed.setDcKeys(fromForm.getDcKeys());
            }
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw ex;
        }
    }
}
