package com.abernathyclinic.gateway.gateway_bff.controller;

import com.abernathyclinic.gateway.gateway_bff.AuthRequest;
import com.abernathyclinic.gateway.gateway_bff.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class JwtController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtController(AuthenticationManager authenticationManager, JwtService jwtService, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    @PostMapping("/token")
    public String generateToken(@RequestBody AuthRequest authRequest) {
        try {
            // Authentifie l'utilisateur
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            // Vérifier si l'utilisateur a le rôle ORGANIZER
            boolean hasOrganizerRole = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(role -> role.equals("ROLE_ORGANIZER"));

            if (!hasOrganizerRole) {
                throw new RuntimeException("Access denied: Only organizers can generate tokens");
            }

            // Si l'authentification est réussie, charge les détails de l'utilisateur
            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

            // Génère et retourne un token JWT
            return jwtService.generateToken(userDetails);
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }
}
