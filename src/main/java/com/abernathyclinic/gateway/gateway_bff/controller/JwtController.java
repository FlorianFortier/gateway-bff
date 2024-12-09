package com.abernathyclinic.gateway.gateway_bff.controller;

import com.abernathyclinic.gateway.gateway_bff.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class JwtController {

    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Endpoint pour générer un token JWT basé sur les informations fournies par le front.
     *
     * @param claims Les informations utilisateur envoyées par le front.
     * @return Un Mono avec le token JWT généré.
     */
    @PostMapping("/token")
    public Mono<ResponseEntity<String>> generateToken(@RequestBody Map<String, Object> claims) {
        return Mono.fromCallable(() -> {
            // Valider et extraire les informations utilisateur
            String username = (String) claims.get("username");
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");

            if (!claims.containsKey("username") || !claims.containsKey("roles")) {
                return ResponseEntity.badRequest().body("Missing required fields: username or role");
            }


            // Générer un token JWT
            String token = jwtService.generateToken(username, roles);
            return ResponseEntity.ok(token);
        }).onErrorResume(e -> {
            String errorMessage = "Error generating token: " + e.getMessage();
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage));
        });
    }


    /**
     * Endpoint pour valider un token JWT à partir des en-têtes HTTP.
     *
     * @param exchange Le ServerWebExchange contenant les détails de la requête.
     * @return Une réponse indiquant si le token est valide ou non.
     */
    @PostMapping("/validate")
    public Mono<ResponseEntity<String>> validateToken(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .flatMap(authHeader -> {
                    if (!authHeader.startsWith("Bearer ")) {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Missing or invalid Authorization header"));
                    }

                    String jwt = authHeader.substring(7); // Retirer "Bearer "
                    if (jwtService.isTokenValid(jwt)) {
                        String username = jwtService.extractUsername(jwt);
                        return Mono.just(ResponseEntity.ok("Valid Token for user: " + username));
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("Invalid or expired Token"));
                    }
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Missing Authorization header")));
    }
}
