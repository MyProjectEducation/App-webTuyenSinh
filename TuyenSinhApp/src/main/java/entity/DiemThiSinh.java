package entity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "xt_diemthixettuyen")
public class DiemThiSinh {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iddiemthi;

    @Column(unique = true)
    private String cccd;
    private String sobaodanh;
    private String d_phuongthuc;

    @Column(name = "`TO`")
    private BigDecimal to;
    @Column(name = "`LI`")
    private BigDecimal li;
    @Column(name = "`HO`")
    private BigDecimal ho;
    @Column(name = "`SI`")
    private BigDecimal si;
    @Column(name = "`SU`")
    private BigDecimal su;
    @Column(name = "`DI`")
    private BigDecimal di;
    @Column(name = "`VA`")
    private BigDecimal va;
    @Column(name = "N1_THI")
    private BigDecimal n1_thi;
    @Column(name = "N1_CC")
    private BigDecimal n1_cc;
    @Column(name = "CNCN")
    private BigDecimal cncn;
    @Column(name = "CNNN")
    private BigDecimal cnnn;
    @Column(name = "TI")
    private BigDecimal ti;
    @Column(name = "KTPL")
    private BigDecimal ktpl;
    @Column(name = "NK1")
    private BigDecimal nk1;
    @Column(name = "NK2")
    private BigDecimal nk2;
    @Column(name = "M1")
    private BigDecimal TO_NL;
    @Column(name = "M2")
    private BigDecimal LI_NL;
    @Column(name = "M3")
    private BigDecimal HO_NL;
    @Column(name = "M4")
    private BigDecimal VA_NL;
    @Column(name = "M5")
    private BigDecimal SI_NL;
    @Column(name = "M6")
    private BigDecimal SU_NL;
    @Column(name = "M7")
    private BigDecimal DI_NL;
    @Column(name = "M8")
    private BigDecimal N1_NL;
    @Column(name = "TO_VS")
    private BigDecimal TO_VS;
    @Column(name = "LI_VS")
    private BigDecimal LI_VS;
    @Column(name = "HO_VS")
    private BigDecimal HO_VS;
    @Column(name = "VA_VS")
    private BigDecimal VA_VS;
    @Column(name = "SI_VS")
    private BigDecimal SI_VS;
    @Column(name = "SU_VS")
    private BigDecimal SU_VS;
    @Column(name = "DI_VS")
    private BigDecimal DI_VS;
    @Column(name = "N1_VS")
    private BigDecimal N1_VS;
    @Column(name = "nl1")
    private BigDecimal nl1;
    @Column(name = "nl2")
    private BigDecimal nl2;

    public DiemThiSinh() {
    }

