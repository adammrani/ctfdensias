package com.ctfdensias.service.impl;

import com.ctfdensias.dto.request.CompetitionRequest;
import com.ctfdensias.exception.AccessDeniedException;
import com.ctfdensias.exception.ResourceNotFoundException;
import com.ctfdensias.model.*;
import com.ctfdensias.repository.*;
import com.ctfdensias.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ChallengeRepository challengeRepository;
    private final CompetitionRepository competitionRepository;
    private final FlagRepository flagRepository;

    public AdminServiceImpl(UserRepository userRepository, TeamRepository teamRepository,
                             ChallengeRepository challengeRepository,
                             CompetitionRepository competitionRepository,
                             FlagRepository flagRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.challengeRepository = challengeRepository;
        this.competitionRepository = competitionRepository;
        this.flagRepository = flagRepository;
    }

    private void requireAdmin(User admin) {
        if (!admin.isAdmin()) throw new AccessDeniedException("Admin role required");
    }

    @Override
    @Transactional
    public User addUser(User admin, User user) {
        requireAdmin(admin);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(User admin, User user) {
        requireAdmin(admin);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(User admin, UUID userId) {
        requireAdmin(admin);
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public User changeUserRole(User admin, UUID userId, Role role) {
        requireAdmin(admin);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Team createTeam(User admin, String teamName) {
        requireAdmin(admin);
        Team team = new Team();
        team.setName(teamName);
        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public Team updateTeam(User admin, UUID teamId, String teamName) {
        requireAdmin(admin);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        team.setName(teamName);
        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public void deleteTeam(User admin, UUID teamId) {
        requireAdmin(admin);
        teamRepository.deleteById(teamId);
    }

    @Override
    public List<Team> getAllTeams(User admin) {
        requireAdmin(admin);
        return teamRepository.findAll();
    }

    @Override
    @Transactional
    public Competition setupCompetition(User admin, CompetitionRequest request) {
        requireAdmin(admin);
        Competition competition = new Competition();
        competition.setName(request.getName());
        competition.setStartTime(request.getStartTime());
        competition.setEndTime(request.getEndTime());
        return competitionRepository.save(competition);
    }

    @Override
    @Transactional
    public Competition updateCompetition(User admin, UUID competitionId, CompetitionRequest request) {
        requireAdmin(admin);
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));
        competition.setName(request.getName());
        competition.setStartTime(request.getStartTime());
        competition.setEndTime(request.getEndTime());
        return competitionRepository.save(competition);
    }

    @Override
    @Transactional
    public Challenge createChallenge(User admin, Challenge challenge) {
        requireAdmin(admin);
        challenge.setPoints(challenge.getInitialPoints());
        return challengeRepository.save(challenge);
    }

    @Override
    @Transactional
    public Challenge updateChallenge(User admin, Challenge challenge) {
        requireAdmin(admin);
        return challengeRepository.save(challenge);
    }

    @Override
    @Transactional
    public void deleteChallenge(User admin, UUID challengeId) {
        requireAdmin(admin);
        challengeRepository.deleteById(challengeId);
    }

    @Override
    @Transactional
    public Challenge addChallengeFlag(User admin, UUID challengeId, String rawFlag) {
        requireAdmin(admin);
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        Flag flag = new Flag();
        flag.setHash(rawFlag);
        flag.setChallenge(challenge);
        flagRepository.save(flag);
        challenge.getFlags().add(flag);
        return challengeRepository.save(challenge);
    }

    @Override
    @Transactional
    public void removeChallengeFlag(User admin, UUID challengeId, UUID flagId) {
        requireAdmin(admin);
        flagRepository.deleteById(flagId);
    }
}
