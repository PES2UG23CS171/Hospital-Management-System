// model/Appointment.java
package com.hospital.management.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.REQUESTED; // ← default changed

    // ✅ EXPANDED enum — matches your state diagram
    public enum AppointmentStatus {
        REQUESTED,
        SCHEDULED,
        CHECKED_IN,
        IN_CONSULTATION,
        TESTS_ORDERED,
        COMPLETED,
        CANCELLED
    }

    public Appointment() {}

    public Appointment(Patient patient, Doctor doctor,
                       LocalDate date, LocalTime time) {
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDate = date;
        this.appointmentTime = time;
    }

    // ✅ TRANSITION METHODS — each enforces valid predecessor state
    public void schedule() {
        if (status == AppointmentStatus.REQUESTED)
            status = AppointmentStatus.SCHEDULED;
        else throw new IllegalStateException("Can only schedule a REQUESTED appointment");
    }

    public void checkIn() {
        if (status == AppointmentStatus.SCHEDULED)
            status = AppointmentStatus.CHECKED_IN;
        else throw new IllegalStateException("Can only check in a SCHEDULED appointment");
    }

    public void startConsultation() {
        if (status == AppointmentStatus.CHECKED_IN)
            status = AppointmentStatus.IN_CONSULTATION;
        else throw new IllegalStateException("Patient must be CHECKED_IN first");
    }

    public void orderTests() {
        if (status == AppointmentStatus.IN_CONSULTATION)
            status = AppointmentStatus.TESTS_ORDERED;
        else throw new IllegalStateException("Tests can only be ordered IN_CONSULTATION");
    }

    public void complete() {
        if (status == AppointmentStatus.IN_CONSULTATION
                || status == AppointmentStatus.TESTS_ORDERED)
            status = AppointmentStatus.COMPLETED;
        else throw new IllegalStateException("Cannot complete from current state");
    }

    public void cancel() {
        if (status == AppointmentStatus.COMPLETED)
            throw new IllegalStateException("Cannot cancel a COMPLETED appointment");
        status = AppointmentStatus.CANCELLED;
    }

    // Getters and Setters (same as before)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient p) { this.patient = p; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor d) { this.doctor = d; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate d) { this.appointmentDate = d; }
    public LocalTime getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(LocalTime t) { this.appointmentTime = t; }
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus s) { this.status = s; }
}