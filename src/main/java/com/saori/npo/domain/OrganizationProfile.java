package com.saori.npo.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class OrganizationProfile {

	private Long id;

	private String organizationName;

	private String representativeName;

	private String location;

	private LocalDate establishedDate;

	private String activityArea;

	private String mission;

	private String targetPeople;

	private String mainActivities;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}