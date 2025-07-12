package com.automatedTradingApplication.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class ScheduledTaskExecutor {

    @Autowired
    private TaskScheduler taskScheduler;

    public void scheduleTaskAtSpecificTime(Runnable task, LocalDateTime dateTime) {
        Date scheduledTime = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
        taskScheduler.schedule(task, scheduledTime);
    }
}