package com.hospital.management.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "doctors")
public class Doctor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100) private String name;
    @Column(length = 100) private String specialization;
    @Column(length = 15) private String phone;
    private String schedule;
    @OneToOne @JoinColumn(name = "user_id") private User user;
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Appointment> appointments;

    public Doctor() {}
    public Doctor(String name, String specialization, String phone, String schedule) {
        this.name = name; this.specialization = specialization;
        this.phone = phone; this.schedule = schedule;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String s) { this.specialization = s; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> a) { this.appointments = a; }
}
