package com.ctfdensias.repository;

import com.ctfdensias.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    List<Submission> findByUserId(UUID userId);
    List<Submission> findByChallengeId(UUID challengeId);
    boolean existsByUserIdAndChallengeIdAndIsCorrectTrue(UUID userId, UUID challengeId);
    long countByChallengeIdAndIsCorrectTrue(UUID challengeId);
}
