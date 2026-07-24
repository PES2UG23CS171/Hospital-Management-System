package com.hospital.management.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bills")
public class Bill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    @OneToOne @JoinColumn(name = "appointment_id")
    private Appointment appointment;
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;
    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;
    @Column(name = "items_description", columnDefinition = "TEXT")
    private String itemsDescription;

    public Bill() {}
    public Bill(Patient patient, Double totalAmount, String itemsDescription) {
        this.patient = patient; this.totalAmount = totalAmount;
        this.billDate = LocalDate.now(); this.itemsDescription = itemsDescription;
    }

    public String generateBill() {
        return String.format("Bill #%d | Patient: %s | Amount: %.2f | Date: %s",
                id, patient.getName(), totalAmount, billDate);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient p) { this.patient = p; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment a) { this.appointment = a; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double amt) { this.totalAmount = amt; }
    public LocalDate getBillDate() { return billDate; }
    public void setBillDate(LocalDate d) { this.billDate = d; }
    public String getItemsDescription() { return itemsDescription; }
    public void setItemsDescription(String d) { this.itemsDescription = d; }
}
