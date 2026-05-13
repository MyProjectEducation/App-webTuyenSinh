package entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "xt_nganh")
public class Nganh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnganh")
    private Integer id;

    @Column(name = "manganh", nullable = false)
    private String maNganh;

    @Column(name = "tennganh", nullable = false)
    private String tenNganh;

    @Column(name = "n_tohopgoc", length = 3)
    private String toHopGoc;

    @Column(name = "n_chitieu")
    private Integer chiTieu;

    @Column(name = "n_diemsan", precision = 10, scale = 2)
    private BigDecimal diemSan;

    @Column(name = "n_diemtrungtuyen", precision = 10, scale = 2)
    private BigDecimal diemTrungTuyen;

    @Column(name = "n_tuyenthang")
    private String tuyenThang;

    @Column(name = "n_dgnl")
    private String dgnl;

    @Column(name = "n_thpt")
    private String thpt;

    @Column(name = "n_vsat")
    private String vsat;

    @Column(name = "sl_xtt")
    private Integer slXtt;

    @Column(name = "sl_dgnl")
    private Integer slDgnl;

    @Column(name = "sl_vsat")
    private Integer slVsat;

    @Column(name = "sl_thpt")
    private String slThpt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMaNganh() {
        return maNganh;
    }

    public void setMaNganh(String maNganh) {
        this.maNganh = maNganh;
    }

    public String getTenNganh() {
        return tenNganh;
    }

    public void setTenNganh(String tenNganh) {
        this.tenNganh = tenNganh;
    }

    public String getToHopGoc() {
        return toHopGoc;
    }

    public void setToHopGoc(String toHopGoc) {
        this.toHopGoc = toHopGoc;
    }

    public Integer getChiTieu() {
        return chiTieu;
    }

    public void setChiTieu(Integer chiTieu) {
        this.chiTieu = chiTieu;
    }

    public BigDecimal getDiemSan() {
        return diemSan;
    }

    public void setDiemSan(BigDecimal diemSan) {
        this.diemSan = diemSan;
    }

    public BigDecimal getDiemTrungTuyen() {
        return diemTrungTuyen;
    }

    public void setDiemTrungTuyen(BigDecimal diemTrungTuyen) {
        this.diemTrungTuyen = diemTrungTuyen;
    }

    public String getTuyenThang() {
        return tuyenThang;
    }

    public void setTuyenThang(String tuyenThang) {
        this.tuyenThang = tuyenThang;
    }

    public String getDgnl() {
        return dgnl;
    }

    public void setDgnl(String dgnl) {
        this.dgnl = dgnl;
    }

    public String getThpt() {
        return thpt;
    }

    public void setThpt(String thpt) {
        this.thpt = thpt;
    }

    public String getVsat() {
        return vsat;
    }

    public void setVsat(String vsat) {
        this.vsat = vsat;
    }

    public Integer getSlXtt() {
        return slXtt;
    }

    public void setSlXtt(Integer slXtt) {
        this.slXtt = slXtt;
    }

    public Integer getSlDgnl() {
        return slDgnl;
    }

    public void setSlDgnl(Integer slDgnl) {
        this.slDgnl = slDgnl;
    }

    public Integer getSlVsat() {
        return slVsat;
    }

    public void setSlVsat(Integer slVsat) {
        this.slVsat = slVsat;
    }

    public String getSlThpt() {
        return slThpt;
    }

    public void setSlThpt(String slThpt) {
        this.slThpt = slThpt;
    }
}
