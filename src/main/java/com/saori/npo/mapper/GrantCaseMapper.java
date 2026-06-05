package com.saori.npo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.saori.npo.domain.GrantCase;

@Mapper
public interface GrantCaseMapper {

    List<GrantCase> findAll();

    GrantCase findById(Long id);

    int insert(GrantCase grantCase);

    int update(GrantCase grantCase);

}