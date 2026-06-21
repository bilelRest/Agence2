package tn.rapid_post.agence.sec.service;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Service;
import tn.rapid_post.agence.sec.entity.AppRole;
import tn.rapid_post.agence.sec.repo.RoleRepository;

@Service
public class DynamicSecurityService {
    private final RoleRepository roleRepository;

    public DynamicSecurityService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public void configureDynamicRoutes(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {

        // Chemins publics
        auth.requestMatchers( "/login", "/error", "/css/**", "/js/**", "/webjars/**","/new","newuser").permitAll();

        // Configurer les permissions
        roleRepository.findAllWithPermissions().forEach(role -> {
            role.getPermissionList().forEach(perm -> {
                auth.requestMatchers(perm.getPath()).hasAuthority("PERM_" + perm.getNom());
            });

            // Optionnel: accès par rôle
            auth.requestMatchers("/" + role.getName().toLowerCase() + "/**")
                    .hasRole(role.getName());
        });

        // Tous les autres chemins nécessitent une authentification
        auth.anyRequest().authenticated();
    }
}