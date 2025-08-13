package com.automatedTradingApplication.scheduler;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduledJobRepository extends MongoRepository<ScheduledJob, String> {
}
