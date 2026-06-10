package com.saori.npo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saori.npo.client.GeminiAiEvaluationClient;

@RestController
public class GeminiTestController {

    private final GeminiAiEvaluationClient geminiAiEvaluationClient;

    public GeminiTestController(GeminiAiEvaluationClient geminiAiEvaluationClient) {
        this.geminiAiEvaluationClient = geminiAiEvaluationClient;
    }

    @GetMapping("/api/test/gemini")
    public String testGemini() {
        return geminiAiEvaluationClient.sendHello();
    }

}