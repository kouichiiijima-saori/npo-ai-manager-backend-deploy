package com.saori.npo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

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
	private final AiEvaluationPromptBuilder aiEvaluationPromptBuilder;
	private final AiResultNormalizer aiResultNormalizer;
	private final AiSnapshotBuilder aiSnapshotBuilder;
	private final DummyAiEvaluationService dummyAiEvaluationService;

	public AiEvaluationService(
			GrantMasterMapper grantMasterMapper,
			GrantCaseMapper grantCaseMapper,
			EvaluationHistoryMapper evaluationHistoryMapper,
			GrantRequirementCheckMapper grantRequirementCheckMapper,
			AiEvaluationClient aiEvaluationClient,
			OrganizationProfileMapper organizationProfileMapper,
			CharterArticleMapper charterArticleMapper,
			ActivityRecordMapper activityRecordMapper,
			AiEvaluationPromptBuilder aiEvaluationPromptBuilder,
			AiResultNormalizer aiResultNormalizer,
			AiSnapshotBuilder aiSnapshotBuilder,
			DummyAiEvaluationService dummyAiEvaluationService) {

		this.grantMasterMapper = grantMasterMapper;
		this.grantCaseMapper = grantCaseMapper;
		this.evaluationHistoryMapper = evaluationHistoryMapper;
		this.grantRequirementCheckMapper = grantRequirementCheckMapper;
		this.aiEvaluationClient = aiEvaluationClient;
		this.organizationProfileMapper = organizationProfileMapper;
		this.charterArticleMapper = charterArticleMapper;
		this.activityRecordMapper = activityRecordMapper;
		this.aiEvaluationPromptBuilder = aiEvaluationPromptBuilder;
		this.aiResultNormalizer = aiResultNormalizer;
		this.aiSnapshotBuilder = aiSnapshotBuilder;
		this.dummyAiEvaluationService = dummyAiEvaluationService;
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
			String prompt = aiEvaluationPromptBuilder.build(
					organizationProfile,
					charterArticles,
					activityRecords,
					grantMaster);

			aiResult = aiEvaluationClient.evaluate(prompt);
			aiRawResponseMode = "gemini";

		} catch (Exception e) {

		    System.out.println("AI API failed. Fallback to dummy AI.");
		    e.printStackTrace();

		    Throwable rootCause = e;

		    while (rootCause.getCause() != null) {
		        rootCause = rootCause.getCause();
		    }

		    System.out.println("ROOT CAUSE CLASS : "
		            + rootCause.getClass().getName());

		    System.out.println("ROOT CAUSE MESSAGE : "
		            + rootCause.getMessage());

		    if (e instanceof HttpClientErrorException.TooManyRequests
		            || rootCause instanceof HttpClientErrorException.TooManyRequests) {

		        aiRawResponseMode = "dummy-fallback-429";

		    } else if (e instanceof HttpServerErrorException.ServiceUnavailable
		            || rootCause instanceof HttpServerErrorException.ServiceUnavailable) {

		        aiRawResponseMode = "dummy-fallback-503";

		    } else {

		        String rootMessage = rootCause.getMessage();

		        if (rootMessage != null && rootMessage.contains("429")) {

		            aiRawResponseMode = "dummy-fallback-429";

		        } else if (rootMessage != null && rootMessage.contains("503")) {

		            aiRawResponseMode = "dummy-fallback-503";

		        } else {

		            aiRawResponseMode = "dummy-fallback-error";
		        }
		    }
		}

		String aiSuitability;
		String aiRecommendationLevel;
		String aiReason;
		String aiEvidence;

		if (aiResult != null) {

			aiSuitability =
					aiResultNormalizer.normalizeSuitability(
							aiResult.getSuitability());

			aiRecommendationLevel =
					aiResultNormalizer.normalizeRecommendationLevel(
							aiResult.getRecommendationLevel());

			aiReason = safeText(
					aiResult.getReason());

			aiEvidence = safeText(
					aiResult.getEvidence());

		} else {

			aiSuitability =
					dummyAiEvaluationService.decideSuitability(
							request.getGrantMasterId());

			aiRecommendationLevel =
					dummyAiEvaluationService.decideRecommendationLevel(
							request.getGrantMasterId());

			aiReason =
					dummyAiEvaluationService.buildAiReason(aiSuitability);

			aiEvidence =
					dummyAiEvaluationService.buildAiEvidence(grantMaster);
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

			createRequirementChecks(
					grantCase.getId(),
					aiResult);
		}

		EvaluationHistory evaluationHistory = new EvaluationHistory();
		evaluationHistory.setGrantCaseId(grantCase.getId());
		evaluationHistory.setAiSuitability(aiSuitability);
		evaluationHistory.setAiRecommendationLevel(aiRecommendationLevel);
		evaluationHistory.setAiReason(aiReason);
		evaluationHistory.setAiEvidence(aiEvidence);
		evaluationHistory.setAdditionalChecks(
				aiResult != null && aiResult.getAdditionalChecks() != null
						? String.join("\n", aiResult.getAdditionalChecks())
						: null);
		evaluationHistory.setOrganizationSnapshot(
				aiSnapshotBuilder.buildOrganizationSnapshot(organizationProfile));
		evaluationHistory.setCharterSnapshot(
				aiSnapshotBuilder.buildCharterSnapshot(charterArticles));
		evaluationHistory.setActivitySnapshot(
				aiSnapshotBuilder.buildActivitySnapshot(activityRecords));
		evaluationHistory.setGrantSnapshot(
				aiSnapshotBuilder.buildGrantSnapshot(grantMaster));
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

	private String safeText(String value) {

		if (value == null || value.isBlank()) {
			return "未登録";
		}

		return value;
	}

	private void createRequirementChecks(
			Long grantCaseId,
			AiEvaluationResult aiResult) {

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

		if (aiResult != null && aiResult.getAdditionalChecks() != null) {

			for (String additionalCheck : aiResult.getAdditionalChecks()) {

				createRequirementCheck(
						grantCaseId,
						additionalCheck,
						null,
						"UNCHECKED",
						"AI判定により追加された確認事項");
			}
		}
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