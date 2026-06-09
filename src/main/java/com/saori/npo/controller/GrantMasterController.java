package com.saori.npo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.saori.npo.domain.GrantMaster;
import com.saori.npo.service.GrantMasterService;

@RestController
public class GrantMasterController {

    private final GrantMasterService grantMasterService;

    public GrantMasterController(
            GrantMasterService grantMasterService) {
        this.grantMasterService = grantMasterService;
    }

    @GetMapping("/api/grant-masters")
    public List<GrantMaster> getGrantMasters() {
        return grantMasterService.findAll();
    }

    @GetMapping("/api/grant-masters/{id}")
    public GrantMaster getGrantMaster(
            @PathVariable Long id) {
        return grantMasterService.findById(id);
    }

    @PostMapping("/api/grant-masters")
    public GrantMaster createGrantMaster(
            @RequestBody GrantMaster grantMaster) {

        return grantMasterService.create(grantMaster);
    }

    @PutMapping("/api/grant-masters/{id}")
    public GrantMaster updateGrantMaster(
            @PathVariable Long id,
            @RequestBody GrantMaster grantMaster) {

        return grantMasterService.update(
                id,
                grantMaster);
    }
}