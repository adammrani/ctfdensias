package main.java.com.ctfdensias.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String submittedFlag;

    private Boolean isCorrect;
    
    private LocalDateTime submittedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    public Submission() {
    }

    public void processSubmission() {
        // Logique pour vérifier la soumission
    }

    public void triggerDynamicDecay() {
        // Fera appel à la classe DynamicScoring plus tard
    }

    // --- Getters et Setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getSubmittedFlag() { return submittedFlag; }
    public void setSubmittedFlag(String submittedFlag) { this.submittedFlag = submittedFlag; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean correct) { isCorrect = correct; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Challenge getChallenge() { return challenge; }
    public void setChallenge(Challenge challenge) { this.challenge = challenge; }
}