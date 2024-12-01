package com.abernathyclinic.gateway.gateway_bff.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.abernathyclinic.gateway.gateway_bff.service.JwtService;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final JwtService jwtService;

    public CustomUserDetailsService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String jwt) throws UsernameNotFoundException {
        String username = jwtService.extractUsername(jwt);
        if (username == null) {
            throw new UsernameNotFoundException("User not found");
        }
        // Implémentez la logique pour charger l'utilisateur par son nom d'utilisateur
        // Par exemple, vous pouvez charger l'utilisateur depuis une base de données
        return new User(username, "", Collections.emptyList());
    }
}