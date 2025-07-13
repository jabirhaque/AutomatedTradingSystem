package com.automatedTradingApplication.alpaca;

import com.automatedTradingApplication.transaction.TransactionRepository;
import net.jacobpeterson.alpaca.openapi.marketdata.ApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AlpacaClientTest {

    @Mock
    TransactionRepository transactionRepository;

    @Mock
    AlpacaApiWrapper alpacaApiWrapper;

    @InjectMocks
    AlpacaClient alpacaClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getQtyFromPriceTest() throws ApiException {
        Mockito.when(alpacaApiWrapper.getLatestTradePrice("AAPL")).thenReturn(211.38);
        double actual = alpacaClient.getQtyFromPrice("AAPL", 500);
        double expected = 500.00/211.38;
        Assertions.assertEquals(expected, actual);
    }
}
