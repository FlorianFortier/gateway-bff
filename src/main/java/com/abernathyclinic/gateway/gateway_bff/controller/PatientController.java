package com.abernathyclinic.gateway.gateway_bff.controller;

import com.abernathyclinic.gateway.gateway_bff.model.Patient;
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
 * Contrôleur pour gérer les opérations relatives aux patients via la Gateway.
 */
@RestController
@RequestMapping("/api/gateway/patients")
public class PatientController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    JwtService jwtService;

    private static final String PATIENT_SERVICE_URL = "http://192.168.0.102:8085/api/patients";

    /**
     * Crée ou met à jour un patient en appelant le service des patients.
     *
     * @param request La requête HTTP contenant le token JWT.
     * @param patient L'objet représentant le patient à créer ou à mettre à jour.
     * @return La réponse contenant les détails du patient créé ou mis à jour.
     */
    @PostMapping
    public ResponseEntity<Patient> createOrUpdatePatient(HttpServletRequest request, @RequestBody Patient patient) {
        HttpHeaders headers = jwtService.validateAndBuildHeaders(request);
        HttpEntity<Patient> httpEntity = new HttpEntity<>(patient, headers);

        return restTemplate.exchange(PATIENT_SERVICE_URL, HttpMethod.POST, httpEntity, Patient.class);
    }

    /**
     * Met à jour un patient existant via le service des patients.
     *
     * @param request La requête HTTP contenant le token JWT.
     * @param id      L'identifiant du patient à mettre à jour.
     * @param patient L'objet contenant les nouvelles informations du patient.
     * @return La réponse contenant les détails du patient mis à jour.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(HttpServletRequest request, @PathVariable String id, @RequestBody Patient patient) {
        HttpHeaders headers = jwtService.validateAndBuildHeaders(request);
        HttpEntity<Patient> httpEntity = new HttpEntity<>(patient, headers);

        String url = PATIENT_SERVICE_URL + "/" + id;
        return restTemplate.exchange(url, HttpMethod.PUT, httpEntity, Patient.class);
    }

    /**
     * Récupère la liste de tous les patients.
     *
     * @param request La requête HTTP contenant le token JWT.
     * @return La réponse contenant une liste de patients.
     */
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients(HttpServletRequest request) {
        HttpHeaders headers = jwtService.validateAndBuildHeaders(request);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(PATIENT_SERVICE_URL, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Patient>>() {});
    }

    /**
     * Récupère les détails d'un patient via son identifiant.
     *
     * @param request La requête HTTP contenant le token JWT.
     * @param id      L'identifiant du patient à récupérer.
     * @return La réponse contenant les détails du patient.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(HttpServletRequest request, @PathVariable String id) {
        HttpHeaders headers = jwtService.validateAndBuildHeaders(request);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        String url = PATIENT_SERVICE_URL + "/" + id;
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, Patient.class);
    }

    /**
     * Supprime un patient via son identifiant.
     *
     * @param request La requête HTTP contenant le token JWT.
     * @param id      L'identifiant du patient à supprimer.
     * @return Une réponse indiquant le succès de l'opération.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatientById(HttpServletRequest request, @PathVariable String id) {
        HttpHeaders headers = jwtService.validateAndBuildHeaders(request);
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        String url = PATIENT_SERVICE_URL + "/" + id;
        restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Void.class);
        return ResponseEntity.ok().build();
    }
}
