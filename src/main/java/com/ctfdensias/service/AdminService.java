package com.ctfdensias.service;

import com.ctfdensias.dto.request.CompetitionRequest;
import com.ctfdensias.model.*;

import java.util.List;
import java.util.UUID;

/**
 * AdminService — from UML diagram.
 * All methods require an admin User as first parameter.
 */
public interface AdminService {
    User addUser(User admin, User user);
    User updateUser(User admin, User user);
    void deleteUser(User admin, UUID userId);
    User changeUserRole(User admin, UUID userId, Role role);

    Team createTeam(User admin, String teamName);
    Team updateTeam(User admin, UUID teamId, String teamName);
    void deleteTeam(User admin, UUID teamId);
    List<Team> getAllTeams(User admin);

    Competition setupCompetition(User admin, CompetitionRequest request);
    Competition updateCompetition(User admin, UUID competitionId, CompetitionRequest request);

    Challenge createChallenge(User admin, Challenge challenge);
    Challenge updateChallenge(User admin, Challenge challenge);
    void deleteChallenge(User admin, UUID challengeId);
    Challenge addChallengeFlag(User admin, UUID challengeId, String rawFlag);
    void removeChallengeFlag(User admin, UUID challengeId, UUID flagId);
}
