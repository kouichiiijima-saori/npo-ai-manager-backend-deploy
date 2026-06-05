package com.saori.npo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.saori.npo.domain.GrantRequirementCheck;
import com.saori.npo.mapper.GrantRequirementCheckMapper;

@Service
public class GrantRequirementCheckService {

    private final GrantRequirementCheckMapper grantRequirementCheckMapper;

    public GrantRequirementCheckService(
            GrantRequirementCheckMapper grantRequirementCheckMapper) {
        this.grantRequirementCheckMapper = grantRequirementCheckMapper;
    }

    public List<GrantRequirementCheck> findAll() {
        return grantRequirementCheckMapper.findAll();
    }

    public List<GrantRequirementCheck> findByGrantCaseId(Long grantCaseId) {
        return grantRequirementCheckMapper.findByGrantCaseId(grantCaseId);
    }

    public GrantRequirementCheck findById(Long id) {
        return grantRequirementCheckMapper.findById(id);
    }

    public GrantRequirementCheck update(
            Long id,
            GrantRequirementCheck grantRequirementCheck) {

        grantRequirementCheck.setId(id);

        grantRequirementCheckMapper.update(grantRequirementCheck);

        return grantRequirementCheckMapper.findById(id);
    }
}