package tn.rapid_post.agence.sec.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String path;

    // Constructeurs
    public Permission() {}
    public Permission(String nom, String path) {
        this.nom = nom;
        this.path = path;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public String getNom() { return nom; }
    public String getPath() { return path; }

    public void setId(Long id) { this.id = id; }
    public void setNom(String nom) { this.nom = nom; }
    public void setPath(String path) { this.path = path; }
}