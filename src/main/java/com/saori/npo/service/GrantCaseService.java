package com.saori.npo.service;

import java.time.LocalDateTime;
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

    public GrantCase archive(
            Long id,
            String archiveReason) {

        if (archiveReason == null || archiveReason.isBlank()) {
            throw new IllegalArgumentException("アーカイブ理由を入力してください。");
        }

        GrantCase existingGrantCase = grantCaseMapper.findById(id);

        if (existingGrantCase == null) {
            throw new IllegalArgumentException("指定された助成金案件が見つかりません。");
        }

        grantCaseMapper.archiveById(
                id,
                archiveReason,
                LocalDateTime.now());

        return grantCaseMapper.findById(id);
    }

    public GrantCase complete(
            Long id,
            String archiveReason) {

        if (archiveReason == null || archiveReason.isBlank()) {
            throw new IllegalArgumentException("アーカイブ理由を入力してください。");
        }

        GrantCase existingGrantCase = grantCaseMapper.findById(id);

        if (existingGrantCase == null) {
            throw new IllegalArgumentException("指定された助成金案件が見つかりません。");
        }

        existingGrantCase.setCaseStage("COMPLETED");
        existingGrantCase.setArchived(true);
        existingGrantCase.setArchiveReason(archiveReason);

        grantCaseMapper.update(existingGrantCase);

        grantCaseMapper.archiveById(
                id,
                archiveReason,
                LocalDateTime.now());

        return grantCaseMapper.findById(id);
    }

}