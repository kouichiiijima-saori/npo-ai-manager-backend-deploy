package com.saori.npo.client;

import org.springframework.stereotype.Component;

import com.saori.npo.dto.AiEvaluationResult;

import tools.jackson.databind.ObjectMapper;

@Component
public class AiEvaluationResponseParser {

	private final ObjectMapper objectMapper;

	public AiEvaluationResponseParser(
			ObjectMapper objectMapper) {

		this.objectMapper = objectMapper;
	}

	public AiEvaluationResult parse(String responseText) {

		try {
			String extractedText = extractTextFromResponse(responseText);
			String jsonText = cleanJsonText(extractedText);

			return objectMapper.readValue(
					jsonText,
					AiEvaluationResult.class);

		} catch (Exception e) {
			throw new IllegalStateException(
					"AI応答JSONの解析に失敗しました。",
					e);
		}
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