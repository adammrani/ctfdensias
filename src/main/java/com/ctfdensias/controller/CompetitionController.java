package com.ctfdensias.controller;

import com.ctfdensias.dto.request.CompetitionRequest;
import com.ctfdensias.model.Competition;
import com.ctfdensias.model.User;
import com.ctfdensias.repository.CompetitionRepository;
import com.ctfdensias.service.AdminService;
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
@RequestMapping("/api/competitions")
@Tag(name = "Competitions")
public class CompetitionController {

    private final AdminService adminService;
    private final CompetitionRepository competitionRepository;

    public CompetitionController(AdminService adminService, CompetitionRepository competitionRepository) {
        this.adminService = adminService;
        this.competitionRepository = competitionRepository;
    }

    @GetMapping
    @Operation(summary = "Get all active competitions")
    public ResponseEntity<List<Competition>> getActive() {
        return ResponseEntity.ok(competitionRepository.findByIsActiveTrue());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create competition (Admin)")
    public ResponseEntity<Competition> create(@Valid @RequestBody CompetitionRequest request,
                                               @AuthenticationPrincipal User admin) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.setupCompetition(admin, request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update competition (Admin)")
    public ResponseEntity<Competition> update(@PathVariable UUID id,
                                               @Valid @RequestBody CompetitionRequest request,
                                               @AuthenticationPrincipal User admin) {
        return ResponseEntity.ok(adminService.updateCompetition(admin, id, request));
    }

    @PatchMapping("/{id}/scoreboard")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Toggle scoreboard visibility (Admin)")
    public ResponseEntity<Map<String,Object>> toggleScoreboard(@PathVariable UUID id,
                                                                @RequestParam boolean visible) {
        Competition comp = competitionRepository.findById(id)
                .orElseThrow(() -> new com.ctfdensias.exception.ResourceNotFoundException("Competition not found"));
        comp.setIsScoreboardVisible(visible);
        competitionRepository.save(comp);
        return ResponseEntity.ok(Map.of("isScoreboardVisible", visible,
                "message", visible ? "Scoreboard is now PUBLIC" : "Scoreboard is now HIDDEN"));
    }
}
