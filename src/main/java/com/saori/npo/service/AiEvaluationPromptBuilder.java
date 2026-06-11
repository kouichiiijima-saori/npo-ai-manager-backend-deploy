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

		appendInstruction(prompt);
		appendOrganizationProfile(prompt, organizationProfile);
		appendCharterArticles(prompt, charterArticles);
		appendActivityRecords(prompt, activityRecords);
		appendGrantMaster(prompt, grantMaster);

		return prompt.toString();
	}

	private void appendInstruction(StringBuilder prompt) {

		prompt.append("""
				あなたはNPO法人の助成金適合性を判定するAIです。
				最終判断は人間が行います。あなたは、団体情報・定款・活動実績・助成金情報を根拠に、応募検討のための参考判定を行ってください。

				必ず有効なJSONのみを返してください。
				説明文、Markdown、コードブロックは不要です。
				JSONのキーは必ずダブルクォートで囲ってください。

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

				suitability は必ず以下のいずれか:
				- SUITABLE
				- NEEDS_CONFIRMATION
				- NOT_SUITABLE

				recommendationLevel は必ず以下のいずれか:
				- A
				- B
				- C

				【判定の優先順位】
				次の順番で確認してください。

				1. 対象団体
				助成金の対象団体に、NPO法人・非営利法人・地域団体などが含まれるか。

				2. 対象地域
				団体の所在地・活動地域が、助成金の対象地域と一致または許容範囲内か。

				3. 対象事業
				助成金の対象事業と、団体の主な活動・活動実績が一致しているか。

				4. 定款目的
				定款に記載された目的・事業内容と、助成金テーマが整合しているか。

				5. 活動実績
				過去の活動実績が、助成対象事業を実施できる根拠になるか。

				6. 必要書類
				必要書類が多い場合、追加確認事項として明記する。

				【suitability 判定基準】

				SUITABLE:
				対象団体・対象地域・対象事業・定款目的・活動実績のうち、複数が明確に一致している。
				特に対象事業と活動実績が一致している場合は高く評価する。
				ただし、テーマが似ているだけでは SUITABLE にしない。

				NEEDS_CONFIRMATION:
				一部は一致しているが、対象経費、応募資格、実施体制、対象地域、必要書類などに確認不足がある。
				判断材料が不足している場合も NEEDS_CONFIRMATION とする。

				NOT_SUITABLE:
				対象団体、対象地域、対象事業、活動実績のいずれかに大きな不一致がある。
				団体活動と助成金テーマの関連性が弱い場合も NOT_SUITABLE とする。

				【recommendationLevel 判定基準】

				A:
				SUITABLE かつ根拠が複数あり、応募準備に進む価値が高い。

				B:
				NEEDS_CONFIRMATION または、SUITABLE だが確認事項が残る。

				C:
				NOT_SUITABLE または、応募優先度が低い。

				【出力ルール】
				reason には、なぜその判定になったかを2〜4文で書く。
				evidence には、団体情報・定款・活動実績・助成金情報のどれを根拠にしたかを書く。
				additionalChecks には、人間が次に確認すべき事項を3〜5個入れる。
				根拠が不足している場合は、不足している情報を additionalChecks に入れる。

				""");
	}

	private void appendOrganizationProfile(
			StringBuilder prompt,
			OrganizationProfile organizationProfile) {

		prompt.append("\n【団体情報】\n");

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