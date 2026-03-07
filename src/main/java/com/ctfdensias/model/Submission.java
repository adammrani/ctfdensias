package com.ctfdensias.model;

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

    @Column(nullable = false)
    private Boolean isCorrect = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    public Submission() {}

    // -------------------------------------------------------
    // Domain methods (from UML) — implemented in service layer
    // -------------------------------------------------------

    /** Validates the submitted flag and sets isCorrect. Called by SubmissionService. */
    public void processSubmission() {
        this.isCorrect = challenge.validateFlag(this.submittedFlag);
    }

    /** Delegates dynamic scoring recalculation to DynamicScoring. Called by SubmissionService. */
    public void triggerDynamicDecay(DynamicScoring scoring) {
        if (Boolean.TRUE.equals(isCorrect)) {
            int submissionCount = challenge.getSolves().size();
            int newPoints = scoring.calculateNewPoints(challenge, submissionCount);
            scoring.applyDecay(challenge, submissionCount);
        }
    }

    // -------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------

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
