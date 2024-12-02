package com.abernathyclinic.gateway.gateway_bff.model;

import java.time.LocalDate;
import java.util.List;


public class Patient {


    private String nom;
    private List<String> medicalNotes;
    private String prenom;
    private LocalDate dateDeNaissance;
    private String genre;
    private String adresse;
    private String telephone;
    private LocalDate lastModified;
    private LocalDate createdAt;
    private String whoLastModified;


    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<String> getMedicalNotes() {
        return medicalNotes;
    }

    public void setMedicalNotes(List<String> medicalNotes) {
        this.medicalNotes = medicalNotes;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public LocalDate getDateDeNaissance() {
        return dateDeNaissance;
    }

    public void setDateDeNaissance(LocalDate dateDeNaissance) {
        this.dateDeNaissance = dateDeNaissance;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public LocalDate getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDate lastModified) {
        this.lastModified = lastModified;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getWhoLastModified() {
        return whoLastModified;
    }

    public void setWhoLastModified(String whoLastModified) {
        this.whoLastModified = whoLastModified;
    }
}
