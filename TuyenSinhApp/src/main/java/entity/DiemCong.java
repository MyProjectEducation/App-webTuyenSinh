package entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "xt_diemcongxetuyen")
public class DiemCong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddiemcong")
    private Integer id;

    @Column(name = "ts_cccd", nullable = false, length = 45)
    private String cccd;

    @Column(name = "manganh", length = 20)
    private String maNganh;

    @Column(name = "matohop", length = 10)
    private String maToHop;

    @Column(name = "phuongthuc", length = 45)
    private String phuongThuc;

    @Column(name = "diemCC", precision = 6, scale = 2)
    private BigDecimal diemCC;

    @Column(name = "diemThuong", precision = 6, scale = 2)
    private BigDecimal diemThuong;

    @Column(name = "diemUtxt", precision = 6, scale = 2)
    private BigDecimal diemUtxt;

    @Column(name = "diemTong", precision = 6, scale = 2)
    private BigDecimal diemTong;

    @Column(name = "ghichu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "dc_keys", nullable = false, length = 45, unique = true)
    private String dcKeys;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(String maNganh) {
        this.maNganh = maNganh;
    }

    public String getMaToHop() {
        return maToHop;
    }

    public void setMaToHop(String maToHop) {
        this.maToHop = maToHop;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public BigDecimal getDiemCC() {
        return diemCC;
    }

    public void setDiemCC(BigDecimal diemCC) {
        this.diemCC = diemCC;
    }

    public BigDecimal getDiemThuong() {
        return diemThuong;
    }

    public void setDiemThuong(BigDecimal diemThuong) {
        this.diemThuong = diemThuong;
    }

    public BigDecimal getDiemUtxt() {
        return diemUtxt;
    }

    public void setDiemUtxt(BigDecimal diemUtxt) {
        this.diemUtxt = diemUtxt;
    }

    public BigDecimal getDiemTong() {
        return diemTong;
    }

    public void setDiemTong(BigDecimal diemTong) {
        this.diemTong = diemTong;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getDcKeys() {
        return dcKeys;
    }

    public void setDcKeys(String dcKeys) {
        this.dcKeys = dcKeys;
    }
}