    public DiemThiSinh(String cccd, String sobaodanh, String d_phuongthuc,
            BigDecimal to, BigDecimal li, BigDecimal ho, BigDecimal si, BigDecimal su, BigDecimal di,
            BigDecimal va, BigDecimal n1_thi, BigDecimal n1_cc, BigDecimal cncn, BigDecimal cnnn,
            BigDecimal ti, BigDecimal ktpl, BigDecimal nk1, BigDecimal nk2) {
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

    public BigDecimal getTo() {
        return to;
    }

    public void setTo(BigDecimal to) {
        this.to = to;
    }

    public BigDecimal getLi() {
        return li;
    }

    public void setLi(BigDecimal li) {
        this.li = li;
    }

    public BigDecimal getHo() {
        return ho;
    }

    public void setHo(BigDecimal ho) {
        this.ho = ho;
    }

    public BigDecimal getSi() {
        return si;
    }

    public void setSi(BigDecimal si) {
        this.si = si;
    }

    public BigDecimal getSu() {
        return su;
    }

    public void setSu(BigDecimal su) {
        this.su = su;
    }

    public BigDecimal getDi() {
        return di;
    }

    public void setDi(BigDecimal di) {
        this.di = di;
    }

    public BigDecimal getVa() {
        return va;
    }

    public void setVa(BigDecimal va) {
        this.va = va;
    }

    public BigDecimal getN1_thi() {
        return n1_thi;
    }

    public void setN1_thi(BigDecimal n1_thi) {
        this.n1_thi = n1_thi;
    }

    public BigDecimal getN1_cc() {
        return n1_cc;
    }

    public void setN1_cc(BigDecimal n1_cc) {
        this.n1_cc = n1_cc;
    }

    public BigDecimal getCncn() {
        return cncn;
    }

    public void setCncn(BigDecimal cncn) {
        this.cncn = cncn;
    }

    public BigDecimal getCnnn() {
        return cnnn;
    }

    public void setCnnn(BigDecimal cnnn) {
        this.cnnn = cnnn;
    }

    public BigDecimal getTi() {
        return ti;
    }

    public void setTi(BigDecimal ti) {
        this.ti = ti;
    }

    public BigDecimal getKtpl() {
        return ktpl;
    }

    public void setKtpl(BigDecimal ktpl) {
        this.ktpl = ktpl;
    }

    public BigDecimal getNk1() {
        return nk1;
    }

    public void setNk1(BigDecimal nk1) {
        this.nk1 = nk1;
    }

    public BigDecimal getNk2() {
        return nk2;
    }

    public void setNk2(BigDecimal nk2) {
        this.nk2 = nk2;
    }

    public BigDecimal getTO_NL() {
        return TO_NL;
    }

    public void setTO_NL(BigDecimal TO_NL) {
        this.TO_NL = TO_NL;
    }

    public BigDecimal getLI_NL() {
        return LI_NL;
    }

    public void setLI_NL(BigDecimal LI_NL) {
        this.LI_NL = LI_NL;
    }

    public BigDecimal getHO_NL() {
        return HO_NL;
    }

    public void setHO_NL(BigDecimal HO_NL) {
        this.HO_NL = HO_NL;
    }

    public BigDecimal getVA_NL() {
        return VA_NL;
    }

    public void setVA_NL(BigDecimal VA_NL) {
        this.VA_NL = VA_NL;
    }

    public BigDecimal getSI_NL() {
        return SI_NL;
    }

    public void setSI_NL(BigDecimal SI_NL) {
        this.SI_NL = SI_NL;
    }

    public BigDecimal getSU_NL() {
        return SU_NL;
    }

    public void setSU_NL(BigDecimal SU_NL) {
        this.SU_NL = SU_NL;
    }

    public BigDecimal getDI_NL() {
        return DI_NL;
    }

    public void setDI_NL(BigDecimal DI_NL) {
        this.DI_NL = DI_NL;
    }

    public BigDecimal getN1_NL() {
        return N1_NL;
    }

    public void setN1_NL(BigDecimal N1_NL) {
        this.N1_NL = N1_NL;
    }

    public BigDecimal getTO_VS() {
        return TO_VS;
    }

    public void setTO_VS(BigDecimal TO_VS) {
        this.TO_VS = TO_VS;
    }

    public BigDecimal getLI_VS() {
        return LI_VS;
    }

    public void setLI_VS(BigDecimal LI_VS) {
        this.LI_VS = LI_VS;
    }

    public BigDecimal getHO_VS() {
        return HO_VS;
    }

    public void setHO_VS(BigDecimal HO_VS) {
        this.HO_VS = HO_VS;
    }

    public BigDecimal getVA_VS() {
        return VA_VS;
    }

    public void setVA_VS(BigDecimal VA_VS) {
        this.VA_VS = VA_VS;
    }

    public BigDecimal getSI_VS() {
        return SI_VS;
    }

    public void setSI_VS(BigDecimal SI_VS) {
        this.SI_VS = SI_VS;
    }

    public BigDecimal getSU_VS() {
        return SU_VS;
    }

    public void setSU_VS(BigDecimal SU_VS) {
        this.SU_VS = SU_VS;
    }

    public BigDecimal getDI_VS() {
        return DI_VS;
    }

    public void setDI_VS(BigDecimal DI_VS) {
        this.DI_VS = DI_VS;
    }

    public BigDecimal getN1_VS() {
        return N1_VS;
    }

    public void setN1_VS(BigDecimal N1_VS) {
        this.N1_VS = N1_VS;
    }

    public BigDecimal getNl1() {
        return nl1;
    }

    public void setNl1(BigDecimal nl1) {
        this.nl1 = nl1;
    }

    public BigDecimal getNl2() {
        return nl2;
    }

    public void setNl2(BigDecimal nl2) {
        this.nl2 = nl2;
    }

}
