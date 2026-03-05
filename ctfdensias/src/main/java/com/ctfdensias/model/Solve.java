package main.java.com.ctfdensias.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "solves")
public class Solve {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDateTime solvedAt = LocalDateTime.now();
    
    private Integer awardedPoints;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public Solve() {
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDateTime getSolvedAt() { return solvedAt; }
    public void setSolvedAt(LocalDateTime solvedAt) { this.solvedAt = solvedAt; }

    public Integer getAwardedPoints() { return awardedPoints; }
    public void setAwardedPoints(Integer awardedPoints) { this.awardedPoints = awardedPoints; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Challenge getChallenge() { return challenge; }
    public void setChallenge(Challenge challenge) { this.challenge = challenge; }
}