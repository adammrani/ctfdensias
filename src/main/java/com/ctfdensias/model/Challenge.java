package com.ctfdensias.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "challenges")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "difficulty_type")
    private Difficulty difficulty;

    private Integer points;
    private Integer initialPoints;
    private Integer minimumPoints;

    private String fileUrl;
    private String fileName;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;

    @JsonIgnore
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flag> flags = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hint> hints = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<Submission> submissions = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<Solve> solves = new ArrayList<>();

    public Challenge() {}

    public Boolean validateFlag(String input) {
        if (input == null || flags.isEmpty()) return false;
        return flags.stream().anyMatch(f -> f.matches(input));
    }

    public void updatePoints(Integer newPoints) {
        this.points = newPoints;
    }

    public List<Hint> getHintsList() { return this.hints; }

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public Integer getInitialPoints() { return initialPoints; }
    public void setInitialPoints(Integer initialPoints) { this.initialPoints = initialPoints; }
    public Integer getMinimumPoints() { return minimumPoints; }
    public void setMinimumPoints(Integer minimumPoints) { this.minimumPoints = minimumPoints; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Competition getCompetition() { return competition; }
    public void setCompetition(Competition competition) { this.competition = competition; }
    public List<Flag> getFlags() { return flags; }
    public void setFlags(List<Flag> flags) { this.flags = flags; }
    public List<Hint> getHints() { return hints; }
    public void setHints(List<Hint> hints) { this.hints = hints; }
    public List<Submission> getSubmissions() { return submissions; }
    public void setSubmissions(List<Submission> submissions) { this.submissions = submissions; }
    public List<Solve> getSolves() { return solves; }
    public void setSolves(List<Solve> solves) { this.solves = solves; }
}