package com.saori.npo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.saori.npo.domain.GrantRequirementCheck;

@Mapper
public interface GrantRequirementCheckMapper {

    List<GrantRequirementCheck> findAll();

    List<GrantRequirementCheck> findByGrantCaseId(Long grantCaseId);

    GrantRequirementCheck findById(Long id);

    int insert(GrantRequirementCheck grantRequirementCheck);

    int update(GrantRequirementCheck grantRequirementCheck);
}