package com.tuyensinh.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "xt_diemcongxetuyen")
public class BonusPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iddiemcong;
    private String ts_cccd;
    private String manganh;
    private String matohop;
    private String phuongthuc;
    private double diemCC;
    private double diemUtxt;
    private double diemTong;
    private String ghichu;
    
    @Column(unique = true)
    private String dc_keys;

    public BonusPoint() {}

    public BonusPoint() {}

    public BonusPoint(int iddiemcong, String ts_cccd, String manganh, String matohop, 
                      String phuongthuc, double diemCC, double diemUtxt, double diemTong, 
                      String ghichu, String dc_keys) {
        this.iddiemcong = iddiemcong; this.ts_cccd = ts_cccd; this.manganh = manganh;
        this.matohop = matohop; this.phuongthuc = phuongthuc; this.diemCC = diemCC;
        this.diemUtxt = diemUtxt; this.diemTong = diemTong; this.ghichu = ghichu; this.dc_keys = dc_keys;
    }

    public int getIddiemcong() { return iddiemcong; }
    public void setIddiemcong(int iddiemcong) { this.iddiemcong = iddiemcong; }
    public String getTs_cccd() { return ts_cccd; }
    public void setTs_cccd(String ts_cccd) { this.ts_cccd = ts_cccd; }
    public String getManganh() { return manganh; }
    public void setManganh(String manganh) { this.manganh = manganh; }
    public String getMatohop() { return matohop; }
    public void setMatohop(String matohop) { this.matohop = matohop; }
    public String getPhuongthuc() { return phuongthuc; }
    public void setPhuongthuc(String phuongthuc) { this.phuongthuc = phuongthuc; }
    public double getDiemCC() { return diemCC; }
    public void setDiemCC(double diemCC) { this.diemCC = diemCC; }
    public double getDiemUtxt() { return diemUtxt; }
    public void setDiemUtxt(double diemUtxt) { this.diemUtxt = diemUtxt; }
    public double getDiemTong() { return diemTong; }
    public void setDiemTong(double diemTong) { this.diemTong = diemTong; }
    public String getGhichu() { return ghichu; }
    public void setGhichu(String ghichu) { this.ghichu = ghichu; }
    public String getDc_keys() { return dc_keys; }
    public void setDc_keys(String dc_keys) { this.dc_keys = dc_keys; }
}
