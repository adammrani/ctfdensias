package com.ctfdensias.service;

import com.ctfdensias.dto.request.ChallengeRequest;
import com.ctfdensias.model.Challenge;
import com.ctfdensias.model.Hint;

import java.util.List;
import java.util.UUID;

public interface ChallengeService {
    List<Challenge> getActiveChallenges();
    List<Challenge> getAllChallenges();
    Challenge getChallengeById(UUID id);
    Challenge createChallenge(ChallengeRequest request);
    Challenge updateChallenge(UUID id, ChallengeRequest request);
    Challenge saveChallenge(Challenge challenge);
    void deleteChallenge(UUID id);
    Challenge addFlag(UUID challengeId, String rawFlag);
    void removeFlag(UUID challengeId, UUID flagId);
    Hint addHint(UUID challengeId, String content, int cost);
    String revealHint(UUID hintId);
}
