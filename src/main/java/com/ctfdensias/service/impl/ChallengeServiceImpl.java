package com.ctfdensias.service.impl;

import com.ctfdensias.dto.request.ChallengeRequest;
import com.ctfdensias.exception.ResourceNotFoundException;
import com.ctfdensias.model.Challenge;
import com.ctfdensias.model.Competition;
import com.ctfdensias.model.Flag;
import com.ctfdensias.model.Hint;
import com.ctfdensias.repository.ChallengeRepository;
import com.ctfdensias.repository.CompetitionRepository;
import com.ctfdensias.repository.FlagRepository;
import com.ctfdensias.service.ChallengeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final CompetitionRepository competitionRepository;
    private final FlagRepository flagRepository;

    public ChallengeServiceImpl(ChallengeRepository challengeRepository,
                                 CompetitionRepository competitionRepository,
                                 FlagRepository flagRepository) {
        this.challengeRepository = challengeRepository;
        this.competitionRepository = competitionRepository;
        this.flagRepository = flagRepository;
    }

    @Override
    public List<Challenge> getActiveChallenges() {
        return challengeRepository.findByIsActiveTrue();
    }

    @Override
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    @Override
    public Challenge getChallengeById(UUID id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found: " + id));
    }

    @Override
    @Transactional
    public Challenge createChallenge(ChallengeRequest request, String fileUrl, String fileName) {
        Challenge challenge = new Challenge();
        mapRequestToChallenge(request, challenge);
        challenge.setPoints(request.getInitialPoints());
        challenge.setFileUrl(fileUrl);
        challenge.setFileName(fileName);
        return challengeRepository.save(challenge);
    }

    @Override
    @Transactional
    public Challenge updateChallenge(UUID id, ChallengeRequest request) {
        Challenge challenge = getChallengeById(id);
        mapRequestToChallenge(request, challenge);
        return challengeRepository.save(challenge);
    }

    @Override
    @Transactional
    public Challenge saveChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    @Override
    @Transactional
    public void deleteChallenge(UUID id) {
        challengeRepository.delete(getChallengeById(id));
    }

    @Override
    @Transactional
    public Challenge addFlag(UUID challengeId, String rawFlag) {
        Challenge challenge = getChallengeById(challengeId);
        Flag flag = new Flag();
        flag.setRawFlag(rawFlag);
        flag.setChallenge(challenge);
        flagRepository.save(flag);
        challenge.getFlags().add(flag);
        return challenge;
    }

    @Override
    @Transactional
    public void removeFlag(UUID challengeId, UUID flagId) {
        Flag flag = flagRepository.findById(flagId)
                .orElseThrow(() -> new ResourceNotFoundException("Flag not found: " + flagId));
        flagRepository.delete(flag);
    }

    @Override
    @Transactional
    public Hint addHint(UUID challengeId, String content, int cost) {
        Challenge challenge = getChallengeById(challengeId);
        Hint hint = new Hint();
        hint.setContent(content);
        hint.setCost(cost);
        hint.setChallenge(challenge);
        challenge.getHints().add(hint);
        challengeRepository.save(challenge);
        return hint;
    }

    @Override
    @Transactional
    public String revealHint(UUID hintId) {
        return challengeRepository.findAll().stream()
                .flatMap(c -> c.getHints().stream())
                .filter(h -> h.getId().equals(hintId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Hint not found"))
                .reveal();
    }

    private void mapRequestToChallenge(ChallengeRequest request, Challenge challenge) {
        challenge.setTitle(request.getTitle());
        challenge.setDescription(request.getDescription());
        challenge.setCategory(request.getCategory());
        challenge.setDifficulty(request.getDifficulty());
        challenge.setInitialPoints(request.getInitialPoints());
        challenge.setMinimumPoints(request.getMinimumPoints());
        if (request.getCompetitionId() != null) {
            Competition competition = competitionRepository.findById(request.getCompetitionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Competition not found"));
            challenge.setCompetition(competition);
        }
    }
}