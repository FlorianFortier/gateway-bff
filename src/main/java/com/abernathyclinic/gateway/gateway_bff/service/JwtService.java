package com.abernathyclinic.gateway.gateway_bff.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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
     * @param token          Le JWT à analyser.
     * @param claimsResolver Une fonction pour extraire l'information désirée.
     * @param <T>            Le type de l'information extraite.
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
     * Extrait tous les claims d'un JWT.
     *
     * @param token Le JWT à analyser.
     * @return Les claims du token.
     */
    private Claims extractAllClaims(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
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
     * Valide un JWT à partir d'un `ServerWebExchange` et construit les en-têtes HTTP si valide.
     *
     * @param exchange L'échange serveur contenant les en-têtes de la requête.
     * @return Un `Mono<HttpHeaders>` avec les en-têtes construits si le token est valide.
     */
    public Mono<HttpHeaders> validateAndBuildHeaders(ServerWebExchange exchange) {
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Mono.error(new SecurityException("Unauthorized: Missing or invalid Authorization header"));
        }

        String token = authorizationHeader.substring(7);
        if (!isTokenValid(token)) {
            return Mono.error(new SecurityException("Unauthorized: Invalid token"));
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        return Mono.just(headers);
    }

    /**
     * Valide un JWT à partir d'un `ServerWebExchange`.
     *
     * @param exchange L'échange serveur contenant les en-têtes de la requête.
     * @return Un `Mono<Boolean>` indiquant si le token est valide.
     */
    public Mono<Boolean> validateJwt(ServerWebExchange exchange) {
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return Mono.just(false);
        }

        String token = authorizationHeader.substring(7);
        return Mono.just(isTokenValid(token));
    }
}
