package com.automatedTradingApplication.scheduler;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
@Builder
public class ScheduledJob {
    @Id
    private String id;
    private String symbol;
    private String qty;
    private boolean buy;
    private boolean sale;
    private LocalDateTime scheduledTime;
}