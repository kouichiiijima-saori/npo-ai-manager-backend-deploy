package com.saori.npo.client;

import com.saori.npo.dto.AiEvaluationResult;

public interface AiEvaluationClient {

    AiEvaluationResult evaluate(String prompt);

}