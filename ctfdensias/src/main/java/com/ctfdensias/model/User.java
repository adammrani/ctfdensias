package main.java.com.ctfdensias.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users") 
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean isActive = true;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Many Users can belong to One Team
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    // --- Constructor ---
    public User() {
    }

    // --- Methods from Diagram ---
    public void setPassword(String rawPassword) {
        // Note: In reality, we will hash this using Spring Security PasswordEncoder in the Service layer
        this.passwordHash = rawPassword; 
    }

    public Boolean verifyPassword(String rawPassword) {
        // We will implement proper verification using bcrypt later
        return this.passwordHash.equals(rawPassword); 
    }

    public Boolean isAdmin() {
        return this.role == Role.ADMIN;
    }

    // --- Getters and Setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Team getTeam() { return team; }
    public void setTeam(Team team) { this.team = team; }
}