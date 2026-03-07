package com.ctfdensias.repository;

import com.ctfdensias.model.Flag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlagRepository extends JpaRepository<Flag, UUID> {
    List<Flag> findByChallengeId(UUID challengeId);
}
