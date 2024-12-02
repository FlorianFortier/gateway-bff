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

@RestController
@RequestMapping("/api/gateway/patients")
public class PatientController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    JwtService jwtService;
    private static final  String PATIENT_SERVICE_URL = "http://192.168.0.102:8085/api/patients";

    // Créer ou mettre à jour un patient
    @PostMapping
    public ResponseEntity<Patient> createOrUpdatePatient(@RequestBody Patient patient) {
        return restTemplate.postForEntity(PATIENT_SERVICE_URL, patient, Patient.class);
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }

        String token = authorizationHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authorizationHeader); // Transmettre le token JWT reçu
        HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(PATIENT_SERVICE_URL, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Patient>>() {});
    }



    // Obtenir un patient par ID
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable String id) {
        String url = PATIENT_SERVICE_URL + "/" + id;
        return restTemplate.getForEntity(url, Patient.class);
    }

    // Supprimer un patient par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatientById(@PathVariable String id) {
        String url = PATIENT_SERVICE_URL + "/" + id;
        restTemplate.delete(url);
        return ResponseEntity.ok().build();
    }
}