package com.automatedTradingApplication.alpaca;

import com.automatedTradingApplication.transaction.TransactionRepository;
import net.jacobpeterson.alpaca.openapi.marketdata.ApiException;
import net.jacobpeterson.alpaca.openapi.trader.model.Order;
import net.jacobpeterson.alpaca.openapi.trader.model.OrderSide;
import net.jacobpeterson.alpaca.openapi.trader.model.OrderStatus;
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
        mockOrder.setStatus(OrderStatus.FILLED);
        mockOrder.setSide(OrderSide.BUY);
        mockOrder.setFilledQty(qty);

        Mockito.when(alpacaApiWrapper.buy(symbol, qty)).thenReturn(mockOrder);

        String filledQty = alpacaClient.buy(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).buy(symbol, qty);
        Assertions.assertEquals(qty, filledQty);
    }

    @Test
    void sellTest() throws net.jacobpeterson.alpaca.openapi.trader.ApiException {
        String qty = "10";
        String symbol = "AAPL";
        boolean exit = false;

        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.FILLED);
        mockOrder.setSide(OrderSide.SELL);
        mockOrder.setFilledQty(qty);

        Mockito.when(alpacaApiWrapper.sell(symbol, qty)).thenReturn(mockOrder);

        String filledQty = alpacaClient.sell(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).sell(symbol, qty);
        Assertions.assertEquals(qty, filledQty);
    }

    @Test
    void shortSellTest() throws net.jacobpeterson.alpaca.openapi.trader.ApiException {
        String qty = "10.5";
        String symbol = "AAPL";
        boolean exit = false;

        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.FILLED);
        mockOrder.setSide(OrderSide.SELL);
        mockOrder.setFilledQty("10.0");

        Mockito.when(alpacaApiWrapper.sell(symbol, "10.0")).thenReturn(mockOrder);

        String filledQty = alpacaClient.shortSell(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).sell(symbol, "10.0");
        Assertions.assertEquals("10.0", filledQty);
    }

    @Test
    void clearPositionTest() throws net.jacobpeterson.alpaca.openapi.trader.ApiException {
        String symbol = "AAPL";
        boolean exit = false;

        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.FILLED);
        mockOrder.setSide(OrderSide.SELL);
        mockOrder.setFilledQty("10");

        Mockito.when(alpacaApiWrapper.clearPosition(symbol)).thenReturn(mockOrder);

        String filledQty = alpacaClient.clearPosition(symbol, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).clearPosition(symbol);
        Assertions.assertEquals("10", filledQty);
    }

    @Test
    void partitionedBuyTestNoPosition() throws Exception {
        String symbol = "AAPL";
        String qty = "10";
        boolean exit = false;

        Position position = new Position();
        position.setSymbol("AMZN");
        position.setQty("150");

        Position position2 = new Position();
        position2.setSymbol("MSFT");
        position2.setQty("50");

        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.FILLED);
        mockOrder.setSide(OrderSide.BUY);
        mockOrder.setFilledQty("10");

        List<Position> positions = List.of(position, position2);
        Mockito.when(alpacaApiWrapper.getPositions()).thenReturn(positions);
        Mockito.when(alpacaApiWrapper.buy("AAPL", "10")).thenReturn(mockOrder);

        String actual = alpacaClient.partitionedBuy(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).buy(symbol, qty);
        Assertions.assertEquals("10", actual);
    }

    @Test
    void partitionedBuyTestPositivePosition() throws Exception {
        String symbol = "AAPL";
        String qty = "10";
        boolean exit = false;

        Position position = new Position();
        position.setSymbol("AAPL");
        position.setQty("150");

        Position position2 = new Position();
        position2.setSymbol("MSFT");
        position2.setQty("50");

        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.FILLED);
        mockOrder.setSide(OrderSide.BUY);
        mockOrder.setFilledQty("10");

        List<Position> positions = List.of(position, position2);
        Mockito.when(alpacaApiWrapper.getPositions()).thenReturn(positions);
        Mockito.when(alpacaApiWrapper.buy("AAPL", "10")).thenReturn(mockOrder);

        String actual = alpacaClient.partitionedBuy(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).buy(symbol, qty);
        Assertions.assertEquals("10", actual);
    }

    @Test
    void partitionedBuyTestNegativePositionNotPartitioning() throws Exception {
        String symbol = "AAPL";
        String qty = "10";
        boolean exit = false;

        Position position = new Position();
        position.setSymbol("AAPL");
        position.setQty("-150");

        Position position2 = new Position();
        position2.setSymbol("MSFT");
        position2.setQty("50");

        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.FILLED);
        mockOrder.setSide(OrderSide.BUY);
        mockOrder.setFilledQty("10");

        List<Position> positions = List.of(position, position2);
        Mockito.when(alpacaApiWrapper.getPositions()).thenReturn(positions);
        Mockito.when(alpacaApiWrapper.buy("AAPL", "10")).thenReturn(mockOrder);

        String actual = alpacaClient.partitionedBuy(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).buy(symbol, qty);
        Assertions.assertEquals("10", actual);
    }

    @Test
    void partitionedBuyTestNegativePositionPartitioning() throws Exception {
        String symbol = "AAPL";
        String qty = "10";
        boolean exit = false;

        Position position = new Position();
        position.setSymbol("AAPL");
        position.setQty("-7");

        Position position2 = new Position();
        position2.setSymbol("MSFT");
        position2.setQty("50");

        Order mockClearOrder = new Order();
        mockClearOrder.setStatus(OrderStatus.FILLED);
        mockClearOrder.setSide(OrderSide.BUY);
        mockClearOrder.setFilledQty("7");

        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.FILLED);
        mockOrder.setSide(OrderSide.BUY);
        mockOrder.setFilledQty("3");

        List<Position> positions = List.of(position, position2);
        Mockito.when(alpacaApiWrapper.getPositions()).thenReturn(positions);
        Mockito.when(alpacaApiWrapper.clearPosition("AAPL")).thenReturn(mockClearOrder);
        Mockito.when(alpacaApiWrapper.buy("AAPL", "3.0")).thenReturn(mockOrder);

        String actual = alpacaClient.partitionedBuy(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).clearPosition(symbol);
        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).buy(symbol, "3.0");
        Assertions.assertEquals("10.0", actual);
    }

    @Test
    void partitionedBuyEqualPosition() throws Exception {
        String symbol = "AAPL";
        String qty = "10.5";
        boolean exit = false;

        Position position = new Position();
        position.setSymbol("AMZN");
        position.setQty("150");

        Position position2 = new Position();
        position2.setSymbol("AAPL");
        position2.setQty("-10.5");

        Order mockClearOrder = new Order();
        mockClearOrder.setStatus(OrderStatus.FILLED);
        mockClearOrder.setSide(OrderSide.BUY);
        mockClearOrder.setFilledQty("10.5");

        List<Position> positions = List.of(position, position2);
        Mockito.when(alpacaApiWrapper.getPositions()).thenReturn(positions);
        Mockito.when(alpacaApiWrapper.clearPosition("AAPL")).thenReturn(mockClearOrder);

        String actual = alpacaClient.partitionedBuy(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).clearPosition(symbol);
        Mockito.verify(alpacaApiWrapper, Mockito.times(0)).sell(symbol, "0.0");
        Assertions.assertEquals("10.5", actual);
    }

    @Test
    public void partitionedSaleTestNoPosition() throws Exception {
        String symbol = "AAPL";
        String qty = "10";
        boolean exit = false;

        Position position = new Position();
        position.setSymbol("AMZN");
        position.setQty("150");

        Position position2 = new Position();
        position2.setSymbol("MSFT");
        position2.setQty("50");

        Order mockOrder = new Order();
        mockOrder.setSide(OrderSide.SELL);
        mockOrder.setFilledQty("10");
        mockOrder.setStatus(OrderStatus.FILLED);

        List<Position> positions = List.of(position, position2);
        Mockito.when(alpacaApiWrapper.getPositions()).thenReturn(positions);
        Mockito.when(alpacaApiWrapper.sell("AAPL", "10.0")).thenReturn(mockOrder);

        String actual = alpacaClient.partitionedSale(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).sell(symbol, "10.0");
        Assertions.assertEquals("10", actual);
    }

    @Test
    public void partitionedSaleTestNoPositionFractional() throws Exception {
        String symbol = "AAPL";
        String qty = "10.5";
        boolean exit = false;

        Position position = new Position();
        position.setSymbol("AMZN");
        position.setQty("150");

        Position position2 = new Position();
        position2.setSymbol("MSFT");
        position2.setQty("50");

        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.FILLED);
        mockOrder.setSide(OrderSide.SELL);
        mockOrder.setFilledQty("10");

        List<Position> positions = List.of(position, position2);
        Mockito.when(alpacaApiWrapper.getPositions()).thenReturn(positions);
        Mockito.when(alpacaApiWrapper.sell("AAPL", "10.0")).thenReturn(mockOrder);

        String actual = alpacaClient.partitionedSale(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).sell(symbol, "10.0");
        Assertions.assertEquals("10", actual);
    }

    @Test
    public void partitionedSaleTestNegativePositionFractional() throws Exception {
        String symbol = "AAPL";
        String qty = "10.5";
        boolean exit = false;

        Position position = new Position();
        position.setSymbol("AAPL");
        position.setQty("-100");

        Position position2 = new Position();
        position2.setSymbol("MSFT");
        position2.setQty("50");

        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.FILLED);
        mockOrder.setSide(OrderSide.SELL);
        mockOrder.setFilledQty("10");

        List<Position> positions = List.of(position, position2);
        Mockito.when(alpacaApiWrapper.getPositions()).thenReturn(positions);
        Mockito.when(alpacaApiWrapper.sell("AAPL", "10.0")).thenReturn(mockOrder);

        String actual = alpacaClient.partitionedSale(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).sell(symbol, "10.0");
        Assertions.assertEquals("10", actual);
    }

    @Test
    public void partitionedSaleTestPositivePositionNoPartitionFractional() throws Exception {
        String symbol = "AAPL";
        String qty = "10.5";
        boolean exit = false;

        Position position = new Position();
        position.setSymbol("AAPL");
        position.setQty("100");

        Position position2 = new Position();
        position2.setSymbol("MSFT");
        position2.setQty("50");

        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.FILLED);
        mockOrder.setSide(OrderSide.SELL);
        mockOrder.setFilledQty("10.5");

        List<Position> positions = List.of(position, position2);
        Mockito.when(alpacaApiWrapper.getPositions()).thenReturn(positions);
        Mockito.when(alpacaApiWrapper.sell("AAPL", "10.5")).thenReturn(mockOrder);

        String actual = alpacaClient.partitionedSale(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).sell(symbol, "10.5");
        Assertions.assertEquals("10.5", actual);
    }

    @Test
    public void partitionedSaleTestPositivePositionPartitionFractional() throws Exception {
        String symbol = "AAPL";
        String qty = "10.5";
        boolean exit = false;

        Position position = new Position();
        position.setSymbol("AAPL");
        position.setQty("3");

        Position position2 = new Position();
        position2.setSymbol("MSFT");
        position2.setQty("50");

        Order mockClearOrder = new Order();
        mockClearOrder.setStatus(OrderStatus.FILLED);
        mockClearOrder.setSide(OrderSide.SELL);
        mockClearOrder.setFilledQty("3");

        Order mockOrder = new Order();
        mockOrder.setStatus(OrderStatus.FILLED);
        mockOrder.setSide(OrderSide.SELL);
        mockOrder.setFilledQty("7.0");

        List<Position> positions = List.of(position, position2);
        Mockito.when(alpacaApiWrapper.getPositions()).thenReturn(positions);
        Mockito.when(alpacaApiWrapper.clearPosition("AAPL")).thenReturn(mockClearOrder);
        Mockito.when(alpacaApiWrapper.sell("AAPL", "7.0")).thenReturn(mockOrder);

        String actual = alpacaClient.partitionedSale(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).clearPosition(symbol);
        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).sell(symbol, "7.0");
        Assertions.assertEquals("10.0", actual);
    }

    @Test
    public void partitionedSaleTestEqualPositionNoPartition() throws Exception {
        String symbol = "AAPL";
        String qty = "10.5";
        boolean exit = false;

        Position position = new Position();
        position.setSymbol("AAPL");
        position.setQty("10.5");

        Position position2 = new Position();
        position2.setSymbol("MSFT");
        position2.setQty("50");

        Order mockClearOrder = new Order();
        mockClearOrder.setStatus(OrderStatus.FILLED);
        mockClearOrder.setSide(OrderSide.SELL);
        mockClearOrder.setFilledQty("10.5");

        List<Position> positions = List.of(position, position2);
        Mockito.when(alpacaApiWrapper.getPositions()).thenReturn(positions);
        Mockito.when(alpacaApiWrapper.clearPosition("AAPL")).thenReturn(mockClearOrder);

        String actual = alpacaClient.partitionedSale(symbol, qty, exit);

        Mockito.verify(alpacaApiWrapper, Mockito.times(1)).clearPosition(symbol);
        Mockito.verify(alpacaApiWrapper, Mockito.times(0)).sell(symbol, "0.0");
        Assertions.assertEquals("10.5", actual);
    }
}