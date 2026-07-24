package com.hospital.management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false) private String username;
    @Column(nullable = false) private String password;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Role role;

    public enum Role { ADMIN, DOCTOR, STAFF, PATIENT, RECEPTIONIST }

    public enum UserState { LOGGED_OUT, LOGGING_IN, ACTIVE, CHANGING_PASSWORD }

    @Enumerated(EnumType.STRING)
    @Column(name = "user_state")
    private UserState state = UserState.LOGGED_OUT;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public UserState getState() { return state; }
    public void setState(UserState state) { this.state = state; }
}
