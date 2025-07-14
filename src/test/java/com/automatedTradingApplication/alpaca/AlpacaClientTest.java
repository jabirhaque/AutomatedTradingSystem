package com.automatedTradingApplication.alpaca;

import com.automatedTradingApplication.transaction.TransactionRepository;
import net.jacobpeterson.alpaca.openapi.marketdata.ApiException;
import net.jacobpeterson.alpaca.openapi.trader.model.Order;
import net.jacobpeterson.alpaca.openapi.trader.model.OrderSide;
import net.jacobpeterson.alpaca.openapi.trader.model.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AlpacaClientTest{

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

    @Test
    void buyTest() throws net.jacobpeterson.alpaca.openapi.trader.ApiException {
        String qty = "10";
        String symbol = "AAPL";
        boolean exit = false;

        Order mockOrder = new Order();
        mockOrder.setSide(OrderSide.BUY);
        mockOrder.setFilledQty(qty);

        Mockito.when(alpacaApiWrapper.buy(symbol, qty)).thenReturn(mockOrder);

        String filledQty = alpacaClient.buy(qty, symbol, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).buy(symbol, qty);
        Assertions.assertEquals(qty, filledQty);
    }

    @Test
    void sellTest() throws net.jacobpeterson.alpaca.openapi.trader.ApiException {
        String qty = "10";
        String symbol = "AAPL";
        boolean exit = false;

        Order mockOrder = new Order();
        mockOrder.setSide(OrderSide.SELL);
        mockOrder.setFilledQty(qty);

        Mockito.when(alpacaApiWrapper.sell(symbol, qty)).thenReturn(mockOrder);

        String filledQty = alpacaClient.sell(qty, symbol, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).sell(symbol, qty);
        Assertions.assertEquals(qty, filledQty);
    }

    @Test
    void shortSellTest() throws net.jacobpeterson.alpaca.openapi.trader.ApiException {
        String qty = "10.5";
        String symbol = "AAPL";
        boolean exit = false;

        Order mockOrder = new Order();
        mockOrder.setSide(OrderSide.SELL);
        mockOrder.setFilledQty("10.0");

        Mockito.when(alpacaApiWrapper.sell(symbol, "10.0")).thenReturn(mockOrder);

        String filledQty = alpacaClient.shortSell(qty, symbol, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).sell(symbol, "10.0");
        Assertions.assertEquals("10.0", filledQty);
    }
}
