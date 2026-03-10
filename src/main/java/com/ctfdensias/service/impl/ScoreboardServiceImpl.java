package com.ctfdensias.service.impl;

import com.ctfdensias.dto.response.ScoreboardEntry;
import com.ctfdensias.exception.AccessDeniedException;
import com.ctfdensias.model.Competition;
import com.ctfdensias.model.Solve;
import com.ctfdensias.model.Team;
import com.ctfdensias.model.User;
import com.ctfdensias.repository.CompetitionRepository;
import com.ctfdensias.repository.SolveRepository;
import com.ctfdensias.repository.TeamRepository;
import com.ctfdensias.service.ScoreboardService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScoreboardServiceImpl implements ScoreboardService {

    private final SolveRepository solveRepository;
    private final TeamRepository teamRepository;
    private final CompetitionRepository competitionRepository;

    public ScoreboardServiceImpl(SolveRepository solveRepository,
                                 TeamRepository teamRepository,
                                 CompetitionRepository competitionRepository) {
        this.solveRepository = solveRepository;
        this.teamRepository = teamRepository;
        this.competitionRepository = competitionRepository;
    }

    @Override
    public List<ScoreboardEntry> getScoreboard() {
        List<Competition> active = competitionRepository.findByIsActiveTrue();
        if (!active.isEmpty() && !Boolean.TRUE.equals(active.get(0).getIsScoreboardVisible())) {
            throw new AccessDeniedException("Scoreboard is currently hidden by the administrator");
        }
        return buildScoreboard();
    }

    @Override
    public List<ScoreboardEntry> getScoreboardAdmin() {
        return buildScoreboard();
    }

    private List<ScoreboardEntry> buildScoreboard() {
        List<Solve> allSolves = solveRepository.findAll();

        Map<UUID, List<Solve>> solvesByTeam = new HashMap<>();
        Map<UUID, Team> teamsById = new HashMap<>();

        for (Solve solve : allSolves) {
            User user = solve.getUser();
            Team team = user != null ? user.getTeam() : null;
            if (team == null) continue;
            teamsById.put(team.getId(), team);
            solvesByTeam.computeIfAbsent(team.getId(), k -> new ArrayList<>()).add(solve);
        }

        for (Team team : teamRepository.findAll()) {
            if (!teamsById.containsKey(team.getId())) {
                teamsById.put(team.getId(), team);
                solvesByTeam.put(team.getId(), Collections.emptyList());
            }
        }

        List<ScoreboardEntry> entries = teamsById.values().stream().map(team -> {
            List<Solve> teamSolves = solvesByTeam.getOrDefault(team.getId(), List.of());

            Set<UUID> uniqueChallengeIds = teamSolves.stream()
                    .map(s -> s.getChallenge().getId())
                    .collect(Collectors.toSet());

            int totalPoints = teamSolves.stream()
                    .collect(Collectors.groupingBy(s -> s.getChallenge().getId(),
                             Collectors.minBy(Comparator.comparing(Solve::getSolvedAt))))
                    .values().stream()
                    .mapToInt(opt -> opt.map(s -> s.getAwardedPoints() != null ? s.getAwardedPoints() : 0).orElse(0))
                    .sum();

            List<String> memberNames = team.getMembers().stream()
                    .map(User::getUsername)
                    .collect(Collectors.toList());

            return new ScoreboardEntry(0, team.getName(), memberNames,
                    totalPoints, uniqueChallengeIds.size());
        }).collect(Collectors.toList());

        entries.sort(Comparator.comparingInt(ScoreboardEntry::getTotalPoints).reversed());
        for (int i = 0; i < entries.size(); i++) entries.get(i).setRank(i + 1);
        return entries;
    }
}