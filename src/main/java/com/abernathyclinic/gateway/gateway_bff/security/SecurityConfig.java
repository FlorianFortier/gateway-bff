package com.abernathyclinic.gateway.gateway_bff.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration de la sécurité pour l'application.
 * <p>
 * Cette classe définit les règles de sécurité, y compris la gestion des tokens JWT
 * et l'accès aux endpoints.
 * </p>
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true) // Active la sécurité basée sur les annotations
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructeur pour injecter le filtre d'authentification JWT.
     *
     * @param jwtAuthenticationFilter Le filtre d'authentification JWT.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configure la chaîne de filtres de sécurité.
     *
     * @param http L'objet HttpSecurity utilisé pour configurer la sécurité web.
     * @return La chaîne de filtres de sécurité configurée.
     * @throws Exception Si une erreur survient lors de la configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Désactive la protection CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/token").permitAll() // Autorise l'accès public à cet endpoint
                        .requestMatchers("/api/auth/validate").permitAll() // Autorise l'accès public à cet endpoint
                        .requestMatchers("/api/gateway/**").authenticated() // Nécessite une authentification pour les autres endpoints
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Utilise une stratégie sans état
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Ajoute le filtre JWT avant celui de l'authentification par nom d'utilisateur/mot de passe

        return http.build();
    }

    /**
     * Fournit un encodeur de mots de passe basé sur BCrypt.
     *
     * @return L'encodeur de mots de passe BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Fournit un gestionnaire d'authentification basé sur la configuration.
     *
     * @param configuration La configuration d'authentification.
     * @return Le gestionnaire d'authentification.
     * @throws Exception Si une erreur survient lors de la création du gestionnaire.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
