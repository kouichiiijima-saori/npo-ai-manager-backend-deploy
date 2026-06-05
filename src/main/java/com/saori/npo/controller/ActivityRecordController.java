package com.saori.npo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.saori.npo.domain.ActivityRecord;
import com.saori.npo.service.ActivityRecordService;

@RestController
public class ActivityRecordController {

    private final ActivityRecordService activityRecordService;

    public ActivityRecordController(
            ActivityRecordService activityRecordService) {
        this.activityRecordService = activityRecordService;
    }

    @GetMapping("/api/activity-records")
    public List<ActivityRecord> getActivityRecords() {
        return activityRecordService.findAll();
    }

    @GetMapping("/api/activity-records/{id}")
    public ActivityRecord getActivityRecord(
            @PathVariable Long id) {
        return activityRecordService.findById(id);
    }

    @PostMapping("/api/activity-records")
    public ActivityRecord createActivityRecord(
            @RequestBody ActivityRecord activityRecord) {
        return activityRecordService.create(activityRecord);
    }

    @PutMapping("/api/activity-records/{id}")
    public ActivityRecord updateActivityRecord(
            @PathVariable Long id,
            @RequestBody ActivityRecord activityRecord) {
        return activityRecordService.update(id, activityRecord);
    }

    @DeleteMapping("/api/activity-records/{id}")
    public void deleteActivityRecord(@PathVariable Long id) {
        activityRecordService.deleteById(id);
    }
}