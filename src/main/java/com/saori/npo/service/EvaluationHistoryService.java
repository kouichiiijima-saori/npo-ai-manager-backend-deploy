package com.saori.npo.service;

import java.time.LocalDateTime;
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

    public EvaluationHistory updateReviewStatus(
            Long id,
            String reviewStatus,
            String reviewMemo) {

        EvaluationHistory evaluationHistory =
                evaluationHistoryMapper.findById(id);

        if (evaluationHistory == null) {
            throw new IllegalArgumentException("指定されたAI判定履歴が見つかりません。");
        }

        evaluationHistory.setReviewStatus(reviewStatus);
        evaluationHistory.setReviewMemo(reviewMemo);
        evaluationHistory.setReviewedAt(LocalDateTime.now());

        evaluationHistoryMapper.update(evaluationHistory);

        return evaluationHistoryMapper.findById(id);
    }
}