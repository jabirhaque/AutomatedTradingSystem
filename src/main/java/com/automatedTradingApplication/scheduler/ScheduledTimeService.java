package com.automatedTradingApplication.scheduler;

import com.automatedTradingApplication.alpaca.AlpacaApiWrapper;
import net.jacobpeterson.alpaca.openapi.trader.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class ScheduledTimeService {

    @Autowired
    private AlpacaApiWrapper alpacaApiWrapper;

    Logger logger = LoggerFactory.getLogger(ScheduledTimeService.class);

    public LocalDateTime getScheduledExitTime(LocalDateTime now) throws ApiException {
        LocalDateTime closeTime = alpacaApiWrapper.nextClosing();
        LocalDateTime openingTime = alpacaApiWrapper.nextOpening();
        Duration duration = Duration.between(closeTime, now);
        LocalDateTime result = openingTime.plusMinutes(390).minusMinutes(Math.abs(duration.toMinutes()));
        logger.info("Scheduling event for: {}", result);
        return result;
    }
}
