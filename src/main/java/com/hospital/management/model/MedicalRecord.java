package com.hospital.management.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // recordID from diagram
    
    @Column(columnDefinition = "TEXT")
    private String diagnosis;
    
    @Column(columnDefinition = "TEXT")
    private String prescription;
    
    @Column(name = "record_date")
    private LocalDate recordDate;
    
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    
    // Constructors
    public MedicalRecord() {}
    
    public MedicalRecord(String diagnosis, String prescription, Patient patient) {
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.patient = patient;
        this.recordDate = LocalDate.now();
    }
    
    // ✅ FACTORY PATTERN - Static Factory Methods
    /**
     * Factory method to create a medical record with diagnosis only
     */
    public static MedicalRecord createRecord(Patient patient, String diagnosis) {
        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setDiagnosis(diagnosis);
        record.setRecordDate(LocalDate.now());
        return record;
    }
    
    /**
     * Factory method to create a medical record with diagnosis and prescription
     */
    public static MedicalRecord createRecord(Patient patient, String diagnosis, String prescription) {
        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setDiagnosis(diagnosis);
        record.setPrescription(prescription);
        record.setRecordDate(LocalDate.now());
        return record;
    }
    
    /**
     * Factory method to create a complete medical record with doctor
     */
    public static MedicalRecord createRecord(Patient patient, Doctor doctor, 
                                            String diagnosis, String prescription) {
        MedicalRecord record = createRecord(patient, diagnosis, prescription);
        record.setDoctor(doctor);
        return record;
    }
    
    // Methods from diagram
    public void updateRecord(String diagnosis, String prescription) {
        this.diagnosis = diagnosis;
        this.prescription = prescription;
    }
    
    public String viewRecord() {
        return String.format("Record #%d | Date: %s | Diagnosis: %s | Prescription: %s",
                id, recordDate, diagnosis, prescription);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    
    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
    
    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }
    
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
}
