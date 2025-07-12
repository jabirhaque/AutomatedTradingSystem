package com.automatedTradingApplication.alpaca;

import com.automatedTradingApplication.news.ScheduledNewsCall;
import com.automatedTradingApplication.transaction.TransactionRepository;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.openapi.marketdata.ApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AlpacaClientTest {

    @Mock
    AlpacaAPI alpacaAPI;

    @Mock
    TransactionRepository transactionRepository;

    @Spy
    @InjectMocks
    AlpacaClient alpacaClient;

    Logger logger = LoggerFactory.getLogger(AlpacaClientTest.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getQtyFromPriceTest() throws ApiException {
        Mockito.doReturn(211.38).when(alpacaClient).getLatestTradePrice("AAPL");
        double actual = alpacaClient.getQtyFromPrice("AAPL", 500);
        double expected = 500.00/211.38;
        logger.debug("Expecting {}, actually {}", expected, actual);
        Assertions.assertEquals(expected, actual);
    }
}
