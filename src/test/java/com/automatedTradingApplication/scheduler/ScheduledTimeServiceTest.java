package com.automatedTradingApplication.scheduler;

import com.automatedTradingApplication.alpaca.AlpacaClient;
import net.jacobpeterson.alpaca.openapi.trader.ApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;


import java.time.LocalDateTime;
import java.time.Month;

@SpringBootTest
public class ScheduledTimeServiceTest {

    @Mock
    AlpacaClient alpacaClient;

    @InjectMocks
    ScheduledTimeService scheduledTimeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetScheduledExitTimeNextDaySameTime() throws ApiException {
        LocalDateTime now = LocalDateTime.of(2025, Month.JULY, 14, 15, 30);
        Mockito.when(alpacaClient.nextClosing()).thenReturn(LocalDateTime.of(2025, Month.JULY, 14, 21, 0));
        Mockito.when(alpacaClient.nextOpening()).thenReturn(LocalDateTime.of(2025, Month.JULY, 15, 14, 30));
        LocalDateTime actual = scheduledTimeService.getScheduledExitTime(now);
        LocalDateTime expected = LocalDateTime.of(2025, Month.JULY, 15, 15, 30);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetScheduledExitTimeWeekend() throws ApiException {
        LocalDateTime now = LocalDateTime.of(2025, Month.JULY, 11, 15, 30);
        Mockito.when(alpacaClient.nextClosing()).thenReturn(LocalDateTime.of(2025, Month.JULY, 11, 21, 0));
        Mockito.when(alpacaClient.nextOpening()).thenReturn(LocalDateTime.of(2025, Month.JULY, 14, 14, 30));
        LocalDateTime actual = scheduledTimeService.getScheduledExitTime(now);
        LocalDateTime expected = LocalDateTime.of(2025, Month.JULY, 14, 15, 30);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testGetScheduledExitTimeHolidayPeriod() throws ApiException {
        LocalDateTime now = LocalDateTime.of(2025, Month.DECEMBER, 24, 17, 30);
        Mockito.when(alpacaClient.nextClosing()).thenReturn(LocalDateTime.of(2025, Month.DECEMBER, 24, 18, 0));
        Mockito.when(alpacaClient.nextOpening()).thenReturn(LocalDateTime.of(2025, Month.DECEMBER, 26, 14, 30));
        LocalDateTime actual = scheduledTimeService.getScheduledExitTime(now);
        LocalDateTime expected = LocalDateTime.of(2025, Month.DECEMBER, 26, 20, 30);
        Assertions.assertEquals(expected, actual);
    }
}
