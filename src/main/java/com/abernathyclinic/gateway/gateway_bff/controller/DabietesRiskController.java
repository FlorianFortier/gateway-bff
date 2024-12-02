package com.abernathyclinic.gateway.gateway_bff.controller;

import com.abernathyclinic.gateway.gateway_bff.model.Patient;
import com.abernathyclinic.gateway.gateway_bff.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/gateway/diabetes")
public class DabietesRiskController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/risk")
    public ResponseEntity<String> calculateRisk(@RequestBody Patient patient, @RequestHeader RequestHeader authCreads) {

        // Ajoute le token JWT dans les en-têtes de la requête
        HttpHeaders headers = new HttpHeaders();

        HttpEntity<Patient> request = new HttpEntity<>(patient, headers);
        String url = "http://192.168.0.102:8080/api/diabetes/risk";
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return ResponseEntity.ok(response.getBody());
    }
}
