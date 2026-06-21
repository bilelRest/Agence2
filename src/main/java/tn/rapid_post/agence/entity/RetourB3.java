package tn.rapid_post.agence.entity;

import jakarta.persistence.*;

@Entity
public class RetourB3 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String numB3;
    private String nomPrenB3Ro;
    @OneToOne
    private B3 b3;

    public String getNomPrenB3Ro() {
        return nomPrenB3Ro;
    }

    public void setNomPrenB3Ro(String nomPrenB3Ro) {
        this.nomPrenB3Ro = nomPrenB3Ro;
    }

    public RetourB3(String numB3, String nomPrenB3Ro, B3 b3) {
        this.numB3 = numB3;
        this.nomPrenB3Ro = nomPrenB3Ro;
        this.b3 = b3;
    }

    public String getNumB3() {
        return numB3;
    }

    public void setNumB3(String numB3) {
        this.numB3 = numB3;
    }

    public String getId() {
        return "RO-"+id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public B3 getB3() {
        return b3;
    }

    public void setB3(B3 b3) {
        this.b3 = b3;
    }

    public RetourB3(B3 b3) {
        this.b3 = b3;
    }

    public RetourB3() {
    }




}

