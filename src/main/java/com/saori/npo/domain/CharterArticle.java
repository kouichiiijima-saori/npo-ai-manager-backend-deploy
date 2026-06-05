package com.saori.npo.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CharterArticle {

    private Long id;

    private Long organizationId;

    private Integer articleNumber;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}