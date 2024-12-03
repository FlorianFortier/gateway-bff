package com.abernathyclinic.gateway.gateway_bff.controller;

import com.abernathyclinic.gateway.gateway_bff.model.History;
import com.abernathyclinic.gateway.gateway_bff.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Contrôleur pour gérer les interactions entre la passerelle et le service d'historique des patients.
 */
@RestController
@RequestMapping("/api/gateway/history")
public class HistoryController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    private static final String HISTORY_SERVICE_URL = "http://192.168.0.102:9103/api/history";

    /**
     * Récupère l'historique des notes pour un patient donné.
     *
     * @param id      L'identifiant du patient.
     * @param request La requête HTTP contenant les informations d'authentification.
     * @return Une réponse HTTP contenant une liste des notes d'historique ou une erreur.
     */
    @GetMapping("/{id}")
    public ResponseEntity<List<History>> getPatientHistory(@PathVariable String id, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }

        String url = HISTORY_SERVICE_URL + "/" + id;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<History>>() {});
    }

    /**
     * Ajoute une nouvelle note à l'historique d'un patient.
     *
     * @param id      L'identifiant du patient.
     * @param note    La note à ajouter.
     * @param request La requête HTTP contenant les informations d'authentification.
     * @return Une réponse HTTP indiquant le succès ou l'échec de l'opération.
     */
    @PostMapping("/{id}/add")
    public ResponseEntity<Void> addNoteToHistory(
            @PathVariable String id,
            @RequestBody History note,
            HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }

        String url = HISTORY_SERVICE_URL + "/" + id + "/add";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader);

        HttpEntity<History> entity = new HttpEntity<>(note, headers);

        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
        }
    }

}
