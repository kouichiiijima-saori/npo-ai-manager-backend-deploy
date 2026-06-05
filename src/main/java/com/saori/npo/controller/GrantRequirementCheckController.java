package com.saori.npo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.saori.npo.domain.GrantRequirementCheck;
import com.saori.npo.service.GrantRequirementCheckService;

@RestController
public class GrantRequirementCheckController {

    private final GrantRequirementCheckService grantRequirementCheckService;

    public GrantRequirementCheckController(
            GrantRequirementCheckService grantRequirementCheckService) {
        this.grantRequirementCheckService = grantRequirementCheckService;
    }

    @GetMapping("/api/grant-requirement-checks")
    public List<GrantRequirementCheck> getGrantRequirementChecks() {
        return grantRequirementCheckService.findAll();
    }

    @GetMapping("/api/grant-cases/{grantCaseId}/requirement-checks")
    public List<GrantRequirementCheck> getGrantRequirementChecksByGrantCaseId(
            @PathVariable Long grantCaseId) {
        return grantRequirementCheckService.findByGrantCaseId(grantCaseId);
    }

    @GetMapping("/api/grant-requirement-checks/{id}")
    public GrantRequirementCheck getGrantRequirementCheck(
            @PathVariable Long id) {
        return grantRequirementCheckService.findById(id);
    }

    @PutMapping("/api/grant-requirement-checks/{id}")
    public GrantRequirementCheck updateGrantRequirementCheck(
            @PathVariable Long id,
            @RequestBody GrantRequirementCheck grantRequirementCheck) {
        return grantRequirementCheckService.update(id, grantRequirementCheck);
    }
}