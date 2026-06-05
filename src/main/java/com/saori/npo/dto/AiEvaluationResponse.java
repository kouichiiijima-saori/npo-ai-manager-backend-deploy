package com.saori.npo.dto;

import lombok.Data;

@Data
public class AiEvaluationResponse {

    private Long grantCaseId;

    private Long evaluationHistoryId;

    private String aiSuitability;

    private String aiRecommendationLevel;

    private String aiReason;

    private String aiEvidence;

    private String examinationStatus;

    private String externalAuditStatus;
}