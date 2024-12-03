package com.abernathyclinic.gateway.gateway_bff.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Service de gestion des JWT (JSON Web Tokens) pour l'authentification et l'autorisation.
 */
@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    /**
     * Définit la clé secrète utilisée pour signer les JWT.
     *
     * @param secretKey La clé secrète en base64.
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Définit la durée d'expiration des JWT.
     *
     * @param jwtExpiration La durée en millisecondes.
     */
    public void setJwtExpiration(long jwtExpiration) {
        this.jwtExpiration = jwtExpiration;
    }

    /**
     * Extrait le nom d'utilisateur (subject) d'un JWT.
     *
     * @param token Le JWT à analyser.
     * @return Le nom d'utilisateur extrait.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait une information spécifique (claim) d'un JWT.
     *
     * @param token           Le JWT à analyser.
     * @param claimsResolver  Une fonction pour extraire l'information désirée.
     * @param <T>             Le type de l'information extraite.
     * @return L'information extraite.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Génère un JWT pour un utilisateur donné.
     *
     * @param username Le nom d'utilisateur (subject).
     * @param roles    Les rôles de l'utilisateur à inclure dans les claims.
     * @return Le JWT généré.
     */
    public String generateToken(String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Vérifie si un JWT est valide.
     *
     * @param token Le JWT à valider.
     * @return {@code true} si le token est valide, sinon {@code false}.
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrait les informations d'expiration d'un JWT.
     *
     * @param token Le JWT à analyser.
     * @return La date d'expiration du token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Vérifie si un JWT est expiré.
     *
     * @param token Le JWT à analyser.
     * @return {@code true} si le token est expiré, sinon {@code false}.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait tous les claims d'un JWT.
     *
     * @param token Le JWT à analyser.
     * @return Les claims du token.
     * @throws IllegalArgumentException Si le token est nul ou vide.
     */
    private Claims extractAllClaims(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Decode argument cannot be null or empty");
        }
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .setAllowedClockSkewSeconds(60)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Génère la clé utilisée pour signer les JWT.
     *
     * @return La clé de signature.
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Valide un JWT et génère une requête HTTP correspondante.
     *
     * @param request La requête HTTP contenant le JWT dans l'en-tête Authorization.
     * @return Une entité HTTP valide ou une réponse d'erreur si le token est invalide.
     */
    public HttpEntity<Object> validateJwt(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }

        String token = authorizationHeader.substring(7);
        if (!isTokenValid(token)) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }
        return null;
    }

    /**
     * Valide un JWT et construit des en-têtes HTTP.
     *
     * @param request La requête HTTP contenant le JWT dans l'en-tête Authorization.
     * @return Les en-têtes HTTP construits.
     * @throws SecurityException Si le token est manquant ou invalide.
     */
    public HttpHeaders validateAndBuildHeaders(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new SecurityException("Unauthorized: Missing or invalid Authorization header");
        }

        String token = authorizationHeader.substring(7);
        if (!isTokenValid(token)) {
            throw new SecurityException("Unauthorized: Invalid token");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);
        return headers;
    }
}
