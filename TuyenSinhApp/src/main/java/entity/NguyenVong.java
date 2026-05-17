package entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "xt_nguyenvongxettuyen")
public class NguyenVong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnv")
    private int idnv;

    @Column(name = "nn_cccd")
    private String nnCccd;

    @Column(name = "nv_manganh")
    private String nvManganh;

    @Column(name = "nv_tt")
    private int nvTt;

    @Column(name = "diem_thxt")
    private BigDecimal diemThxt;

    @Column(name = "diem_utqd")
    private BigDecimal diemUtqd;

    @Column(name = "diem_cong")
    private BigDecimal diemCong;

    @Column(name = "diem_xettuyen")
    private BigDecimal diemXettuyen;

    @Column(name = "nv_ketqua")
    private String nvKetqua;

    @Column(name = "nv_keys")
    private String nvKeys;

    @Column(name = "tt_phuongthuc")
    private String ttPhuongthuc;

    @Column(name = "tt_thm")
    private String ttThm;

    public NguyenVong() {}

    // Getters and Setters
    public int getIdnv() { return idnv; }
    public void setIdnv(int idnv) { this.idnv = idnv; }
    
    public String getNnCccd() { return nnCccd; }
    public void setNnCccd(String nnCccd) { this.nnCccd = nnCccd; }
    
    public String getNvManganh() { return nvManganh; }
    public void setNvManganh(String nvManganh) { this.nvManganh = nvManganh; }
    
    public int getNvTt() { return nvTt; }
    public void setNvTt(int nvTt) { this.nvTt = nvTt; }
    
    public BigDecimal getDiemThxt() { return diemThxt; }
    public void setDiemThxt(BigDecimal diemThxt) { this.diemThxt = diemThxt; }
    
    public BigDecimal getDiemUtqd() { return diemUtqd; }
    public void setDiemUtqd(BigDecimal diemUtqd) { this.diemUtqd = diemUtqd; }
    
    public BigDecimal getDiemCong() { return diemCong; }
    
    // ĐÃ SỬA LỖI 1 & LỖI 2 tại đây: Cú pháp hàm setter chuẩn hóa
    public void setDiemCong(BigDecimal diemCong) { 
        this.diemCong = diemCong; 
    }
    
    public BigDecimal getDiemXettuyen() { return diemXettuyen; }
    public void setDiemXettuyen(BigDecimal diemXettuyen) { this.diemXettuyen = diemXettuyen; }
    
    public String getNvKetqua() { return nvKetqua; }
    public void setNvKetqua(String nvKetqua) { this.nvKetqua = nvKetqua; }
    
    public String getNvKeys() { return nvKeys; }
    public void setNvKeys(String nvKeys) { this.nvKeys = nvKeys; }
    
    public String getTtPhuongthuc() { return ttPhuongthuc; }
    public void setTtPhuongthuc(String ttPhuongthuc) { this.ttPhuongthuc = ttPhuongthuc; }
    
    public String getTtThm() { return ttThm; }
    public void setTtThm(String ttThm) { this.ttThm = ttThm; }
}