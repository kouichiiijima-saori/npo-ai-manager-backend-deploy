package com.saori.npo.client;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.saori.npo.config.GeminiProperties;
import com.saori.npo.dto.AiEvaluationResult;

@Component
public class GeminiAiEvaluationClient implements AiEvaluationClient {

	private final GeminiProperties geminiProperties;
	private final RestClient restClient;
	private final AiEvaluationResponseParser aiEvaluationResponseParser;
	private final AiRetryExecutor aiRetryExecutor;

	public GeminiAiEvaluationClient(
			GeminiProperties geminiProperties,
			AiEvaluationResponseParser aiEvaluationResponseParser,
			AiRetryExecutor aiRetryExecutor) {

		this.geminiProperties = geminiProperties;
		this.aiEvaluationResponseParser = aiEvaluationResponseParser;
		this.aiRetryExecutor = aiRetryExecutor;
		this.restClient = RestClient.builder()
				.baseUrl("https://generativelanguage.googleapis.com")
				.build();
	}

	@Override
	public AiEvaluationResult evaluate(String prompt) {

		try {
			String responseText =
					aiRetryExecutor.execute(
							() -> sendPrompt(prompt));

			return aiEvaluationResponseParser.parse(responseText);

		} catch (Exception e) {
			throw new IllegalStateException(
					"GeminiのAI判定に失敗しました。",
					e);
		}
	}

	public String sendHello() {
		return aiRetryExecutor.execute(
				() -> sendPrompt("こんにちは。短く返事してください。"));
	}

	private String sendPrompt(String prompt) {

		String path = "/v1beta/models/"
				+ geminiProperties.getModel()
				+ ":generateContent?key="
				+ geminiProperties.getKey();

		Map<String, Object> requestBody = Map.of(
				"contents", List.of(
						Map.of(
								"parts", List.of(
										Map.of("text", prompt)))));

		Map response = restClient.post()
				.uri(path)
				.body(requestBody)
				.retrieve()
				.body(Map.class);

		return response.toString();
	}

}