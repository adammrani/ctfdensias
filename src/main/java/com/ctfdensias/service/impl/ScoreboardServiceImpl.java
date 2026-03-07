package com.ctfdensias.service.impl;

import com.ctfdensias.dto.response.ScoreboardEntry;
import com.ctfdensias.exception.AccessDeniedException;
import com.ctfdensias.model.Competition;
import com.ctfdensias.model.Solve;
import com.ctfdensias.model.User;
import com.ctfdensias.repository.CompetitionRepository;
import com.ctfdensias.repository.SolveRepository;
import com.ctfdensias.repository.UserRepository;
import com.ctfdensias.service.ScoreboardService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScoreboardServiceImpl implements ScoreboardService {

    private final SolveRepository solveRepository;
    private final UserRepository userRepository;
    private final CompetitionRepository competitionRepository;

    public ScoreboardServiceImpl(SolveRepository solveRepository,
                                  UserRepository userRepository,
                                  CompetitionRepository competitionRepository) {
        this.solveRepository = solveRepository;
        this.userRepository = userRepository;
        this.competitionRepository = competitionRepository;
    }

    @Override
    public List<ScoreboardEntry> getScoreboard() {
        // Check if scoreboard is visible (based on active competition)
        List<Competition> active = competitionRepository.findByIsActiveTrue();
        if (!active.isEmpty() && !Boolean.TRUE.equals(active.get(0).getIsScoreboardVisible())) {
            throw new AccessDeniedException("Scoreboard is currently hidden by the administrator");
        }
        return buildScoreboard();
    }

    @Override
    public List<ScoreboardEntry> getScoreboardAdmin() {
        // Admin always sees scoreboard regardless of visibility setting
        return buildScoreboard();
    }

    private List<ScoreboardEntry> buildScoreboard() {
        Map<UUID, List<Solve>> solvesByUser = solveRepository.findAll().stream()
                .collect(Collectors.groupingBy(s -> s.getUser().getId()));

        List<ScoreboardEntry> entries = userRepository.findAll().stream().map(user -> {
            List<Solve> userSolves = solvesByUser.getOrDefault(user.getId(), List.of());
            int total = userSolves.stream()
                    .mapToInt(s -> s.getAwardedPoints() != null ? s.getAwardedPoints() : 0).sum();
            String teamName = user.getTeam() != null ? user.getTeam().getName() : null;
            return new ScoreboardEntry(0, user.getUsername(), teamName, total, userSolves.size());
        }).collect(Collectors.toList());

        entries.sort(Comparator.comparingInt(ScoreboardEntry::getTotalPoints).reversed());
        for (int i = 0; i < entries.size(); i++) entries.get(i).setRank(i + 1);
        return entries;
    }
}
