package com.saori.npo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.saori.npo.domain.GrantCase;
import com.saori.npo.mapper.GrantCaseMapper;

@Service
public class GrantCaseService {

    private final GrantCaseMapper grantCaseMapper;

    public GrantCaseService(
            GrantCaseMapper grantCaseMapper) {
        this.grantCaseMapper = grantCaseMapper;
    }

    public List<GrantCase> findAll() {
        return grantCaseMapper.findAll();
    }

    public GrantCase findById(Long id) {
        return grantCaseMapper.findById(id);
    }

    public GrantCase update(
            Long id,
            GrantCase grantCase) {

        grantCase.setId(id);

        grantCaseMapper.update(grantCase);

        return grantCaseMapper.findById(id);
    }
}