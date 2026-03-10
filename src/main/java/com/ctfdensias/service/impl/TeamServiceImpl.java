package com.ctfdensias.service.impl;

import com.ctfdensias.dto.request.TeamRequest;
import com.ctfdensias.exception.ConflictException;
import com.ctfdensias.exception.ResourceNotFoundException;
import com.ctfdensias.model.Team;
import com.ctfdensias.model.User;
import com.ctfdensias.repository.TeamRepository;
import com.ctfdensias.repository.UserRepository;
import com.ctfdensias.service.TeamService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public TeamServiceImpl(TeamRepository teamRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Team> getAllTeams(User admin) {
        return teamRepository.findAll();
    }

    @Override
    public Team getTeamById(UUID id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + id));
    }

    @Override
    @Transactional
    public Team createTeam(TeamRequest request, User creator) {
        if (creator.getTeam() != null) {
            throw new ConflictException("You are already in a team. Leave your current team first.");
        }
        if (teamRepository.existsByName(request.getName())) {
            throw new ConflictException("Team name already taken: " + request.getName());
        }
        Team team = new Team();
        team.setName(request.getName());
        team.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        team = teamRepository.save(team);
        team.addMember(creator);
        userRepository.save(creator);
        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public Team joinTeam(String teamName, String password, User user) {
        if (user.getTeam() != null) {
            throw new ConflictException("You are already in a team. Leave your current team first.");
        }
        Team team = teamRepository.findByName(teamName)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found: " + teamName));
        if (!passwordEncoder.matches(password, team.getPasswordHash())) {
            throw new com.ctfdensias.exception.AccessDeniedException("Incorrect team password.");
        }
        team.addMember(user);
        userRepository.save(user);
        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public Team updateTeam(User admin, UUID teamId, TeamRequest request) {
        Team team = getTeamById(teamId);
        team.setName(request.getName());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            team.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public void deleteTeam(User admin, UUID teamId) {
        Team team = getTeamById(teamId);
        for (User member : team.getMembers()) {
            member.setTeam(null);
            userRepository.save(member);
        }
        team.getMembers().clear();
        teamRepository.delete(team);
    }

    @Override
    @Transactional
    public Team addMember(UUID teamId, UUID userId) {
        Team team = getTeamById(teamId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        team.addMember(user);
        userRepository.save(user);
        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public Team removeMember(UUID teamId, UUID userId) {
        Team team = getTeamById(teamId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        team.removeMember(user);
        userRepository.save(user);
        return teamRepository.save(team);
    }
}