package com.ctfdensias.repository;

import com.ctfdensias.model.Solve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SolveRepository extends JpaRepository<Solve, UUID> {
    List<Solve> findByUserId(UUID userId);
    List<Solve> findByChallengeId(UUID challengeId);
    boolean existsByUserIdAndChallengeId(UUID userId, UUID challengeId);

    @Query("SELECT s.user.id, SUM(s.awardedPoints) as total FROM Solve s GROUP BY s.user.id ORDER BY total DESC")
    List<Object[]> getUserScoreboard();
}
