package com.saori.npo.client;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.saori.npo.config.GeminiProperties;
import com.saori.npo.dto.AiEvaluationResult;

import tools.jackson.databind.ObjectMapper;

@Component
public class GeminiAiEvaluationClient implements AiEvaluationClient {

	private final GeminiProperties geminiProperties;

	private final RestClient restClient;

	private final ObjectMapper objectMapper;

	public GeminiAiEvaluationClient(
			GeminiProperties geminiProperties,
			ObjectMapper objectMapper) {

		this.geminiProperties = geminiProperties;
		this.objectMapper = objectMapper;
		this.restClient = RestClient.builder()
				.baseUrl("https://generativelanguage.googleapis.com")
				.build();
	}

	@Override
	public AiEvaluationResult evaluate(String prompt) {

		try {
			String responseText = sendPromptWithRetry(prompt);
			String extractedText = extractTextFromResponse(responseText);
			String jsonText = cleanJsonText(extractedText);

			return objectMapper.readValue(
					jsonText,
					AiEvaluationResult.class);

		} catch (Exception e) {
			throw new IllegalStateException(
					"GeminiのAI判定に失敗しました。",
					e);
		}
	}

	public String sendHello() {
		return sendPromptWithRetry("こんにちは。短く返事してください。");
	}

	private String sendPromptWithRetry(String prompt) {

		RuntimeException lastException = null;

		for (int attempt = 1; attempt <= 3; attempt++) {
			try {
				return sendPrompt(prompt);
			} catch (RuntimeException e) {
				lastException = e;

				if (e instanceof HttpClientErrorException.TooManyRequests) {
					System.out.println("Gemini API quota exceeded. No retry.");
					throw e;
				}

				System.out.println("Gemini API retry attempt failed: " + attempt);
				System.out.println(e.getMessage());

				if (attempt < 3) {

					long waitMillis = (long) Math.pow(2, attempt - 1) * 6000L;

					System.out.println(
							"Gemini API retry after "
									+ (waitMillis / 1000)
									+ " sec. attempt="
									+ attempt);

					sleep(waitMillis);
				}
			}
		}

		throw lastException;
	}

	private void sleep(long millis) {

		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
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

	private String extractTextFromResponse(String responseText) {

		int textStart = responseText.indexOf("text=");

		if (textStart == -1) {
			return responseText;
		}

		int start = textStart + "text=".length();
		int end = responseText.indexOf("}], role=model", start);

		if (end == -1) {
			return responseText.substring(start);
		}

		return responseText.substring(start, end);
	}

	private String cleanJsonText(String text) {

		return text
				.replace("```json", "")
				.replace("```", "")
				.trim();
	}
}