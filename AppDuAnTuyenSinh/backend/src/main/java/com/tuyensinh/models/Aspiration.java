package com.tuyensinh.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "xt_nguyenvongxettuyen")
public class Aspiration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idnv;
    private String nn_cccd;
    private String nv_manganh;
    private int nv_tt;
    private double diem_thxt;
    private double diem_utqd;
    private double diem_cong;
    private double diem_xettuyen;
    private String nv_ketqua;
    
    @Column(unique = true)
    private String nv_keys;
    private String tt_phuongthuc;
    private String tt_thm;

    public Aspiration() {}

    public Aspiration(int idnv, String nn_cccd, String nv_manganh, int nv_tt, double diem_thxt, 
                      double diem_utqd, double diem_cong, double diem_xettuyen, String nv_ketqua, 
                      String nv_keys, String tt_phuongthuc, String tt_thm) {
        this.idnv = idnv; this.nn_cccd = nn_cccd; this.nv_manganh = nv_manganh; this.nv_tt = nv_tt;
        this.diem_thxt = diem_thxt; this.diem_utqd = diem_utqd; this.diem_cong = diem_cong; 
        this.diem_xettuyen = diem_xettuyen; this.nv_ketqua = nv_ketqua; this.nv_keys = nv_keys; 
        this.tt_phuongthuc = tt_phuongthuc; this.tt_thm = tt_thm;
    }

    public int getIdnv() { return idnv; }
    public void setIdnv(int idnv) { this.idnv = idnv; }
    public String getNn_cccd() { return nn_cccd; }
    public void setNn_cccd(String nn_cccd) { this.nn_cccd = nn_cccd; }
    public String getNv_manganh() { return nv_manganh; }
    public void setNv_manganh(String nv_manganh) { this.nv_manganh = nv_manganh; }
    public int getNv_tt() { return nv_tt; }
    public void setNv_tt(int nv_tt) { this.nv_tt = nv_tt; }
    public double getDiem_thxt() { return diem_thxt; }
    public void setDiem_thxt(double diem_thxt) { this.diem_thxt = diem_thxt; }
    public double getDiem_utqd() { return diem_utqd; }
    public void setDiem_utqd(double diem_utqd) { this.diem_utqd = diem_utqd; }
    public double getDiem_cong() { return diem_cong; }
    public void setDiem_cong(double diem_cong) { this.diem_cong = diem_cong; }
    public double getDiem_xettuyen() { return diem_xettuyen; }
    public void setDiem_xettuyen(double diem_xettuyen) { this.diem_xettuyen = diem_xettuyen; }
    public String getNv_ketqua() { return nv_ketqua; }
    public void setNv_ketqua(String nv_ketqua) { this.nv_ketqua = nv_ketqua; }
    public String getNv_keys() { return nv_keys; }
    public void setNv_keys(String nv_keys) { this.nv_keys = nv_keys; }
    public String getTt_phuongthuc() { return tt_phuongthuc; }
    public void setTt_phuongthuc(String tt_phuongthuc) { this.tt_phuongthuc = tt_phuongthuc; }
    public String getTt_thm() { return tt_thm; }
    public void setTt_thm(String tt_thm) { this.tt_thm = tt_thm; }
}
