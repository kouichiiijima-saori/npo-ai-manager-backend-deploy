package com.saori.npo.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class EvaluationHistory {

    private Long id;

    private Long grantCaseId;

    private String aiSuitability;

    private String aiRecommendationLevel;

    private String aiReason;

    private String aiEvidence;

    private String organizationSnapshot;

    private String charterSnapshot;

    private String activitySnapshot;

    private String grantSnapshot;

    private String aiRawResponse;

    private LocalDateTime evaluatedAt;
}