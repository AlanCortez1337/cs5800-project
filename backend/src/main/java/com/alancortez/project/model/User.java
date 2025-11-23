package com.alancortez.project.model;

import com.alancortez.project.utils.USER_ROLE;
import jakarta.persistence.*; // Use jakarta.persistence for Spring Boot 3+
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Date;

@MappedSuperclass // Tells JPA to inherit fields to child entities, but not map this class itself
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates a constructor with all fields
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Integer id;

    @Column(name = "username", nullable = false, unique = true)
    protected String userName;

    // Use caution when storing plain passwords in production systems.
    @Column(name = "password", nullable = false)
    protected String password;

    // Assuming UserRole is either an Enum or a simple String.
    // If it's an Enum, @Enumerated(EnumType.STRING) is recommended.
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    protected USER_ROLE role;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_created", updatable = false)
    protected Date dateCreated;

    public User(String userName, String password, USER_ROLE role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public USER_ROLE getRole() {
        return role;
    }

    public void setUsername(String username) {
        this.userName = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_updated")
    protected Date dateUpdated;

    @PrePersist
    protected void onCreate() {
        if (dateCreated == null) {
            dateCreated = new Date();
        }
        dateUpdated = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        dateUpdated = new Date();
    }
}
