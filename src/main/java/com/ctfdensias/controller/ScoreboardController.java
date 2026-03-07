package com.ctfdensias.controller;

import com.ctfdensias.dto.response.ScoreboardEntry;
import com.ctfdensias.service.ScoreboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scoreboard")
@Tag(name = "Scoreboard")
public class ScoreboardController {

    private final ScoreboardService scoreboardService;

    public ScoreboardController(ScoreboardService scoreboardService) {
        this.scoreboardService = scoreboardService;
    }

    @GetMapping
    @Operation(summary = "Live scoreboard (hidden if admin disabled it)")
    public ResponseEntity<List<ScoreboardEntry>> getScoreboard() {
        return ResponseEntity.ok(scoreboardService.getScoreboard());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Scoreboard always visible to admin")
    public ResponseEntity<List<ScoreboardEntry>> getScoreboardAdmin() {
        return ResponseEntity.ok(scoreboardService.getScoreboardAdmin());
    }
}
