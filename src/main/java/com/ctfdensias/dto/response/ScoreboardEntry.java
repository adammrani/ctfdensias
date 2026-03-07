package com.ctfdensias.dto.response;

public class ScoreboardEntry {
    private int rank;
    private String username;
    private String teamName;
    private int totalPoints;
    private int solveCount;

    public ScoreboardEntry() {}
    public ScoreboardEntry(int rank, String username, String teamName, int totalPoints, int solveCount) {
        this.rank = rank;
        this.username = username;
        this.teamName = teamName;
        this.totalPoints = totalPoints;
        this.solveCount = solveCount;
    }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    public int getSolveCount() { return solveCount; }
    public void setSolveCount(int solveCount) { this.solveCount = solveCount; }
}
