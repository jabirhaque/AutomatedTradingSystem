package com.automatedTradingApplication.alpaca;

import com.automatedTradingApplication.transaction.TransactionRepository;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.openapi.marketdata.ApiException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class AlpacaClientTest {

    @Mock
    AlpacaAPI alpacaAPI;

    @Mock
    TransactionRepository transactionRepository;

    @Spy
    @InjectMocks
    AlpacaClient alpacaClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getQtyFromPriceTest() throws ApiException {
        Mockito.doReturn(211.38).when(alpacaClient).getLatestTradePrice("AAPL");
        double actual = alpacaClient.getQtyFromPrice("AAPL", 500);
        double expected = 500.00/211.38;
        Assertions.assertEquals(expected, actual);
    }
}
