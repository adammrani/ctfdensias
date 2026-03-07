package com.ctfdensias.controller;

import com.ctfdensias.dto.request.ChallengeRequest;
import com.ctfdensias.model.Challenge;
import com.ctfdensias.model.Hint;
import com.ctfdensias.service.ChallengeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/challenges")
@Tag(name = "Challenges")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @GetMapping
    @Operation(summary = "List all active challenges (public)")
    public ResponseEntity<List<Challenge>> getActiveChallenges() {
        return ResponseEntity.ok(challengeService.getActiveChallenges());
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "List ALL challenges including inactive (Admin)")
    public ResponseEntity<List<Challenge>> getAllChallenges() {
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get challenge by ID")
    public ResponseEntity<Challenge> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(challengeService.getChallengeById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create challenge (Admin)")
    public ResponseEntity<Challenge> create(@Valid @RequestBody ChallengeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(challengeService.createChallenge(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update challenge (Admin)")
    public ResponseEntity<Challenge> update(@PathVariable UUID id, @Valid @RequestBody ChallengeRequest request) {
        return ResponseEntity.ok(challengeService.updateChallenge(id, request));
    }

    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Enable/disable a challenge (Admin)")
    public ResponseEntity<Map<String,Object>> toggle(@PathVariable UUID id) {
        Challenge c = challengeService.getChallengeById(id);
        c.setIsActive(!Boolean.TRUE.equals(c.getIsActive()));
        challengeService.saveChallenge(c);
        return ResponseEntity.ok(Map.of("id", c.getId(), "isActive", c.getIsActive()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete challenge (Admin)")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        challengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }

    // ---- Flags ----

    @PostMapping("/{id}/flags")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add a flag to challenge — stored as SHA-256 hash (Admin)")
    public ResponseEntity<Map<String,String>> addFlag(@PathVariable UUID id, @RequestParam String flag) {
        challengeService.addFlag(id, flag);
        return ResponseEntity.ok(Map.of("message", "Flag added and hashed successfully"));
    }

    @DeleteMapping("/{challengeId}/flags/{flagId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Remove a flag (Admin)")
    public ResponseEntity<Void> removeFlag(@PathVariable UUID challengeId, @PathVariable UUID flagId) {
        challengeService.removeFlag(challengeId, flagId);
        return ResponseEntity.noContent().build();
    }

    // ---- Hints ----

    @PostMapping("/{id}/hints")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Add a hint to challenge (Admin)")
    public ResponseEntity<Hint> addHint(@PathVariable UUID id,
                                         @RequestParam String content,
                                         @RequestParam(defaultValue = "0") int cost) {
        return ResponseEntity.status(HttpStatus.CREATED).body(challengeService.addHint(id, content, cost));
    }

    @GetMapping("/hints/{hintId}/reveal")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Reveal hint content (authenticated users)")
    public ResponseEntity<Map<String,String>> revealHint(@PathVariable UUID hintId) {
        return ResponseEntity.ok(Map.of("content", challengeService.revealHint(hintId)));
    }
}
