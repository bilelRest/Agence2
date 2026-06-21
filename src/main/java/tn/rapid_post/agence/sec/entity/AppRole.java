package tn.rapid_post.agence.sec.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class AppRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Permission> permissionList = new ArrayList<>();

    // Constructeurs
    public AppRole() {}
    public AppRole(String name) {
        this.name = name;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public List<Permission> getPermissionList() { return permissionList; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPermissionList(List<Permission> permissionList) {
        this.permissionList = permissionList;
    }
}