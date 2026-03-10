package com.ctfdensias.controller;

import com.ctfdensias.dto.request.TeamRequest;
import com.ctfdensias.model.Team;
import com.ctfdensias.model.User;
import com.ctfdensias.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
@Tag(name = "Teams", description = "Team management")
@SecurityRequirement(name = "bearerAuth")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all teams (Admin only)")
    public ResponseEntity<List<Team>> getAllTeams(@AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(teamService.getAllTeams(admin));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by ID")
    public ResponseEntity<Team> getTeam(@PathVariable UUID id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new team (authenticated user becomes first member)")
    public ResponseEntity<Team> createTeam(@Valid @RequestBody TeamRequest request,
                                           @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamService.createTeam(request, currentUser));
    }

    @PostMapping("/join")
    @Operation(summary = "Join an existing team by name + password")
    public ResponseEntity<Team> joinTeam(@RequestBody Map<String, String> body,
                                         @AuthenticationPrincipal User currentUser) {
        String name     = body.get("name");
        String password = body.get("password");
        if (name == null || password == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(teamService.joinTeam(name, password, currentUser));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a team (Admin only)")
    public ResponseEntity<Team> updateTeam(@PathVariable UUID id,
                                           @Valid @RequestBody TeamRequest request,
                                           @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(teamService.updateTeam(admin, id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a team (Admin only)")
    public ResponseEntity<Void> deleteTeam(@PathVariable UUID id,
                                           @AuthenticationPrincipal User admin) {
        teamService.deleteTeam(admin, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a member to a team (Admin only)")
    public ResponseEntity<Team> addMember(@PathVariable UUID teamId, @PathVariable UUID userId) {
        return ResponseEntity.ok(teamService.addMember(teamId, userId));
    }

    @DeleteMapping("/{teamId}/members/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove a member from a team (Admin only)")
    public ResponseEntity<Team> removeMember(@PathVariable UUID teamId, @PathVariable UUID userId) {
        return ResponseEntity.ok(teamService.removeMember(teamId, userId));
    }
}