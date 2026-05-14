package entity;

import javax.persistence.*;
import java.math.BigDecimal; // Import thư viện để xử lý kiểu dữ liệu Decimal

@Entity
@Table(name = "xt_bangquydoi")
public class BangQuyDoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idqd")
    private int idqd;

    @Column(name = "d_phuongthuc")
    private String phuongThuc;

    @Column(name = "d_tohop")
    private String toHop;

    @Column(name = "d_mon")
    private String mon;

    // Thay đổi từ Double sang BigDecimal để khớp với kiểu DECIMAL của MySQL
    @Column(name = "d_diema")
    private BigDecimal diemA;

    @Column(name = "d_diemb")
    private BigDecimal diemB;

    @Column(name = "d_diemc")
    private BigDecimal diemC;

    @Column(name = "d_diemd")
    private BigDecimal diemD;

    @Column(name = "d_maquydoi")
    private String maQuyDoi;

    @Column(name = "d_phanvi")
    private String phanVi;

    // Constructor không tham số bắt buộc của Hibernate
    public BangQuyDoi() {
    }

    // Getter và Setter
    public int getIdqd() { 
        return idqd; 
    }
    
    public void setIdqd(int idqd) { 
        this.idqd = idqd; 
    }

    public String getPhuongThuc() { 
        return phuongThuc; 
    }
    
    public void setPhuongThuc(String phuongThuc) { 
        this.phuongThuc = phuongThuc; 
    }

    public String getToHop() { 
        return toHop; 
    }
    
    public void setToHop(String toHop) { 
        this.toHop = toHop; 
    }

    public String getMon() { 
        return mon; 
    }
    
    public void setMon(String mon) { 
        this.mon = mon; 
    }

    public BigDecimal getDiemA() { 
        return diemA; 
    }
    
    public void setDiemA(BigDecimal diemA) { 
        this.diemA = diemA; 
    }

    public BigDecimal getDiemB() { 
        return diemB; 
    }
    
    public void setDiemB(BigDecimal diemB) { 
        this.diemB = diemB; 
    }

    public BigDecimal getDiemC() { 
        return diemC; 
    }
    
    public void setDiemC(BigDecimal diemC) { 
        this.diemC = diemC; 
    }

    public BigDecimal getDiemD() { 
        return diemD; 
    }
    
    public void setDiemD(BigDecimal diemD) { 
        this.diemD = diemD; 
    }

    public String getMaQuyDoi() { 
        return maQuyDoi; 
    }
    
    public void setMaQuyDoi(String maQuyDoi) { 
        this.maQuyDoi = maQuyDoi; 
    }

    public String getPhanVi() { 
        return phanVi; 
    }
    
    public void setPhanVi(String phanVi) { 
        this.phanVi = phanVi; 
    }
}