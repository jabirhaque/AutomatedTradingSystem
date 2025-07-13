package com.automatedTradingApplication.transaction;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document
public class Transaction {
    @Id
    private String id;
    private String symbol;
    private String transactionType;
    private double qty;
    private double notional;
    private boolean exit;
    private LocalDateTime submittedTimestamp;
}
