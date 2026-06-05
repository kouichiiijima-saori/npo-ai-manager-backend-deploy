package com.saori.npo.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class GrantCase {

    private Long id;

    private Long organizationId;

    private Long grantMasterId;

    private String caseName;

    private String caseStage;

    private String examinationStatus;

    private String externalAuditStatus;

    private String examinationMemo;

    private String nextAction;

    private LocalDate nextActionDueDate;

    private Boolean archived;

    private LocalDateTime archivedAt;

    private String archiveReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}