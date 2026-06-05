package com.saori.npo.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.saori.npo.domain.ActivityRecord;

@Mapper
public interface ActivityRecordMapper {

    List<ActivityRecord> findAll();

    ActivityRecord findById(Long id);

    int insert(ActivityRecord activityRecord);

    int update(ActivityRecord activityRecord);

    int deleteById(Long id);

}