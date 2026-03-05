package main.java.com.ctfdensias.model;

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
    private Difficulty difficulty;

    private Integer points;
    private Integer initialPoints;
    private Integer minimumPoints;
    
    private Boolean isActive = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relation avec la compétition
    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;

    // Un challenge a plusieurs Flags, Hints, Submissions, Solves
    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<Flag> flags = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<Hint> hints = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<Submission> submissions = new ArrayList<>();

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    private List<Solve> solves = new ArrayList<>();

    // --- Constructeur ---
    public Challenge() {
    }

    // --- Méthodes du diagramme ---
    public Boolean validateFlag(String input) {
        // Logique de validation à faire plus tard
        return false;
    }

    public void updatePoints(Integer newPoints) {
        this.points = newPoints;
    }

    public List<Hint> getHintsList() {
        return this.hints;
    }

    // --- Getters et Setters ---
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