package tn.rapid_post.agence.sec.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, columnDefinition = "VARCHAR DEFAULT ''")

    private String nomPrenom;
    private String username;
    private String password;
    @Column(nullable = false, columnDefinition = "Boolean DEFAULT true ")

    private boolean isActive;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<AppRole> roles = new ArrayList<>();

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public AppUser() {

    }

    public String getNomPrenom() {
        return nomPrenom;
    }

    public void setNomPrenom(String nomPrenom) {
        this.nomPrenom = nomPrenom;
    }

    // Constructeurs
    public AppUser(String nomPrenom, boolean isActive) {
        this.nomPrenom = nomPrenom;
        this.isActive = isActive;
    }
    public AppUser(String nomPrenom, String username, String password, boolean isActive) {
        this.nomPrenom = nomPrenom;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
    }
    public AppUser(String nomPrenom, String username, String password, boolean isActive, List<AppRole> roles) {
        this.nomPrenom = nomPrenom;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Ajoute les rôles (format ROLE_NOM)
        authorities.addAll(roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList()));

        // Ajoute les permissions (format PERM_NOM)
        authorities.addAll(roles.stream()
                .flatMap(role -> role.getPermissionList().stream())
                .map(perm -> new SimpleGrantedAuthority("PERM_" + perm.getNom()))
                .collect(Collectors.toList()));

        return authorities;
    }

    // Getters/Setters
    @Override public String getPassword() { return password; }
    @Override public String getUsername() { return username; }
    public Long getId() { return id; }
    public List<AppRole> getRoles() { return roles; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRoles(List<AppRole> roles) { this.roles = roles; }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}