package com.saori.npo.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.saori.npo.domain.GrantCase;

@Mapper
public interface GrantCaseMapper {

    List<GrantCase> findAll();

    GrantCase findById(Long id);

    GrantCase findByOrganizationIdAndGrantMasterId(
            Long organizationId,
            Long grantMasterId);

    int insert(GrantCase grantCase);

    int update(GrantCase grantCase);

    int archiveById(
            @Param("id") Long id,
            @Param("archiveReason") String archiveReason,
            @Param("archivedAt") LocalDateTime archivedAt);

}