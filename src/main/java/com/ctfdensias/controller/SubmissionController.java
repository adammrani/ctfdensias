package com.ctfdensias.controller;

import com.ctfdensias.dto.request.SubmitFlagRequest;
import com.ctfdensias.dto.response.SubmissionResponse;
import com.ctfdensias.model.User;
import com.ctfdensias.service.SubmissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@Tag(name = "Submissions", description = "Flag submission and validation")
@SecurityRequirement(name = "bearerAuth")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    @Operation(summary = "Submit a flag for a challenge")
    public ResponseEntity<SubmissionResponse> submitFlag(
            @Valid @RequestBody SubmitFlagRequest request,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(submissionService.submitFlag(request, currentUser));
    }
}
