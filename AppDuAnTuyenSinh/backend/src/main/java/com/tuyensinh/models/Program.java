package com.tuyensinh.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "xt_nganh")
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idnganh;
    
    private String manganh;
    private String tennganh;
    
    @Column(name = "n_tohopgoc")
    private String nToHopGoc;
    
    @Column(name = "n_chitieu")
    private int nChiTieu;
    
    @Column(name = "n_diemsan")
    private double nDiemSan;
    
    @Column(name = "n_diemtrungtuyen")
    private double nDiemTrungTuyen;
    
    @Column(name = "n_tuyenthang")
    private String nTuyenThang;
    
    @Column(name = "n_dgnl")
    private String nDgnl;
    
    @Column(name = "n_thpt")
    private String nThpt;
    
    @Column(name = "n_vsat")
    private String nVsat;
    
    @Column(name = "sl_xtt")
    private int slXtt;
    
    @Column(name = "sl_dgnl")
    private int slDgnl;
    
    @Column(name = "sl_vsat")
    private int slVsat;
    
    @Column(name = "sl_thpt")
    private String slThpt;

    public Program() {}

    public Program(int idnganh, String manganh, String tennganh, String nToHopGoc, int nChiTieu, double nDiemSan, double nDiemTrungTuyen) {
        this.idnganh = idnganh;
        this.manganh = manganh;
        this.tennganh = tennganh;
        this.nToHopGoc = nToHopGoc;
        this.nChiTieu = nChiTieu;
        this.nDiemSan = nDiemSan;
        this.nDiemTrungTuyen = nDiemTrungTuyen;
    }

    // Getters / Setters
    public int getIdnganh() { return idnganh; }
    public void setIdnganh(int idnganh) { this.idnganh = idnganh; }

    public String getManganh() { return manganh; }
    public void setManganh(String manganh) { this.manganh = manganh; }

    public String getTennganh() { return tennganh; }
    public void setTennganh(String tennganh) { this.tennganh = tennganh; }

    public String getnToHopGoc() { return nToHopGoc; }
    public void setnToHopGoc(String nToHopGoc) { this.nToHopGoc = nToHopGoc; }

    public int getnChiTieu() { return nChiTieu; }
    public void setnChiTieu(int nChiTieu) { this.nChiTieu = nChiTieu; }

    public double getnDiemSan() { return nDiemSan; }
    public void setnDiemSan(double nDiemSan) { this.nDiemSan = nDiemSan; }

    public double getnDiemTrungTuyen() { return nDiemTrungTuyen; }
    public void setnDiemTrungTuyen(double nDiemTrungTuyen) { this.nDiemTrungTuyen = nDiemTrungTuyen; }

    public String getnTuyenThang() { return nTuyenThang; }
    public void setnTuyenThang(String nTuyenThang) { this.nTuyenThang = nTuyenThang; }

    public String getnDgnl() { return nDgnl; }
    public void setnDgnl(String nDgnl) { this.nDgnl = nDgnl; }

    public String getnThpt() { return nThpt; }
    public void setnThpt(String nThpt) { this.nThpt = nThpt; }

    public String getnVsat() { return nVsat; }
    public void setnVsat(String nVsat) { this.nVsat = nVsat; }

    public int getSlXtt() { return slXtt; }
    public void setSlXtt(int slXtt) { this.slXtt = slXtt; }

    public int getSlDgnl() { return slDgnl; }
    public void setSlDgnl(int slDgnl) { this.slDgnl = slDgnl; }

    public int getSlVsat() { return slVsat; }
    public void setSlVsat(int slVsat) { this.slVsat = slVsat; }

    public String getSlThpt() { return slThpt; }
    public void setSlThpt(String slThpt) { this.slThpt = slThpt; }
}
