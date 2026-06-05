package com.saori.npo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.saori.npo.domain.CharterArticle;
import com.saori.npo.service.CharterArticleService;

@RestController
public class CharterArticleController {

    private final CharterArticleService charterArticleService;

    public CharterArticleController(CharterArticleService charterArticleService) {
        this.charterArticleService = charterArticleService;
    }

    @GetMapping("/api/charter-articles")
    public List<CharterArticle> getCharterArticles() {
        return charterArticleService.findAll();
    }

    @GetMapping("/api/charter-articles/{id}")
    public CharterArticle getCharterArticle(@PathVariable Long id) {
        return charterArticleService.findById(id);
    }

    @PostMapping("/api/charter-articles")
    public CharterArticle createCharterArticle(
            @RequestBody CharterArticle charterArticle) {
        return charterArticleService.create(charterArticle);
    }

    @PutMapping("/api/charter-articles/{id}")
    public CharterArticle updateCharterArticle(
            @PathVariable Long id,
            @RequestBody CharterArticle charterArticle) {
        return charterArticleService.update(id, charterArticle);
    }

    @DeleteMapping("/api/charter-articles/{id}")
    public void deleteCharterArticle(@PathVariable Long id) {
        charterArticleService.deleteById(id);
    }
}