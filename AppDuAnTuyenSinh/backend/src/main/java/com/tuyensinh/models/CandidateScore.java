package com.tuyensinh.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "xt_diemthixettuyen")
public class CandidateScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iddiemthi;
    
    @Column(unique = true)
    private String cccd;
    private String sobaodanh;
    private String d_phuongthuc;
    
    @Column(name = "TO_SCORE") private double to; 
    @Column(name = "LI") private double li; 
    @Column(name = "HO") private double ho; 
    @Column(name = "SI") private double si; 
    @Column(name = "SU") private double su; 
    @Column(name = "DI") private double di; 
    @Column(name = "VA") private double va; 
    @Column(name = "N1_THI") private double n1_thi; 
    @Column(name = "N1_CC") private double n1_cc; 
    @Column(name = "CNCN") private double cncn; 
    @Column(name = "CNNN") private double cnnn; 
    @Column(name = "TI") private double ti; 
    @Column(name = "KTPL") private double ktpl; 
    @Column(name = "NL1") private double nl1; 
    @Column(name = "NK1") private double nk1; 
    @Column(name = "NK2") private double nk2;

    public CandidateScore() {}

    public CandidateScore(int iddiemthi, String cccd, String sobaodanh, String d_phuongthuc, 
                          double to, double li, double ho, double si, double su, double di, 
                          double va, double n1_thi, double n1_cc, double cncn, double cnnn, 
                          double ti, double ktpl, double nl1, double nk1, double nk2) {
        this.iddiemthi = iddiemthi; this.cccd = cccd; this.sobaodanh = sobaodanh; this.d_phuongthuc = d_phuongthuc;
        this.to = to; this.li = li; this.ho = ho; this.si = si; this.su = su; this.di = di; this.va = va;
        this.n1_thi = n1_thi; this.n1_cc = n1_cc; this.cncn = cncn; this.cnnn = cnnn; this.ti = ti; 
        this.ktpl = ktpl; this.nl1 = nl1; this.nk1 = nk1; this.nk2 = nk2;
    }

    public int getIddiemthi() { return iddiemthi; }
    public void setIddiemthi(int iddiemthi) { this.iddiemthi = iddiemthi; }
    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }
    public String getSobaodanh() { return sobaodanh; }
    public void setSobaodanh(String sobaodanh) { this.sobaodanh = sobaodanh; }
    public String getD_phuongthuc() { return d_phuongthuc; }
    public void setD_phuongthuc(String d_phuongthuc) { this.d_phuongthuc = d_phuongthuc; }
    public double getTo() { return to; }
    public void setTo(double to) { this.to = to; }
    public double getLi() { return li; }
    public void setLi(double li) { this.li = li; }
    public double getHo() { return ho; }
    public void setHo(double ho) { this.ho = ho; }
    public double getSi() { return si; }
    public void setSi(double si) { this.si = si; }
    public double getSu() { return su; }
    public void setSu(double su) { this.su = su; }
    public double getDi() { return di; }
    public void setDi(double di) { this.di = di; }
    public double getVa() { return va; }
    public void setVa(double va) { this.va = va; }
    public double getN1_thi() { return n1_thi; }
    public void setN1_thi(double n1_thi) { this.n1_thi = n1_thi; }
    public double getN1_cc() { return n1_cc; }
    public void setN1_cc(double n1_cc) { this.n1_cc = n1_cc; }
    public double getCncn() { return cncn; }
    public void setCncn(double cncn) { this.cncn = cncn; }
    public double getCnnn() { return cnnn; }
    public void setCnnn(double cnnn) { this.cnnn = cnnn; }
    public double getTi() { return ti; }
    public void setTi(double ti) { this.ti = ti; }
    public double getKtpl() { return ktpl; }
    public void setKtpl(double ktpl) { this.ktpl = ktpl; }
    public double getNl1() { return nl1; }
    public void setNl1(double nl1) { this.nl1 = nl1; }
    public double getNk1() { return nk1; }
    public void setNk1(double nk1) { this.nk1 = nk1; }
    public double getNk2() { return nk2; }
    public void setNk2(double nk2) { this.nk2 = nk2; }
}
