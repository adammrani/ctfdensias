package com.ctfdensias.dto.response;

import java.util.List;

public class ScoreboardEntry {
    private int rank;
    private String teamName;
    private List<String> members;
    private int totalPoints;
    private int solveCount;

    public ScoreboardEntry() {}

    public ScoreboardEntry(int rank, String teamName, List<String> members,
                           int totalPoints, int solveCount) {
        this.rank = rank;
        this.teamName = teamName;
        this.members = members;
        this.totalPoints = totalPoints;
        this.solveCount = solveCount;
    }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public List<String> getMembers() { return members; }
    public void setMembers(List<String> members) { this.members = members; }
    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }
    public int getSolveCount() { return solveCount; }
    public void setSolveCount(int solveCount) { this.solveCount = solveCount; }
}