package com.saori.npo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saori.npo.domain.EvaluationHistory;
import com.saori.npo.domain.GrantCase;
import com.saori.npo.domain.GrantMaster;
import com.saori.npo.domain.GrantRequirementCheck;
import com.saori.npo.dto.AiEvaluationRequest;
import com.saori.npo.dto.AiEvaluationResponse;
import com.saori.npo.mapper.EvaluationHistoryMapper;
import com.saori.npo.mapper.GrantCaseMapper;
import com.saori.npo.mapper.GrantMasterMapper;
import com.saori.npo.mapper.GrantRequirementCheckMapper;

@Service
public class AiEvaluationService {

    private final GrantMasterMapper grantMasterMapper;

    private final GrantCaseMapper grantCaseMapper;

    private final EvaluationHistoryMapper evaluationHistoryMapper;

    private final GrantRequirementCheckMapper grantRequirementCheckMapper;

    public AiEvaluationService(
            GrantMasterMapper grantMasterMapper,
            GrantCaseMapper grantCaseMapper,
            EvaluationHistoryMapper evaluationHistoryMapper,
            GrantRequirementCheckMapper grantRequirementCheckMapper) {
        this.grantMasterMapper = grantMasterMapper;
        this.grantCaseMapper = grantCaseMapper;
        this.evaluationHistoryMapper = evaluationHistoryMapper;
        this.grantRequirementCheckMapper = grantRequirementCheckMapper;
    }

    @Transactional
    public AiEvaluationResponse evaluate(
            AiEvaluationRequest request) {

        GrantMaster grantMaster = grantMasterMapper.findById(
                request.getGrantMasterId());

        String aiSuitability = decideSuitability(
                request.getGrantMasterId());

        String aiRecommendationLevel = decideRecommendationLevel(
                request.getGrantMasterId());

        String aiReason = buildAiReason(aiSuitability);

        String aiEvidence = buildAiEvidence(grantMaster);

        GrantCase existingGrantCase = null;

        if (request.getGrantCaseId() != null) {

            existingGrantCase = grantCaseMapper.findById(
                    request.getGrantCaseId());

        } else {

            existingGrantCase =
                    grantCaseMapper.findByOrganizationIdAndGrantMasterId(
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
        evaluationHistory.setOrganizationSnapshot("{\"organizationId\":"
                + request.getOrganizationId() + "}");
        evaluationHistory.setCharterSnapshot("{\"source\":\"charter_articles\"}");
        evaluationHistory.setActivitySnapshot("{\"source\":\"activity_records\"}");
        evaluationHistory.setGrantSnapshot("{\"grantMasterId\":"
                + request.getGrantMasterId() + "}");
        evaluationHistory.setAiRawResponse("{\"mode\":\"dummy-ai\"}");
        evaluationHistory.setReviewStatus("UNREVIEWED");
        evaluationHistory.setReviewMemo(null);
        evaluationHistory.setReviewedAt(null);

        evaluationHistoryMapper.insert(evaluationHistory);

        createRequirementChecks(grantCase.getId());

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

    private String decideSuitability(Long grantMasterId) {

        if (grantMasterId == 1L) {
            return "SUITABLE";
        }

        if (grantMasterId == 2L) {
            return "NEED_CONFIRM";
        }

        return "UNSUITABLE";
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

        if ("NEED_CONFIRM".equals(aiSuitability)) {
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