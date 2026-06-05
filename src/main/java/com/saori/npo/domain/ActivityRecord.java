package com.saori.npo.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ActivityRecord {

    private Long id;

    private Long organizationId;

    private Integer fiscalYear;

    private String projectName;

    private String content;

    private String result;

    private String reportFileName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}