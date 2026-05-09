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

    @Column(name = "`TO`")
    private Double to;
    @Column(name = "`LI`")
    private Double li;
    @Column(name = "`HO`")
    private Double ho;
    @Column(name = "`SI`")
    private Double si;
    @Column(name = "`SU`")
    private Double su;
    @Column(name = "`DI`")
    private Double di;
    @Column(name = "`VA`")
    private Double va;
    @Column(name = "N1_THI")
    private Double n1_thi;
    @Column(name = "N1_CC")
    private Double n1_cc;
    @Column(name = "CNCN")
    private Double cncn;
    @Column(name = "CNNN")
    private Double cnnn;
    @Column(name = "TI")
    private Double ti;
    @Column(name = "KTPL")
    private Double ktpl;
    @Column(name = "NL1")
    private Double nl1;
    @Column(name = "NK1")
    private Double nk1;
    @Column(name = "NK2")
    private Double nk2;

    public CandidateScore() {
    }

    public CandidateScore(String cccd, String sobaodanh, String d_phuongthuc,
            Double to, Double li, Double ho, Double si, Double su, Double di,
            Double va, Double n1_thi, Double n1_cc, Double cncn, Double cnnn,
            Double ti, Double ktpl, Double nl1, Double nk1, Double nk2) {
        this.cccd = cccd;
        this.sobaodanh = sobaodanh;
        this.d_phuongthuc = d_phuongthuc;
        this.to = to;
        this.li = li;
        this.ho = ho;
        this.si = si;
        this.su = su;
        this.di = di;
        this.va = va;
        this.n1_thi = n1_thi;
        this.n1_cc = n1_cc;
        this.cncn = cncn;
        this.cnnn = cnnn;
        this.ti = ti;
        this.ktpl = ktpl;
        this.nl1 = nl1;
        this.nk1 = nk1;
        this.nk2 = nk2;
    }

    public int getIddiemthi() {
        return iddiemthi;
    }

    public void setIddiemthi(int iddiemthi) {
        this.iddiemthi = iddiemthi;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getSobaodanh() {
        return sobaodanh;
    }

    public void setSobaodanh(String sobaodanh) {
        this.sobaodanh = sobaodanh;
    }

    public String getD_phuongthuc() {
        return d_phuongthuc;
    }

    public void setD_phuongthuc(String d_phuongthuc) {
        this.d_phuongthuc = d_phuongthuc;
    }

    public Double getTo() {
        return to;
    }

    public void setTo(Double to) {
        this.to = to;
    }

    public Double getLi() {
        return li;
    }

    public void setLi(Double li) {
        this.li = li;
    }

    public Double getHo() {
        return ho;
    }

    public void setHo(Double ho) {
        this.ho = ho;
    }

    public Double getSi() {
        return si;
    }

    public void setSi(Double si) {
        this.si = si;
    }

    public Double getSu() {
        return su;
    }

    public void setSu(Double su) {
        this.su = su;
    }

    public Double getDi() {
        return di;
    }

    public void setDi(Double di) {
        this.di = di;
    }

    public Double getVa() {
        return va;
    }

    public void setVa(Double va) {
        this.va = va;
    }

    public Double getN1_thi() {
        return n1_thi;
    }

    public void setN1_thi(Double n1_thi) {
        this.n1_thi = n1_thi;
    }

    public Double getN1_cc() {
        return n1_cc;
    }

    public void setN1_cc(Double n1_cc) {
        this.n1_cc = n1_cc;
    }

    public Double getCncn() {
        return cncn;
    }

    public void setCncn(Double cncn) {
        this.cncn = cncn;
    }

    public Double getCnnn() {
        return cnnn;
    }

    public void setCnnn(Double cnnn) {
        this.cnnn = cnnn;
    }

    public Double getTi() {
        return ti;
    }

    public void setTi(Double ti) {
        this.ti = ti;
    }

    public Double getKtpl() {
        return ktpl;
    }

    public void setKtpl(Double ktpl) {
        this.ktpl = ktpl;
    }

    public Double getNl1() {
        return nl1;
    }

    public void setNl1(Double nl1) {
        this.nl1 = nl1;
    }

    public Double getNk1() {
        return nk1;
    }

    public void setNk1(Double nk1) {
        this.nk1 = nk1;
    }

    public Double getNk2() {
        return nk2;
    }

    public void setNk2(Double nk2) {
        this.nk2 = nk2;
    }
}
