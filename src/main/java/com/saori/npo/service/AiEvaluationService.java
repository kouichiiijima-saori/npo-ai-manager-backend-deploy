package com.saori.npo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saori.npo.client.AiEvaluationClient;
import com.saori.npo.domain.ActivityRecord;
import com.saori.npo.domain.CharterArticle;
import com.saori.npo.domain.EvaluationHistory;
import com.saori.npo.domain.GrantCase;
import com.saori.npo.domain.GrantMaster;
import com.saori.npo.domain.GrantRequirementCheck;
import com.saori.npo.domain.OrganizationProfile;
import com.saori.npo.dto.AiEvaluationRequest;
import com.saori.npo.dto.AiEvaluationResponse;
import com.saori.npo.dto.AiEvaluationResult;
import com.saori.npo.mapper.ActivityRecordMapper;
import com.saori.npo.mapper.CharterArticleMapper;
import com.saori.npo.mapper.EvaluationHistoryMapper;
import com.saori.npo.mapper.GrantCaseMapper;
import com.saori.npo.mapper.GrantMasterMapper;
import com.saori.npo.mapper.GrantRequirementCheckMapper;
import com.saori.npo.mapper.OrganizationProfileMapper;

@Service
public class AiEvaluationService {

	private final GrantMasterMapper grantMasterMapper;

	private final GrantCaseMapper grantCaseMapper;

	private final EvaluationHistoryMapper evaluationHistoryMapper;

	private final GrantRequirementCheckMapper grantRequirementCheckMapper;

	private final AiEvaluationClient aiEvaluationClient;

	private final OrganizationProfileMapper organizationProfileMapper;

	private final CharterArticleMapper charterArticleMapper;

	private final ActivityRecordMapper activityRecordMapper;

	public AiEvaluationService(
			GrantMasterMapper grantMasterMapper,
			GrantCaseMapper grantCaseMapper,
			EvaluationHistoryMapper evaluationHistoryMapper,
			GrantRequirementCheckMapper grantRequirementCheckMapper,
			AiEvaluationClient aiEvaluationClient,
			OrganizationProfileMapper organizationProfileMapper,
			CharterArticleMapper charterArticleMapper,
			ActivityRecordMapper activityRecordMapper) {

		this.grantMasterMapper = grantMasterMapper;
		this.grantCaseMapper = grantCaseMapper;
		this.evaluationHistoryMapper = evaluationHistoryMapper;
		this.grantRequirementCheckMapper = grantRequirementCheckMapper;
		this.aiEvaluationClient = aiEvaluationClient;
		this.organizationProfileMapper = organizationProfileMapper;
		this.charterArticleMapper = charterArticleMapper;
		this.activityRecordMapper = activityRecordMapper;
	}

