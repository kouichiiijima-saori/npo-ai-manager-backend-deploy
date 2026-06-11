package com.saori.npo.service;

import org.springframework.stereotype.Component;

import com.saori.npo.domain.GrantMaster;

@Component
public class DummyAiEvaluationService {

	public String decideSuitability(Long grantMasterId) {

		if (grantMasterId == 1L) {
			return "SUITABLE";
		}

		if (grantMasterId == 2L) {
			return "NEEDS_CONFIRMATION";
		}

		return "NOT_SUITABLE";
	}

	public String decideRecommendationLevel(Long grantMasterId) {

		if (grantMasterId == 1L) {
			return "A";
		}

		if (grantMasterId == 2L) {
			return "B";
		}

		return "C";
	}

	public String buildAiReason(String aiSuitability) {

		if ("SUITABLE".equals(aiSuitability)) {
			return "団体の目的と活動実績が助成対象事業と高く一致しています。";
		}

		if ("NEEDS_CONFIRMATION".equals(aiSuitability)) {
			return "事業内容は近いものの、対象経費や提出資料の確認が必要です。";
		}

		return "現時点では助成対象事業との整合性に不足があります。";
	}

	public String buildAiEvidence(GrantMaster grantMaster) {

		if (grantMaster == null) {
			return "助成金情報が確認できませんでした。";
		}

		return "助成金テーマ：" + safeText(grantMaster.getTargetTheme())
				+ " / 対象事業：" + safeText(grantMaster.getTargetProject())
				+ " / 必要書類：" + safeText(grantMaster.getRequiredDocuments());
	}

	private String safeText(String value) {

		if (value == null || value.isBlank()) {
			return "未登録";
		}

		return value;
	}
}