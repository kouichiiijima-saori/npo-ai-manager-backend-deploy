package com.saori.npo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.saori.npo.domain.CharterArticle;
import com.saori.npo.mapper.CharterArticleMapper;

@Service
public class CharterArticleService {

    private final CharterArticleMapper charterArticleMapper;

    public CharterArticleService(CharterArticleMapper charterArticleMapper) {
        this.charterArticleMapper = charterArticleMapper;
    }

    public List<CharterArticle> findAll() {
        return charterArticleMapper.findAll();
    }

    public CharterArticle findById(Long id) {
        return charterArticleMapper.findById(id);
    }

    public CharterArticle create(CharterArticle charterArticle) {
        if (charterArticle.getOrganizationId() == null) {
            charterArticle.setOrganizationId(1L);
        }

        charterArticleMapper.insert(charterArticle);

        return charterArticleMapper.findById(charterArticle.getId());
    }

    public CharterArticle update(Long id, CharterArticle charterArticle) {
        charterArticle.setId(id);

        charterArticleMapper.update(charterArticle);

        return charterArticleMapper.findById(id);
    }

    public void deleteById(Long id) {
        charterArticleMapper.deleteById(id);
    }
}