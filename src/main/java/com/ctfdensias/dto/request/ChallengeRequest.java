package com.ctfdensias.dto.request;

import com.ctfdensias.model.Difficulty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ChallengeRequest {

    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String category;

    @NotNull
    private Difficulty difficulty;

    @NotNull
    @Min(0)
    private Integer initialPoints;

    @NotNull
    @Min(0)
    private Integer minimumPoints;

    private UUID competitionId;

    public ChallengeRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public Integer getInitialPoints() { return initialPoints; }
    public void setInitialPoints(Integer initialPoints) { this.initialPoints = initialPoints; }

    public Integer getMinimumPoints() { return minimumPoints; }
    public void setMinimumPoints(Integer minimumPoints) { this.minimumPoints = minimumPoints; }

    public UUID getCompetitionId() { return competitionId; }
    public void setCompetitionId(UUID competitionId) { this.competitionId = competitionId; }
}
