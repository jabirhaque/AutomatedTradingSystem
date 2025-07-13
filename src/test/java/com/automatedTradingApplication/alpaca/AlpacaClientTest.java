package com.automatedTradingApplication.alpaca;

import com.automatedTradingApplication.transaction.TransactionRepository;
import net.jacobpeterson.alpaca.openapi.marketdata.ApiException;
import net.jacobpeterson.alpaca.openapi.trader.model.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AlpacaClientTest {
    /**
     * TODO: When testing for sell/buy/clear orders, setup a mock database to assert successful persistence, e.g. H2
     */

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

    @Test
    void getQtyFromPositionTestWhenPresent() throws net.jacobpeterson.alpaca.openapi.trader.ApiException {
        Position position = new Position();
        position.setSymbol("AAPL");
        position.setQty("100");

        Position position2 = new Position();
        position2.setSymbol("AMZN");
        position2.setQty("150");

        Position position3 = new Position();
        position3.setSymbol("MSFT");
        position3.setQty("50");

        List<Position> positions = List.of(position, position2, position3);
        Mockito.when(alpacaApiWrapper.getPositions()).thenReturn(positions);

        String expected = "150";
        String actual = alpacaClient.getQtyFromPosition("AMZN");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getQtyFromPositionTestWhenNotPresent() throws net.jacobpeterson.alpaca.openapi.trader.ApiException {
        Position position = new Position();
        position.setSymbol("AAPL");
        position.setQty("100");

        Position position2 = new Position();
        position2.setSymbol("AMZN");
        position2.setQty("150");

        Position position3 = new Position();
        position3.setSymbol("MSFT");
        position3.setQty("50");

        List<Position> positions = List.of(position, position2, position3);
        Mockito.when(alpacaApiWrapper.getPositions()).thenReturn(positions);

        String expected = "0";
        String actual = alpacaClient.getQtyFromPosition("NVDA");
        Assertions.assertEquals(expected, actual);
    }
}