	@Transactional
	public AiEvaluationResponse evaluate(
			AiEvaluationRequest request) {

		GrantMaster grantMaster = grantMasterMapper.findById(
				request.getGrantMasterId());

		OrganizationProfile organizationProfile = organizationProfileMapper.findById(
				request.getOrganizationId());

		List<CharterArticle> charterArticles = charterArticleMapper.findByOrganizationId(
				request.getOrganizationId());

		List<ActivityRecord> activityRecords = activityRecordMapper.findByOrganizationId(
				request.getOrganizationId());

		AiEvaluationResult aiResult = null;

		String aiRawResponseMode;

		try {
			String prompt = buildPrompt(
					organizationProfile,
					charterArticles,
					activityRecords,
					grantMaster);

			aiResult = aiEvaluationClient.evaluate(prompt);
			aiRawResponseMode = "gemini";

		} catch (Exception e) {

		    System.out.println("AI API failed. Fallback to dummy AI.");
		    e.printStackTrace();

		    String message = e.getMessage();

		    if (message != null && message.contains("429")) {
		        aiRawResponseMode = "dummy-fallback-429";
		    } else if (message != null && message.contains("503")) {
		        aiRawResponseMode = "dummy-fallback-503";
		    } else {
		        aiRawResponseMode = "dummy-fallback-error";
		    }
		}

		String aiSuitability;
		String aiRecommendationLevel;
		String aiReason;
		String aiEvidence;

		if (aiResult != null) {

			aiSuitability = normalizeSuitability(
					aiResult.getSuitability());

			aiRecommendationLevel = normalizeRecommendationLevel(
					aiResult.getRecommendationLevel());

			aiReason = safeText(
					aiResult.getReason());

			aiEvidence = safeText(
					aiResult.getEvidence());

		} else {

			aiSuitability = decideSuitability(
					request.getGrantMasterId());

			aiRecommendationLevel = decideRecommendationLevel(
					request.getGrantMasterId());

			aiReason = buildAiReason(aiSuitability);

			aiEvidence = buildAiEvidence(grantMaster);
		}

		GrantCase existingGrantCase = null;

		if (request.getGrantCaseId() != null) {

			existingGrantCase = grantCaseMapper.findById(
					request.getGrantCaseId());

		} else {

			existingGrantCase = grantCaseMapper.findByOrganizationIdAndGrantMasterId(
					request.getOrganizationId(),
					request.getGrantMasterId());
		}

		GrantCase grantCase;

		if (existingGrantCase != null) {

			grantCase = existingGrantCase;

		} else {

			grantCase = new GrantCase();
			grantCase.setOrganizationId(request.getOrganizationId());
			grantCase.setGrantMasterId(request.getGrantMasterId());
			grantCase.setCaseName(grantMaster.getTitle());
			grantCase.setCaseStage("APPLY_PREPARATION");
			grantCase.setExaminationStatus("UNCONFIRMED");
			grantCase.setExternalAuditStatus("NO_RESPONSE");
			grantCase.setExaminationMemo("AI判定結果をもとに確認してください。");
			grantCase.setNextAction("応募要件と提出書類を確認する");
			grantCase.setNextActionDueDate(grantMaster.getApplicationDeadline());
			grantCase.setArchived(false);

			grantCaseMapper.insert(grantCase);

			createRequirementChecks(grantCase.getId());
		}

		EvaluationHistory evaluationHistory = new EvaluationHistory();
		evaluationHistory.setGrantCaseId(grantCase.getId());
		evaluationHistory.setAiSuitability(aiSuitability);
		evaluationHistory.setAiRecommendationLevel(aiRecommendationLevel);
		evaluationHistory.setAiReason(aiReason);
		evaluationHistory.setAiEvidence(aiEvidence);
		evaluationHistory.setOrganizationSnapshot(
				buildOrganizationSnapshot(organizationProfile));
		evaluationHistory.setCharterSnapshot(
				buildCharterSnapshot(charterArticles));
		evaluationHistory.setActivitySnapshot(
				buildActivitySnapshot(activityRecords));
		evaluationHistory.setGrantSnapshot(
				buildGrantSnapshot(grantMaster));
		evaluationHistory.setAiRawResponse(
				"{\"mode\":\"" + aiRawResponseMode + "\"}");
		evaluationHistory.setReviewStatus("UNREVIEWED");
		evaluationHistory.setReviewMemo(null);
		evaluationHistory.setReviewedAt(null);

		evaluationHistoryMapper.insert(evaluationHistory);

		AiEvaluationResponse response = new AiEvaluationResponse();
		response.setGrantCaseId(grantCase.getId());
		response.setEvaluationHistoryId(evaluationHistory.getId());
		response.setAiSuitability(aiSuitability);
		response.setAiRecommendationLevel(aiRecommendationLevel);
		response.setAiReason(aiReason);
		response.setAiEvidence(aiEvidence);
		response.setExaminationStatus(grantCase.getExaminationStatus());
		response.setExternalAuditStatus(grantCase.getExternalAuditStatus());

		return response;
	}

