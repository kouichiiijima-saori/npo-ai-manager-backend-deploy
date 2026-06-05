package com.saori.npo.service;

import org.springframework.stereotype.Service;

import com.saori.npo.domain.OrganizationProfile;
import com.saori.npo.mapper.OrganizationProfileMapper;

@Service
public class OrganizationProfileService {

    private final OrganizationProfileMapper organizationProfileMapper;

    public OrganizationProfileService(
            OrganizationProfileMapper organizationProfileMapper) {
        this.organizationProfileMapper = organizationProfileMapper;
    }

    public OrganizationProfile findDefaultProfile() {
        return organizationProfileMapper.findById(1L);
    }

    public OrganizationProfile update(Long id, OrganizationProfile organizationProfile) {
        organizationProfile.setId(id);

        organizationProfileMapper.update(organizationProfile);

        return organizationProfileMapper.findById(id);
    }
}