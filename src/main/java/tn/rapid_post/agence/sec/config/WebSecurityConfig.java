package tn.rapid_post.agence.sec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import tn.rapid_post.agence.sec.service.DynamicSecurityService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final DynamicSecurityService dynamicSecurityService;

    public WebSecurityConfig(DynamicSecurityService dynamicSecurityService) {
        this.dynamicSecurityService = dynamicSecurityService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .headers(headers -> headers
                        .frameOptions(frame -> frame.disable()) // Désactive X-Frame-Options
                );
        http
                .authorizeHttpRequests(auth -> {
                    dynamicSecurityService.configureDynamicRoutes(auth);
                })
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/loginpath",true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        http.sessionManagement(session -> session
                .sessionConcurrency(concurrency -> concurrency
                        .maximumSessions(1) // max 1 session par utilisateur
                        .maxSessionsPreventsLogin(true) // empêche une nouvelle connexion si une session existe déjà
                )
        );


        return http.build();
    }


}