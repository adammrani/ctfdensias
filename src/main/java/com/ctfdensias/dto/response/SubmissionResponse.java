package com.ctfdensias.dto.response;

import java.util.UUID;

public class SubmissionResponse {
    private UUID submissionId;
    private boolean correct;
    private String message;
    private Integer pointsAwarded;

    public SubmissionResponse() {}
    public SubmissionResponse(UUID submissionId, boolean correct, String message, Integer pointsAwarded) {
        this.submissionId = submissionId;
        this.correct = correct;
        this.message = message;
        this.pointsAwarded = pointsAwarded;
    }

    public UUID getSubmissionId() { return submissionId; }
    public void setSubmissionId(UUID submissionId) { this.submissionId = submissionId; }
    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Integer getPointsAwarded() { return pointsAwarded; }
    public void setPointsAwarded(Integer pointsAwarded) { this.pointsAwarded = pointsAwarded; }
}
