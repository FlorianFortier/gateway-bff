package com.abernathyclinic.gateway.gateway_bff.controller;

import com.abernathyclinic.gateway.gateway_bff.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @return Token JWT
     */
    @PostMapping("/token")
    public ResponseEntity<String> generateToken(@RequestBody Map<String, Object> claims) {
        try {
            // Valider et extraire les informations utilisateur
            String username = (String) claims.get("username");
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");

            if (username == null || roles == null) {
                return ResponseEntity.badRequest().body("Invalid context provided");
            }

            // Générer un token JWT
            String token = jwtService.generateToken(username, roles);

            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating token: " + e.getMessage());
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
        }

        String jwt = authHeader.substring(7); // Retire "Bearer "
        if (jwtService.isTokenValid(jwt)) {
            return ResponseEntity.ok("Valid Token");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }
}

