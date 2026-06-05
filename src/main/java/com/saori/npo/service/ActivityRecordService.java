package com.saori.npo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.saori.npo.domain.ActivityRecord;
import com.saori.npo.mapper.ActivityRecordMapper;

@Service
public class ActivityRecordService {

    private final ActivityRecordMapper activityRecordMapper;

    public ActivityRecordService(
            ActivityRecordMapper activityRecordMapper) {
        this.activityRecordMapper = activityRecordMapper;
    }

    public List<ActivityRecord> findAll() {
        return activityRecordMapper.findAll();
    }

    public ActivityRecord findById(Long id) {
        return activityRecordMapper.findById(id);
    }

    public ActivityRecord create(ActivityRecord activityRecord) {

        if (activityRecord.getOrganizationId() == null) {
            activityRecord.setOrganizationId(1L);
        }

        activityRecordMapper.insert(activityRecord);

        return activityRecordMapper.findById(
                activityRecord.getId());
    }

    public ActivityRecord update(
            Long id,
            ActivityRecord activityRecord) {

        activityRecord.setId(id);

        activityRecordMapper.update(activityRecord);

        return activityRecordMapper.findById(id);
    }

    public void deleteById(Long id) {
        activityRecordMapper.deleteById(id);
    }
}