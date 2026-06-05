package com.saori.npo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.saori.npo.domain.EvaluationHistory;

@Mapper
public interface EvaluationHistoryMapper {

    List<EvaluationHistory> findAll();

    EvaluationHistory findById(Long id);

    int insert(EvaluationHistory evaluationHistory);

}