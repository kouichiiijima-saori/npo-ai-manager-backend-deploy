package com.saori.npo.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.saori.npo.domain.OrganizationProfile;
import com.saori.npo.service.OrganizationProfileService;

@RestController
public class OrganizationProfileController {

    private final OrganizationProfileService organizationProfileService;

    public OrganizationProfileController(
            OrganizationProfileService organizationProfileService) {
        this.organizationProfileService = organizationProfileService;
    }

    @GetMapping("/api/organization-profile")
    public OrganizationProfile getOrganizationProfile() {
        return organizationProfileService.findDefaultProfile();
    }

    @PutMapping("/api/organization-profile/{id}")
    public OrganizationProfile updateOrganizationProfile(
            @PathVariable Long id,
            @RequestBody OrganizationProfile organizationProfile) {
        return organizationProfileService.update(id, organizationProfile);
    }

}