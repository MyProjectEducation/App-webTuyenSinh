package com.tuyensinh.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

@Entity
@Table(name = "xt_tohop_monthi")
public class Combination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idtohop;
    
    @Column(unique = true)
    private String matohop;
    private String mon1;
    private String mon2;
    private String mon3;
    private String tentohop;

    public Combination() {}

    public Combination(int idtohop, String matohop, String mon1, String mon2, String mon3, String tentohop) {
        this.idtohop = idtohop;
        this.matohop = matohop;
        this.mon1 = mon1;
        this.mon2 = mon2;
        this.mon3 = mon3;
        this.tentohop = tentohop;
    }

    public int getIdtohop() { return idtohop; }
    public void setIdtohop(int idtohop) { this.idtohop = idtohop; }
    
    public String getMatohop() { return matohop; }
    public void setMatohop(String matohop) { this.matohop = matohop; }
    
    public String getMon1() { return mon1; }
    public void setMon1(String mon1) { this.mon1 = mon1; }
    
    public String getMon2() { return mon2; }
    public void setMon2(String mon2) { this.mon2 = mon2; }
    
    public String getMon3() { return mon3; }
    public void setMon3(String mon3) { this.mon3 = mon3; }
    
    public String getTentohop() { return tentohop; }
    public void setTentohop(String tentohop) { this.tentohop = tentohop; }
}
