package com.saori.npo.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GrantRequirementCheck {

    private Long id;

    private Long grantCaseId;

    private String requirementName;

    private String targetFileName;

    private String checkStatus;

    private String checkMemo;

    private Boolean archived;

    private LocalDateTime archivedAt;

    private String archiveReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}