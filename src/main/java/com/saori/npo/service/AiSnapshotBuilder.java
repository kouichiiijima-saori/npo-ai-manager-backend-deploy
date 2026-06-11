package com.saori.npo.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.saori.npo.domain.ActivityRecord;
import com.saori.npo.domain.CharterArticle;
import com.saori.npo.domain.GrantMaster;
import com.saori.npo.domain.OrganizationProfile;

@Component
public class AiSnapshotBuilder {

	public String buildOrganizationSnapshot(
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

	public String buildCharterSnapshot(
			List<CharterArticle> charterArticles) {

		int count = 0;

		if (charterArticles != null) {
			count = charterArticles.size();
		}

		return "{\"source\":\"charter_articles\",\"count\":"
				+ count
				+ "}";
	}

	public String buildActivitySnapshot(
			List<ActivityRecord> activityRecords) {

		int count = 0;

		if (activityRecords != null) {
			count = activityRecords.size();
		}

		return "{\"source\":\"activity_records\",\"count\":"
				+ count
				+ "}";
	}

	public String buildGrantSnapshot(
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

	private String safeJsonText(String value) {

		if (value == null || value.isBlank()) {
			return "";
		}

		return value
				.replace("\\", "\\\\")
				.replace("\"", "\\\"");
	}
}