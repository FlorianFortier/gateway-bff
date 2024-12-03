package com.abernathyclinic.gateway.gateway_bff.controller;

import com.abernathyclinic.gateway.gateway_bff.model.Patient;
import com.abernathyclinic.gateway.gateway_bff.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Contrôleur pour gérer les requêtes liées au calcul du risque de diabète.
 */
@RestController
@RequestMapping("/api/gateway/diabetes")
public class DabietesRiskController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    /**
     * Envoie les données d'un patient au microservice de calcul du risque de diabète.
     *
     * @param request La requête HTTP contenant les informations d'authentification.
     * @param patient L'objet Patient contenant les informations nécessaires pour le calcul du risque.
     * @return Une réponse HTTP contenant le niveau de risque calculé ou un code d'erreur.
     */
    @PostMapping("/risk")
    public ResponseEntity<String> calculateRisk(HttpServletRequest request, @RequestBody Patient patient) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }

        String token = authorizationHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }

        // Envoyer directement le Patient au service Risk
        String url = "http://192.168.0.102:8084/api/diabetes/risk";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);

        HttpEntity<Patient> requestEntity = new HttpEntity<>(patient, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        return ResponseEntity.ok(response.getBody());
    }
}