	private String buildPrompt(
			OrganizationProfile organizationProfile,
			List<CharterArticle> charterArticles,
			List<ActivityRecord> activityRecords,
			GrantMaster grantMaster) {

		StringBuilder prompt = new StringBuilder();

		prompt.append("""
				あなたはNPO法人の助成金適合性を判定するAIです。
				最終判断は人間が行います。あなたは、団体情報・定款・活動実績・助成金情報を根拠に、応募検討のための参考判定を行ってください。

				必ず有効なJSONのみを返してください。
				説明文、Markdown、コードブロックは不要です。
				JSONのキーは必ずダブルクォートで囲ってください。

				suitability は "SUITABLE", "NEEDS_CONFIRMATION", "NOT_SUITABLE" のいずれか。
				recommendationLevel は "A", "B", "C" のいずれか。

				判定基準:
				- SUITABLE: 団体目的、活動実績、助成対象事業が明確に一致している
				- NEEDS_CONFIRMATION: 一部一致しているが、対象経費・対象地域・応募資格・実施体制など追加確認が必要
				- NOT_SUITABLE: 団体目的や活動実績と助成対象が大きく異なる

				注意：
				団体活動と助成金テーマが一部でも似ているだけで SUITABLE にしないでください。
				実際の活動実績・定款目的・対象地域・対象団体・必要書類が複数一致する場合のみ SUITABLE としてください。
				関連性が弱い場合は NOT_SUITABLE、判断材料が不足する場合は NEEDS_CONFIRMATION としてください。

				出力形式:
				{
				  "suitability": "SUITABLE",
				  "recommendationLevel": "A",
				  "reason": "判定理由を日本語で書く",
				  "evidence": "根拠を日本語で書く",
				  "additionalChecks": [
				    "追加確認事項1",
				    "追加確認事項2"
				  ]
				}

				【団体情報】
				""");

		if (organizationProfile != null) {
			prompt.append("団体名: ")
					.append(safeText(organizationProfile.getOrganizationName()))
					.append("\n");
			prompt.append("所在地: ")
					.append(safeText(organizationProfile.getLocation()))
					.append("\n");
			prompt.append("活動地域: ")
					.append(safeText(organizationProfile.getActivityArea()))
					.append("\n");
			prompt.append("ミッション: ")
					.append(safeText(organizationProfile.getMission()))
					.append("\n");
			prompt.append("対象者: ")
					.append(safeText(organizationProfile.getTargetPeople()))
					.append("\n");
			prompt.append("主な活動: ")
					.append(safeText(organizationProfile.getMainActivities()))
					.append("\n");
		} else {
			prompt.append("団体情報: 未登録\n");
		}

		prompt.append("\n【定款】\n");

		if (charterArticles == null || charterArticles.isEmpty()) {
			prompt.append("定款情報: 未登録\n");
		} else {
			for (CharterArticle article : charterArticles) {
				prompt.append("第")
						.append(article.getArticleNumber())
						.append("条 ")
						.append(safeText(article.getTitle()))
						.append(": ")
						.append(safeText(article.getContent()))
						.append("\n");
			}
		}

		prompt.append("\n【活動実績】\n");

		if (activityRecords == null || activityRecords.isEmpty()) {
			prompt.append("活動実績: 未登録\n");
		} else {
			for (ActivityRecord activity : activityRecords) {
				prompt.append(activity.getFiscalYear())
						.append("年度 ")
						.append(safeText(activity.getProjectName()))
						.append(": ")
						.append(safeText(activity.getContent()))
						.append(" 成果: ")
						.append(safeText(activity.getResult()))
						.append("\n");
			}
		}

		prompt.append("\n【助成金情報】\n");

		if (grantMaster != null) {
			prompt.append("助成金名: ")
					.append(safeText(grantMaster.getTitle()))
					.append("\n");
			prompt.append("提供団体: ")
					.append(safeText(grantMaster.getProvider()))
					.append("\n");
			prompt.append("概要: ")
					.append(safeText(grantMaster.getSummary()))
					.append("\n");
			prompt.append("対象テーマ: ")
					.append(safeText(grantMaster.getTargetTheme()))
					.append("\n");
			prompt.append("対象事業: ")
					.append(safeText(grantMaster.getTargetProject()))
					.append("\n");
			prompt.append("対象団体: ")
					.append(safeText(grantMaster.getTargetOrganization()))
					.append("\n");
			prompt.append("対象地域: ")
					.append(safeText(grantMaster.getTargetArea()))
					.append("\n");
			prompt.append("必要書類: ")
					.append(safeText(grantMaster.getRequiredDocuments()))
					.append("\n");
		} else {
			prompt.append("助成金情報: 未登録\n");
		}

		return prompt.toString();
	}

