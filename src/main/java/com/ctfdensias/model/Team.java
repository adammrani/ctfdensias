package com.ctfdensias.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<User> members = new ArrayList<>();

    public Team() {}

    public void addMember(User user) {
        this.members.add(user);
        user.setTeam(this);
    }

    public void removeMember(User user) {
        this.members.remove(user);
        user.setTeam(null);
    }

    public Integer getScore() {
        return members.stream()
                .flatMap(u -> u.getSolves().stream())
                .mapToInt(s -> s.getAwardedPoints() != null ? s.getAwardedPoints() : 0)
                .sum();
    }

    public int getMemberCount() { return members.size(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public List<User> getMembers() { return members; }
    public void setMembers(List<User> members) { this.members = members; }
}