package com.ctfdensias.service.impl;

import com.ctfdensias.dto.request.TeamRequest;
import com.ctfdensias.exception.ConflictException;
import com.ctfdensias.exception.ResourceNotFoundException;
import com.ctfdensias.model.Team;
import com.ctfdensias.model.User;
import com.ctfdensias.repository.TeamRepository;
import com.ctfdensias.repository.UserRepository;
import com.ctfdensias.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamServiceImpl(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
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
        if (teamRepository.existsByName(request.getName())) {
            throw new ConflictException("Team name already taken: " + request.getName());
        }
        Team team = new Team();
        team.setName(request.getName());
        team = teamRepository.save(team);
        team.addMember(creator);
        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public Team updateTeam(User admin, UUID teamId, TeamRequest request) {
        Team team = getTeamById(teamId);
        team.setName(request.getName());
        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public void deleteTeam(User admin, UUID teamId) {
        Team team = getTeamById(teamId);
        teamRepository.delete(team);
    }

    @Override
    @Transactional
    public Team addMember(UUID teamId, UUID userId) {
        Team team = getTeamById(teamId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        team.addMember(user);
        return teamRepository.save(team);
    }

    @Override
    @Transactional
    public Team removeMember(UUID teamId, UUID userId) {
        Team team = getTeamById(teamId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        team.removeMember(user);
        return teamRepository.save(team);
    }
}
