package com.abernathyclinic.gateway.gateway_bff.model;

import jakarta.validation.constraints.NotBlank;

public class History {

    private String id;
    @NotBlank(message = "La note ne peut pas être vide")
    private String note;

    @NotBlank(message = "L'ID du patient est requis")
    private String patId;
    private String patient;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public @NotBlank(message = "L'ID du patient est requis") String getPatId() {
        return patId;
    }

    public void setPatId(String patId) {
        this.patId = patId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPatient() {
        return patient;
    }

    public void setPatient(String patient) {
        this.patient = patient;
    }

}
