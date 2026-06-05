package com.saori.npo.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.saori.npo.domain.OrganizationProfile;

@Mapper
public interface OrganizationProfileMapper {

    OrganizationProfile findById(Long id);

    int update(OrganizationProfile organizationProfile);

}