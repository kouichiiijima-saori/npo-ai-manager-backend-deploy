package com.saori.npo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.saori.npo.domain.EvaluationHistory;
import com.saori.npo.service.EvaluationHistoryService;

@RestController
public class EvaluationHistoryController {

    private final EvaluationHistoryService evaluationHistoryService;

    public EvaluationHistoryController(
            EvaluationHistoryService evaluationHistoryService) {
        this.evaluationHistoryService = evaluationHistoryService;
    }

    @GetMapping("/api/evaluation-histories")
    public List<EvaluationHistory> getEvaluationHistories() {
        return evaluationHistoryService.findAll();
    }

    @GetMapping("/api/evaluation-histories/{id}")
    public EvaluationHistory getEvaluationHistory(
            @PathVariable Long id) {
        return evaluationHistoryService.findById(id);
    }
}