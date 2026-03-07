package com.ctfdensias.service.impl;

import com.ctfdensias.dto.request.SubmitFlagRequest;
import com.ctfdensias.dto.response.SubmissionResponse;
import com.ctfdensias.exception.ResourceNotFoundException;
import com.ctfdensias.model.*;
import com.ctfdensias.repository.*;
import com.ctfdensias.service.SubmissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SubmissionServiceImpl implements SubmissionService {

    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;
    private final SolveRepository solveRepository;

    // Default decay configuration — could be stored in DB via DynamicScoring entity
    private final DynamicScoring dynamicScoring = new DynamicScoring(0.08);

    public SubmissionServiceImpl(ChallengeRepository challengeRepository,
                                  SubmissionRepository submissionRepository,
                                  SolveRepository solveRepository) {
        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
        this.solveRepository = solveRepository;
    }

    @Override
    @Transactional
    public SubmissionResponse submitFlag(SubmitFlagRequest request, User currentUser) {
        Challenge challenge = challengeRepository.findById(request.getChallengeId())
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));

        // Check if already solved
        if (solveRepository.existsByUserIdAndChallengeId(currentUser.getId(), challenge.getId())) {
            return new SubmissionResponse(null, false, "You already solved this challenge!", null);
        }

        // Record submission
        Submission submission = new Submission();
        submission.setUser(currentUser);
        submission.setChallenge(challenge);
        submission.setSubmittedFlag(request.getFlag());
        submission.processSubmission(); // validates flag

        submissionRepository.save(submission);

        if (Boolean.TRUE.equals(submission.getIsCorrect())) {
            // Calculate points with dynamic decay
            int solveCount = (int) submissionRepository.countByChallengeIdAndIsCorrectTrue(challenge.getId());
            int points = dynamicScoring.calculateNewPoints(challenge, solveCount - 1);

            // Apply decay to challenge's current point value for future solvers
            dynamicScoring.applyDecay(challenge, solveCount);
            challengeRepository.save(challenge);

            // Record the solve
            Solve solve = new Solve();
            solve.setUser(currentUser);
            solve.setChallenge(challenge);
            solve.setAwardedPoints(points);
            solveRepository.save(solve);

            return new SubmissionResponse(submission.getId(), true,
                    "🚩 Correct flag! Well done!", points);
        } else {
            return new SubmissionResponse(submission.getId(), false,
                    "❌ Wrong flag. Try again!", null);
        }
    }
}
