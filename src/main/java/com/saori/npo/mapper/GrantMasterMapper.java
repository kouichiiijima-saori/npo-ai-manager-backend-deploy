package com.saori.npo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.saori.npo.domain.GrantMaster;

@Mapper
public interface GrantMasterMapper {

    List<GrantMaster> findAll();

    GrantMaster findById(Long id);

    int insert(GrantMaster grantMaster);

    int update(GrantMaster grantMaster);

}