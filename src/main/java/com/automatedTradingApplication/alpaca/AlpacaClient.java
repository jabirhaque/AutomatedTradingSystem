package com.automatedTradingApplication.alpaca;

import com.automatedTradingApplication.transaction.Transaction;
import com.automatedTradingApplication.transaction.TransactionRepository;
import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.util.apitype.MarketDataWebsocketSourceType;
import net.jacobpeterson.alpaca.model.util.apitype.TraderAPIEndpointType;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockFeed;
import net.jacobpeterson.alpaca.openapi.trader.ApiException;
import net.jacobpeterson.alpaca.openapi.trader.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AlpacaClient {

    @Autowired
    private TransactionRepository transactionRepository;

    @Value("${alpaca.api.keyID}")
    private String keyID;

    @Value("${alpaca.api.secretKey}")
    private String secretKey;
    
    Logger logger = LoggerFactory.getLogger(AlpacaClient.class);

    final TraderAPIEndpointType endpointType = TraderAPIEndpointType.PAPER;
    final MarketDataWebsocketSourceType sourceType = MarketDataWebsocketSourceType.IEX;

    public double getLatestTradePrice(String symbol) throws net.jacobpeterson.alpaca.openapi.marketdata.ApiException {
        AlpacaAPI alpacaAPI = new AlpacaAPI(keyID, secretKey, endpointType, sourceType);
        return alpacaAPI.marketData().stock().stockLatestTradeSingle(symbol, StockFeed.IEX, null).getTrade().getP();
    }

    public Position getOrders(String symbol) throws ApiException {
         Position position = new AlpacaAPI(keyID, secretKey, endpointType, sourceType).trader().positions().getOpenPosition(symbol);
         return position;
    }

    public void sellBack(String price, String symbol) throws ApiException {
        Position position = getOrders(symbol);
        logger.info("Attempting scheduled sale of ${} of {}", price, symbol);
        new AlpacaAPI(keyID, secretKey, endpointType, sourceType).trader().orders()
                .postOrder(new PostOrderRequest()
                        .symbol(symbol)
                        .notional(price)
                        .side(OrderSide.SELL)
                        .type(OrderType.MARKET)
                        .timeInForce(TimeInForce.DAY));
        logger.info("Completed scheduled sale of ${} of {}", price, symbol);
    }

    public void buyBack(double qty, String symbol) throws Exception{
        Position position = getOrders(symbol);
        logger.info("Attempting scheduled buy back {} qty of {}", qty, symbol);
        new AlpacaAPI(keyID, secretKey, endpointType, sourceType).trader().orders()
                .postOrder(new PostOrderRequest()
                        .symbol(symbol)
                        .qty(String.valueOf(qty))
                        .side(OrderSide.BUY)
                        .type(OrderType.MARKET)
                        .timeInForce(TimeInForce.DAY));
        logger.info("Completed scheduled buy back {} qty of {}", qty, symbol);
    }

    public void buyVolume(String price, String symbol) throws Exception{
        logger.info("Attempting to purchase ${} of {}", price, symbol);
        Order result = new AlpacaAPI(keyID, secretKey, endpointType, sourceType).trader().orders()
                .postOrder(new PostOrderRequest()
                        .symbol(symbol)
                        .notional(price)
                        .side(OrderSide.BUY)
                        .type(OrderType.MARKET)
                        .timeInForce(TimeInForce.DAY));
        logger.info("Purchased ${} of {}", price, symbol);
        Transaction transaction = Transaction.builder().ticker(symbol).transactionType("BUY").notional(Double.parseDouble(price)).exit(false).submittedTimestamp(LocalDateTime.now()).build();
        transactionRepository.save(transaction);
    }

    public double shortVolume(double price, String symbol) throws ApiException, net.jacobpeterson.alpaca.openapi.marketdata.ApiException {
        AlpacaAPI alpacaAPI = new AlpacaAPI(keyID, secretKey, endpointType, sourceType);
        double latestTradePrice = getLatestTradePrice(symbol);

        double qty = Math.floor(price/latestTradePrice);

        if (qty>0){
            logger.info("Attempting to short qty: {} of {}", qty, symbol);

            alpacaAPI.trader().orders()
                    .postOrder(new PostOrderRequest()
                            .symbol(symbol)
                            .qty(String.valueOf(qty))
                            .side(OrderSide.SELL)
                            .type(OrderType.MARKET)
                            .timeInForce(TimeInForce.DAY));
            logger.info("Shorted qty: {} of {}", qty, symbol);
            Transaction transaction = Transaction.builder().ticker(symbol).transactionType("SHORT").qty(qty).exit(false).submittedTimestamp(LocalDateTime.now()).build();
            transactionRepository.save(transaction);
        }else{
            logger.info("${} of {} is not sufficient to short", price, symbol);
        }
        return qty;
    }
}
