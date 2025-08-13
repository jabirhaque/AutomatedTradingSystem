package com.automatedTradingApplication.scheduler;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledJobRepository extends MongoRepository<ScheduledJob, String> {
    List<ScheduledJob> findByScheduledTimeLessThanEqual(LocalDateTime currentTime);
}
