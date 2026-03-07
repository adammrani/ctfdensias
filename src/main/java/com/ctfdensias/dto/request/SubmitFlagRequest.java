package com.ctfdensias.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class SubmitFlagRequest {

    @NotNull
    private UUID challengeId;

    @NotBlank
    private String flag;

    public SubmitFlagRequest() {}

    public UUID getChallengeId() { return challengeId; }
    public void setChallengeId(UUID challengeId) { this.challengeId = challengeId; }

    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }
}
