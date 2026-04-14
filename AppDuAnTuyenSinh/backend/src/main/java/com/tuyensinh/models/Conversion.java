package com.tuyensinh.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "xt_bangquydoi")
public class Conversion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idqd;
    private String d_phuongthuc;
    private String d_tohop;
    private String d_mon;
    private double d_diema, d_diemb, d_diemc, d_diemd;
    
    @Column(unique = true)
    private String d_maquydoi;
    private String d_phanvi;

    public Conversion() {}

    public Conversion(int idqd, String d_phuongthuc, String d_tohop, String d_mon, 
                      double d_diema, double d_diemb, double d_diemc, double d_diemd, 
                      String d_maquydoi, String d_phanvi) {
        this.idqd = idqd; this.d_phuongthuc = d_phuongthuc; this.d_tohop = d_tohop; 
        this.d_mon = d_mon; this.d_diema = d_diema; this.d_diemb = d_diemb; 
        this.d_diemc = d_diemc; this.d_diemd = d_diemd; this.d_maquydoi = d_maquydoi; 
        this.d_phanvi = d_phanvi;
    }

    public int getIdqd() { return idqd; }
    public void setIdqd(int idqd) { this.idqd = idqd; }
    public String getD_phuongthuc() { return d_phuongthuc; }
    public void setD_phuongthuc(String d_phuongthuc) { this.d_phuongthuc = d_phuongthuc; }
    public String getD_tohop() { return d_tohop; }
    public void setD_tohop(String d_tohop) { this.d_tohop = d_tohop; }
    public String getD_mon() { return d_mon; }
    public void setD_mon(String d_mon) { this.d_mon = d_mon; }
    public double getD_diema() { return d_diema; }
    public void setD_diema(double d_diema) { this.d_diema = d_diema; }
    public double getD_diemb() { return d_diemb; }
    public void setD_diemb(double d_diemb) { this.d_diemb = d_diemb; }
    public double getD_diemc() { return d_diemc; }
    public void setD_diemc(double d_diemc) { this.d_diemc = d_diemc; }
    public double getD_diemd() { return d_diemd; }
    public void setD_diemd(double d_diemd) { this.d_diemd = d_diemd; }
    public String getD_maquydoi() { return d_maquydoi; }
    public void setD_maquydoi(String d_maquydoi) { this.d_maquydoi = d_maquydoi; }
    public String getD_phanvi() { return d_phanvi; }
    public void setD_phanvi(String d_phanvi) { this.d_phanvi = d_phanvi; }
}
