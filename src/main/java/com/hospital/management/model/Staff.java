package com.hospital.management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "staff")
public class Staff {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100) private String name;
    @Column(length = 50) private String role;
    @Column(length = 15) private String phone;
    private String schedule;
    @OneToOne @JoinColumn(name = "user_id") private User user;

    public Staff() {}
    public Staff(String name, String role, String phone, String schedule) {
        this.name = name; this.role = role; this.phone = phone; this.schedule = schedule;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
