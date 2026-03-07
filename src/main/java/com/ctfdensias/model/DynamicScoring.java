package com.ctfdensias.model;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * Implements dynamic scoring decay: as more teams solve a challenge,
 * its point value decreases from initialPoints down to minimumPoints.
 */
@Entity
@Table(name = "dynamic_scoring")
public class DynamicScoring {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private Double decayRate = 0.08;

    public DynamicScoring() {}

    public DynamicScoring(Double decayRate) {
        this.decayRate = decayRate;
    }

    // -------------------------------------------------------
    // Domain methods (from UML)
    // -------------------------------------------------------

    /**
     * Calculates the new point value for a challenge based on solve count.
     * Uses exponential decay: points = max(minPoints, initialPoints * e^(-decayRate * solveCount))
     */
    public Integer calculateNewPoints(Challenge challenge, Integer submissionCount) {
        if (submissionCount <= 0) return challenge.getInitialPoints();

        double decayed = challenge.getInitialPoints() * Math.exp(-decayRate * submissionCount);
        int newPoints = (int) Math.round(decayed);
        return Math.max(newPoints, challenge.getMinimumPoints());
    }

    /**
     * Applies the decay — updates the challenge's current point value in place.
     */
    public void applyDecay(Challenge challenge, Integer submissionCount) {
        int newPoints = calculateNewPoints(challenge, submissionCount);
        challenge.updatePoints(newPoints);
    }

    // -------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Double getDecayRate() { return decayRate; }
    public void setDecayRate(Double decayRate) { this.decayRate = decayRate; }
}
