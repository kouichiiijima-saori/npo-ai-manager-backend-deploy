package com.saori.npo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.saori.npo.dto.AiEvaluationRequest;
import com.saori.npo.dto.AiEvaluationResponse;
import com.saori.npo.service.AiEvaluationService;

@RestController
public class AiEvaluationController {

    private final AiEvaluationService aiEvaluationService;

    public AiEvaluationController(
            AiEvaluationService aiEvaluationService) {
        this.aiEvaluationService = aiEvaluationService;
    }

    @PostMapping("/api/ai-evaluations")
    public AiEvaluationResponse evaluate(
            @RequestBody AiEvaluationRequest request) {

        return aiEvaluationService.evaluate(request);
    }
}