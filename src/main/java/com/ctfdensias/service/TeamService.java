package com.ctfdensias.service;

import com.ctfdensias.dto.request.TeamRequest;
import com.ctfdensias.model.Team;
import com.ctfdensias.model.User;

import java.util.List;
import java.util.UUID;

public interface TeamService {
    List<Team> getAllTeams(User admin);
    Team getTeamById(UUID id);
    Team createTeam(TeamRequest request, User creator);
    Team updateTeam(User admin, UUID teamId, TeamRequest request);
    void deleteTeam(User admin, UUID teamId);
    Team addMember(UUID teamId, UUID userId);
    Team removeMember(UUID teamId, UUID userId);
}
