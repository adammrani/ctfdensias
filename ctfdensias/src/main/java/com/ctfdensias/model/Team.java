package main.java.com.ctfdensias.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    private LocalDateTime createdAt = LocalDateTime.now();

    // A Team can have many Users. mappedBy = "team" links it to the 'team' variable in the User class.
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<User> members = new ArrayList<>();

    // --- Constructor ---
    public Team() {
    }

    // --- Methods from Diagram ---
    public void addMember(User user) {
        this.members.add(user);
        user.setTeam(this); // Maintains the bidirectional relationship
    }

    public void removeMember(User user) {
        this.members.remove(user);
        user.setTeam(null);
    }

    public Integer getScore() {
        // We will implement the actual score calculation logic later!
        return 0; 
    }

    // --- Getters and Setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<User> getMembers() { return members; }
    public void setMembers(List<User> members) { this.members = members; }
}
