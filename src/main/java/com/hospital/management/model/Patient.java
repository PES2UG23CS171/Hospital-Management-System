package com.hospital.management.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer age;
    private String gender;
    private String phone;
    private String address;
    @Column(columnDefinition = "TEXT")
    private String medicalHistory;  // Keep for backward compatibility

    @OneToOne @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Appointment> appointments;
    
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<MedicalRecord> medicalRecords;
    
    // ✅ FACTORY PATTERN - Static Factory Methods
    /**
     * Factory method to create a new patient with basic info
     */
    public static Patient createPatient(String name, Integer age, String gender) {
        Patient patient = new Patient();
        patient.setName(name);
        patient.setAge(age);
        patient.setGender(gender);
        return patient;
    }
    
    /**
     * Factory method to create a patient with full details
     */
    public static Patient createPatient(String name, Integer age, String gender, 
                                       String phone, String address) {
        Patient patient = new Patient();
        patient.setName(name);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setPhone(phone);
        patient.setAddress(address);
        return patient;
    }
    
    /**
     * Factory method to create a patient with medical history
     */
    public static Patient createPatientWithHistory(String name, Integer age, String gender,
                                                   String phone, String address, 
                                                   String medicalHistory) {
        Patient patient = createPatient(name, age, gender, phone, address);
        patient.setMedicalHistory(medicalHistory);
        return patient;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }
    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
    public void setMedicalRecords(List<MedicalRecord> medicalRecords) { this.medicalRecords = medicalRecords; }
}
