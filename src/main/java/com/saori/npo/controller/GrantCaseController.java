package com.saori.npo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.saori.npo.domain.GrantCase;
import com.saori.npo.service.GrantCaseService;

@RestController
public class GrantCaseController {

    private final GrantCaseService grantCaseService;

    public GrantCaseController(
            GrantCaseService grantCaseService) {
        this.grantCaseService = grantCaseService;
    }

    @GetMapping("/api/grant-cases")
    public List<GrantCase> getGrantCases() {
        return grantCaseService.findAll();
    }

    @GetMapping("/api/grant-cases/{id}")
    public GrantCase getGrantCase(
            @PathVariable Long id) {
        return grantCaseService.findById(id);
    }

    @PutMapping("/api/grant-cases/{id}")
    public GrantCase updateGrantCase(
            @PathVariable Long id,
            @RequestBody GrantCase grantCase) {

        return grantCaseService.update(id, grantCase);
    }
}