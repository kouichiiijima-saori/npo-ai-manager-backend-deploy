package com.saori.npo.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.saori.npo.client.AiEvaluationClient;
import com.saori.npo.domain.ActivityRecord;
import com.saori.npo.domain.CharterArticle;
import com.saori.npo.domain.GrantCase;
import com.saori.npo.domain.GrantMaster;
import com.saori.npo.domain.OrganizationProfile;
import com.saori.npo.dto.AiEvaluationRequest;
import com.saori.npo.dto.AiEvaluationResult;
import com.saori.npo.mapper.ActivityRecordMapper;
import com.saori.npo.mapper.CharterArticleMapper;
import com.saori.npo.mapper.EvaluationHistoryMapper;
import com.saori.npo.mapper.GrantCaseMapper;
import com.saori.npo.mapper.GrantMasterMapper;
import com.saori.npo.mapper.GrantRequirementCheckMapper;
import com.saori.npo.mapper.OrganizationProfileMapper;

@ExtendWith(MockitoExtension.class)
class AiEvaluationServiceTest {

    @Mock
    private GrantMasterMapper grantMasterMapper;

    @Mock
    private GrantCaseMapper grantCaseMapper;

    @Mock
    private EvaluationHistoryMapper evaluationHistoryMapper;

    @Mock
    private GrantRequirementCheckMapper grantRequirementCheckMapper;

    @Mock
    private AiEvaluationClient aiEvaluationClient;

    @Mock
    private OrganizationProfileMapper organizationProfileMapper;

    @Mock
    private CharterArticleMapper charterArticleMapper;

    @Mock
    private ActivityRecordMapper activityRecordMapper;

    @Mock
    private AiEvaluationPromptBuilder aiEvaluationPromptBuilder;

    @Mock
    private AiResultNormalizer aiResultNormalizer;

    @Mock
    private AiSnapshotBuilder aiSnapshotBuilder;

    @Mock
    private DummyAiEvaluationService dummyAiEvaluationService;

    @InjectMocks
    private AiEvaluationService aiEvaluationService;

    @Test
    void returnsNotFoundWhenGrantMasterDoesNotExist() {
        AiEvaluationRequest request = new AiEvaluationRequest();
        request.setOrganizationId(1L);
        request.setGrantMasterId(99L);

        when(grantMasterMapper.findById(request.getGrantMasterId()))
                .thenReturn(null);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> aiEvaluationService.evaluate(request));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verifyNoInteractions(aiEvaluationClient);
        verifyNoInteractions(organizationProfileMapper);
        verifyNoInteractions(grantCaseMapper);
        verifyNoInteractions(evaluationHistoryMapper);
    }

    @Test
    void rejectsEvaluationWhenApplicationDeadlineHasPassed() {
        GrantMaster grantMaster = new GrantMaster();
        grantMaster.setId(10L);
        grantMaster.setApplicationDeadline(LocalDate.now().minusDays(1));

        AiEvaluationRequest request = new AiEvaluationRequest();
        request.setOrganizationId(1L);
        request.setGrantMasterId(grantMaster.getId());

        when(grantMasterMapper.findById(grantMaster.getId()))
                .thenReturn(grantMaster);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> aiEvaluationService.evaluate(request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(aiEvaluationClient);
        verifyNoInteractions(organizationProfileMapper);
        verifyNoInteractions(grantCaseMapper);
        verifyNoInteractions(evaluationHistoryMapper);
    }

    @Test
    void allowsEvaluationWhenApplicationDeadlineIsToday() {
        GrantMaster grantMaster = new GrantMaster();
        grantMaster.setId(10L);
        grantMaster.setTitle("当日締切の助成金");
        grantMaster.setApplicationDeadline(LocalDate.now());

        AiEvaluationRequest request = new AiEvaluationRequest();
        request.setOrganizationId(1L);
        request.setGrantMasterId(grantMaster.getId());

        OrganizationProfile organizationProfile = new OrganizationProfile();
        List<CharterArticle> charterArticles = List.of();
        List<ActivityRecord> activityRecords = List.of();

        AiEvaluationResult aiResult = new AiEvaluationResult();
        aiResult.setSuitability("SUITABLE");
        aiResult.setRecommendationLevel("A");
        aiResult.setReason("応募対象です。");
        aiResult.setEvidence("締切当日です。");
        aiResult.setAdditionalChecks(List.of());

        GrantCase grantCase = new GrantCase();
        grantCase.setId(20L);
        grantCase.setExaminationStatus("UNCONFIRMED");
        grantCase.setExternalAuditStatus("NO_RESPONSE");

        when(grantMasterMapper.findById(grantMaster.getId()))
                .thenReturn(grantMaster);
        when(organizationProfileMapper.findById(request.getOrganizationId()))
                .thenReturn(organizationProfile);
        when(charterArticleMapper.findByOrganizationId(request.getOrganizationId()))
                .thenReturn(charterArticles);
        when(activityRecordMapper.findByOrganizationId(request.getOrganizationId()))
                .thenReturn(activityRecords);
        when(aiEvaluationPromptBuilder.build(
                organizationProfile,
                charterArticles,
                activityRecords,
                grantMaster))
                .thenReturn("prompt");
        when(aiEvaluationClient.evaluate("prompt"))
                .thenReturn(aiResult);
        when(aiResultNormalizer.normalizeSuitability(aiResult.getSuitability()))
                .thenReturn("SUITABLE");
        when(aiResultNormalizer.normalizeRecommendationLevel(
                aiResult.getRecommendationLevel()))
                .thenReturn("A");
        when(grantCaseMapper.findByOrganizationIdAndGrantMasterId(
                request.getOrganizationId(),
                request.getGrantMasterId()))
                .thenReturn(grantCase);

        assertDoesNotThrow(() -> aiEvaluationService.evaluate(request));
    }
}
