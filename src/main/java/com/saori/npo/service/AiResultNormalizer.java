package com.saori.npo.service;

import org.springframework.stereotype.Component;

@Component
public class AiResultNormalizer {

	public String normalizeSuitability(
			String value) {

		if ("SUITABLE".equals(value)) {
			return "SUITABLE";
		}

		if ("NEEDS_CONFIRMATION".equals(value)) {
			return "NEEDS_CONFIRMATION";
		}

		if ("NEED_CONFIRM".equals(value)) {
			return "NEEDS_CONFIRMATION";
		}

		if ("NOT_SUITABLE".equals(value)) {
			return "NOT_SUITABLE";
		}

		if ("UNSUITABLE".equals(value)) {
			return "NOT_SUITABLE";
		}

		return "NEEDS_CONFIRMATION";
	}

	public String normalizeRecommendationLevel(
			String value) {

		if ("A".equals(value)) {
			return "A";
		}

		if ("B".equals(value)) {
			return "B";
		}

		if ("C".equals(value)) {
			return "C";
		}

		return "B";
	}
}