package com.saori.npo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.saori.npo.domain.GrantMaster;
import com.saori.npo.mapper.GrantMasterMapper;

@Service
public class GrantMasterService {

    private final GrantMasterMapper grantMasterMapper;

    public GrantMasterService(GrantMasterMapper grantMasterMapper) {
        this.grantMasterMapper = grantMasterMapper;
    }

    public List<GrantMaster> findAll() {
        return grantMasterMapper.findAll();
    }

    public GrantMaster findById(Long id) {
        return grantMasterMapper.findById(id);
    }
}