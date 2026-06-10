package com.saori.npo.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiEvaluationResult {

    private String suitability;

    private String recommendationLevel;

    private String reason;

    private String evidence;

    private List<String> additionalChecks;
}