package com.saori.npo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.saori.npo.domain.CharterArticle;

@Mapper
public interface CharterArticleMapper {

    List<CharterArticle> findAll();

    List<CharterArticle> findByOrganizationId(Long organizationId);

    CharterArticle findById(Long id);

    int insert(CharterArticle charterArticle);

    int update(CharterArticle charterArticle);

    int deleteById(Long id);
}