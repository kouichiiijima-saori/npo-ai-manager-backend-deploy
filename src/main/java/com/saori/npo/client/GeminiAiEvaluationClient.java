package com.saori.npo.client;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.saori.npo.config.GeminiProperties;

@Component
public class GeminiAiEvaluationClient {

    private final GeminiProperties geminiProperties;
    private final RestClient restClient;

    public GeminiAiEvaluationClient(GeminiProperties geminiProperties) {
        this.geminiProperties = geminiProperties;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();
    }

    public String sendHello() {
        String path = "/v1beta/models/"
                + geminiProperties.getModel()
                + ":generateContent?key="
                + geminiProperties.getKey();

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", "こんにちは。短く返事してください。")
                                )
                        )
                )
        );

        Map response = restClient.post()
                .uri(path)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        return response.toString();
    }
}