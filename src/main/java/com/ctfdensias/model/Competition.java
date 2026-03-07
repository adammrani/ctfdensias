package com.ctfdensias.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "competitions")
public class Competition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Boolean isActive = true;

    /** Admin can hide the scoreboard at any time */
    @Column(nullable = false)
    private Boolean isScoreboardVisible = true;

    @JsonIgnore
    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Team> teams = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "competition", cascade = CascadeType.ALL)
    private List<Challenge> challenges = new ArrayList<>();

    public Competition() {}

    public Boolean isActive() { return this.isActive; }

    public void addChallenge(Challenge challenge) {
        this.challenges.add(challenge);
        challenge.setCompetition(this);
    }

    public void removeChallenge(Challenge challenge) {
        this.challenges.remove(challenge);
        challenge.setCompetition(null);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public Boolean getIsScoreboardVisible() { return isScoreboardVisible; }
    public void setIsScoreboardVisible(Boolean v) { isScoreboardVisible = v; }
    public List<Team> getTeams() { return teams; }
    public void setTeams(List<Team> teams) { this.teams = teams; }
    public List<Challenge> getChallenges() { return challenges; }
    public void setChallenges(List<Challenge> challenges) { this.challenges = challenges; }
}
