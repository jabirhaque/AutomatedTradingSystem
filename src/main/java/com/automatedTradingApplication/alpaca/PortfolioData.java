package com.automatedTradingApplication.alpaca;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder

public class PortfolioData {
    LocalDateTime timestamp;
    double open;
    double high;
    double low;
    double close;
    double volume;
    int sign;
}
