package com.abernathyclinic.gateway.gateway_bff.security;

import com.abernathyclinic.gateway.gateway_bff.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre d'authentification basé sur les JWT (JSON Web Tokens).
 * <p>
 * Ce filtre vérifie si un JWT est présent dans l'en-tête de la requête,
 * valide le token et charge l'utilisateur correspondant dans le contexte de sécurité.
 * </p>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Constructeur pour initialiser les services nécessaires.
     *
     * @param jwtService        Le service pour la gestion des JWT.
     * @param userDetailsService Le service pour charger les détails de l'utilisateur.
     */
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filtrage de chaque requête pour vérifier et valider les JWT.
     *
     * @param request     La requête HTTP entrante.
     * @param response    La réponse HTTP sortante.
     * @param filterChain La chaîne de filtres.
     * @throws ServletException En cas d'erreur liée au servlet.
     * @throws IOException      En cas d'erreur d'entrée/sortie.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        // Vérification de l'en-tête d'autorisation
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("No JWT token found in the request headers");
            filterChain.doFilter(request, response);
            return;
        }

        // Extraction du token JWT
        final String jwt = authHeader.substring(7); // Retirer le préfixe "Bearer "
        final String username = jwtService.extractUsername(jwt);

        logger.debug("JWT token found: " + jwt);
        logger.debug("Extracted username: " + username);

        // Validation et authentification du token
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt)) {
                logger.debug("JWT token is valid for user: " + username);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                logger.debug("Invalid JWT token for user: " + username);
            }
        } else {
            logger.debug("Username is null or user is already authenticated");
        }

        // Continuer le filtrage avec les autres filtres de la chaîne
        filterChain.doFilter(request, response);
    }
}
