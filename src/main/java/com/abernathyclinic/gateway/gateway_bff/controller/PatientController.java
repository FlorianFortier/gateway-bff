package com.abernathyclinic.gateway.gateway_bff.controller;

import com.abernathyclinic.gateway.gateway_bff.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
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

    private static final  String PATIENT_SERVICE_URL = "http://localhost:8080/api/patients";

    // Créer ou mettre à jour un patient
    @PostMapping
    public ResponseEntity<Patient> createOrUpdatePatient(@RequestBody Patient patient) {
        return restTemplate.postForEntity(PATIENT_SERVICE_URL, patient, Patient.class);
    }

    // Obtenir tous les patients
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        return restTemplate.exchange(
                PATIENT_SERVICE_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Patient>>() {}
        );
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