	private String normalizeSuitability(String value) {

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

	private String normalizeRecommendationLevel(String value) {

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

	private String buildOrganizationSnapshot(
			OrganizationProfile organizationProfile) {

		if (organizationProfile == null) {
			return "{\"organization\":\"not_found\"}";
		}

		return "{\"organizationId\":"
				+ organizationProfile.getId()
				+ ",\"organizationName\":\""
				+ safeJsonText(organizationProfile.getOrganizationName())
				+ "\"}";
	}

	private String buildCharterSnapshot(
			List<CharterArticle> charterArticles) {

		int count = 0;

		if (charterArticles != null) {
			count = charterArticles.size();
		}

		return "{\"source\":\"charter_articles\",\"count\":"
				+ count
				+ "}";
	}

	private String buildActivitySnapshot(
			List<ActivityRecord> activityRecords) {

		int count = 0;

		if (activityRecords != null) {
			count = activityRecords.size();
		}

		return "{\"source\":\"activity_records\",\"count\":"
				+ count
				+ "}";
	}

	private String buildGrantSnapshot(
			GrantMaster grantMaster) {

		if (grantMaster == null) {
			return "{\"grantMaster\":\"not_found\"}";
		}

		return "{\"grantMasterId\":"
				+ grantMaster.getId()
				+ ",\"title\":\""
				+ safeJsonText(grantMaster.getTitle())
				+ "\"}";
	}

	private String decideSuitability(Long grantMasterId) {

		if (grantMasterId == 1L) {
			return "SUITABLE";
		}

		if (grantMasterId == 2L) {
			return "NEEDS_CONFIRMATION";
		}

		return "NOT_SUITABLE";
	}

	private String decideRecommendationLevel(Long grantMasterId) {

		if (grantMasterId == 1L) {
			return "A";
		}

		if (grantMasterId == 2L) {
			return "B";
		}

		return "C";
	}

	private String buildAiReason(String aiSuitability) {

		if ("SUITABLE".equals(aiSuitability)) {
			return "団体の目的と活動実績が助成対象事業と高く一致しています。";
		}

		if ("NEEDS_CONFIRMATION".equals(aiSuitability)) {
			return "事業内容は近いものの、対象経費や提出資料の確認が必要です。";
		}

		return "現時点では助成対象事業との整合性に不足があります。";
	}

	private String buildAiEvidence(GrantMaster grantMaster) {

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

	private String safeJsonText(String value) {

		if (value == null || value.isBlank()) {
			return "";
		}

		return value
				.replace("\\", "\\\\")
				.replace("\"", "\\\"");
	}

	private void createRequirementChecks(Long grantCaseId) {

		createRequirementCheck(
				grantCaseId,
				"事業計画書",
				"2026_business_plan.pdf",
				"UNCHECKED",
				"AI判定により確認項目として追加");

		createRequirementCheck(
				grantCaseId,
				"収支計画書",
				"2026_budget_plan.pdf",
				"UNCHECKED",
				"AI判定により確認項目として追加");

		createRequirementCheck(
				grantCaseId,
				"活動報告書",
				"2025_activity_report.pdf",
				"UNCHECKED",
				"AI判定により確認項目として追加");
	}

	private void createRequirementCheck(
			Long grantCaseId,
			String requirementName,
			String targetFileName,
			String checkStatus,
			String checkMemo) {

		GrantRequirementCheck requirementCheck = new GrantRequirementCheck();

		requirementCheck.setGrantCaseId(grantCaseId);
		requirementCheck.setRequirementName(requirementName);
		requirementCheck.setTargetFileName(targetFileName);
		requirementCheck.setCheckStatus(checkStatus);
		requirementCheck.setCheckMemo(checkMemo);
		requirementCheck.setArchived(false);

		grantRequirementCheckMapper.insert(requirementCheck);
	}
}