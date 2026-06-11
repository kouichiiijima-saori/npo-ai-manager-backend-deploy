package com.saori.npo.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.saori.npo.domain.ActivityRecord;
import com.saori.npo.domain.CharterArticle;
import com.saori.npo.domain.GrantMaster;
import com.saori.npo.domain.OrganizationProfile;

@Component
public class AiEvaluationPromptBuilder {

	public String build(
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

		appendOrganizationProfile(
				prompt,
				organizationProfile);

		appendCharterArticles(
				prompt,
				charterArticles);

		appendActivityRecords(
				prompt,
				activityRecords);

		appendGrantMaster(
				prompt,
				grantMaster);

		return prompt.toString();
	}

	private void appendOrganizationProfile(
			StringBuilder prompt,
			OrganizationProfile organizationProfile) {

		if (organizationProfile == null) {
			prompt.append("団体情報: 未登録\n");
			return;
		}

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
	}

	private void appendCharterArticles(
			StringBuilder prompt,
			List<CharterArticle> charterArticles) {

		prompt.append("\n【定款】\n");

		if (charterArticles == null || charterArticles.isEmpty()) {
			prompt.append("定款情報: 未登録\n");
			return;
		}

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

	private void appendActivityRecords(
			StringBuilder prompt,
			List<ActivityRecord> activityRecords) {

		prompt.append("\n【活動実績】\n");

		if (activityRecords == null || activityRecords.isEmpty()) {
			prompt.append("活動実績: 未登録\n");
			return;
		}

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

	private void appendGrantMaster(
			StringBuilder prompt,
			GrantMaster grantMaster) {

		prompt.append("\n【助成金情報】\n");

		if (grantMaster == null) {
			prompt.append("助成金情報: 未登録\n");
			return;
		}

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
	}

	private String safeText(String value) {

		if (value == null || value.isBlank()) {
			return "未登録";
		}

		return value;
	}
}