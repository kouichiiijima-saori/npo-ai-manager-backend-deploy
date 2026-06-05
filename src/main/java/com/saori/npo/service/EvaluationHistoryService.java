package com.saori.npo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.saori.npo.domain.EvaluationHistory;
import com.saori.npo.mapper.EvaluationHistoryMapper;

@Service
public class EvaluationHistoryService {

    private final EvaluationHistoryMapper evaluationHistoryMapper;

    public EvaluationHistoryService(
            EvaluationHistoryMapper evaluationHistoryMapper) {
        this.evaluationHistoryMapper = evaluationHistoryMapper;
    }

    public List<EvaluationHistory> findAll() {
        return evaluationHistoryMapper.findAll();
    }

    public EvaluationHistory findById(Long id) {
        return evaluationHistoryMapper.findById(id);
    }
}