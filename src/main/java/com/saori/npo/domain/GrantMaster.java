package com.saori.npo.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GrantMaster {

    private Long id;

    private Integer fiscalYear;

    private String title;

    private String provider;

    private LocalDate applicationStartDate;

    private LocalDate applicationDeadline;

    private Long maxGrantAmount;

    private String summary;

    private String targetTheme;

    private String targetProject;

    private String targetOrganization;

    private String targetArea;

    private String requiredDocuments;

    private String officialUrl;

    private String officialPdfName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}