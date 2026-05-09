package com.tuyensinh.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "xt_nganh_tohop")
public class ProgramCombination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String manganh;
    private String matohop;
    private String th_mon1;
    private int hsmon1;
    private String th_mon2;
    private int hsmon2;
    private String th_mon3;
    private int hsmon3;

    @Column(unique = true)
    private String tb_keys;

    @Column(name = "N1")
    private int n1;
    @Column(name = "TO")
    private int to;
    @Column(name = "LI")
    private int li;
    @Column(name = "HO")
    private int ho;
    @Column(name = "SI")
    private int si;
    @Column(name = "VA")
    private int va;
    @Column(name = "SU")
    private int su;
    @Column(name = "DI")
    private int di;
    @Column(name = "TI")
    private int ti;
    @Column(name = "KHAC")
    private int khac;
    @Column(name = "KTPL")
    private int ktpl;

    private double dolech;

    public ProgramCombination() {
    }

    public ProgramCombination(int id, String manganh, String matohop, String th_mon1, int hsmon1,
            String th_mon2, int hsmon2, String th_mon3, int hsmon3, String tb_keys,
            int n1, int to, int li, int ho, int si, int va, int su, int di,
            int ti, int khac, int ktpl, double dolech) {
        this.id = id;
        this.manganh = manganh;
        this.matohop = matohop;
        this.th_mon1 = th_mon1;
        this.hsmon1 = hsmon1;
        this.th_mon2 = th_mon2;
        this.hsmon2 = hsmon2;
        this.th_mon3 = th_mon3;
        this.hsmon3 = hsmon3;
        this.tb_keys = tb_keys;
        this.n1 = n1;
        this.to = to;
        this.li = li;
        this.ho = ho;
        this.si = si;
        this.va = va;
        this.su = su;
        this.di = di;
        this.ti = ti;
        this.khac = khac;
        this.ktpl = ktpl;
        this.dolech = dolech;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getManganh() {
        return manganh;
    }

    public void setManganh(String manganh) {
        this.manganh = manganh;
    }

    public String getMatohop() {
        return matohop;
    }

    public void setMatohop(String matohop) {
        this.matohop = matohop;
    }

    public String getTh_mon1() {
        return th_mon1;
    }

    public void setTh_mon1(String th_mon1) {
        this.th_mon1 = th_mon1;
    }

    public int getHsmon1() {
        return hsmon1;
    }

    public void setHsmon1(int hsmon1) {
        this.hsmon1 = hsmon1;
    }

    public String getTh_mon2() {
        return th_mon2;
    }

    public void setTh_mon2(String th_mon2) {
        this.th_mon2 = th_mon2;
    }

    public int getHsmon2() {
        return hsmon2;
    }

    public void setHsmon2(int hsmon2) {
        this.hsmon2 = hsmon2;
    }

    public String getTh_mon3() {
        return th_mon3;
    }

    public void setTh_mon3(String th_mon3) {
        this.th_mon3 = th_mon3;
    }

    public int getHsmon3() {
        return hsmon3;
    }

    public void setHsmon3(int hsmon3) {
        this.hsmon3 = hsmon3;
    }

    public String getTb_keys() {
        return tb_keys;
    }

    public void setTb_keys(String tb_keys) {
        this.tb_keys = tb_keys;
    }

    public int getN1() {
        return n1;
    }

    public void setN1(int n1) {
        this.n1 = n1;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getLi() {
        return li;
    }

    public void setLi(int li) {
        this.li = li;
    }

    public int getHo() {
        return ho;
    }

    public void setHo(int ho) {
        this.ho = ho;
    }

    public int getSi() {
        return si;
    }

    public void setSi(int si) {
        this.si = si;
    }

    public int getVa() {
        return va;
    }

    public void setVa(int va) {
        this.va = va;
    }

    public int getSu() {
        return su;
    }

    public void setSu(int su) {
        this.su = su;
    }

    public int getDi() {
        return di;
    }

    public void setDi(int di) {
        this.di = di;
    }

    public int getTi() {
        return ti;
    }

    public void setTi(int ti) {
        this.ti = ti;
    }

    public int getKhac() {
        return khac;
    }

    public void setKhac(int khac) {
        this.khac = khac;
    }

    public int getKtpl() {
        return ktpl;
    }

    public void setKtpl(int ktpl) {
        this.ktpl = ktpl;
    }

    public double getDolech() {
        return dolech;
    }

    public void setDolech(double dolech) {
        this.dolech = dolech;
    }
}
