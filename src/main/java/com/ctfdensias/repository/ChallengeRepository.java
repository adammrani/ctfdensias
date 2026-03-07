package com.ctfdensias.repository;

import com.ctfdensias.model.Challenge;
import com.ctfdensias.model.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, UUID> {
    List<Challenge> findByIsActiveTrue();
    List<Challenge> findByCategory(String category);
    List<Challenge> findByDifficulty(Difficulty difficulty);
    List<Challenge> findByCompetitionId(UUID competitionId);
}
