package com.automatedTradingApplication.alpaca;

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

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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

    public double getQtyFromPrice(String symbol, double price, boolean sale) throws net.jacobpeterson.alpaca.openapi.marketdata.ApiException {
        double raw = price/getLatestTradePrice(symbol);
        return sale?Math.floor(raw): raw;
    }

    public double getLatestTradePrice(String symbol) throws net.jacobpeterson.alpaca.openapi.marketdata.ApiException {
        AlpacaAPI alpacaAPI = new AlpacaAPI(keyID, secretKey, endpointType, sourceType);
        return alpacaAPI.marketData().stock().stockLatestTradeSingle(symbol, StockFeed.IEX, null).getTrade().getP();
    }

    public String getQtyFromOrder(String uuid) throws ApiException {
        return new AlpacaAPI(keyID, secretKey, endpointType, sourceType).trader().orders().getOrderByOrderID(UUID.fromString(uuid), false).getFilledQty();
    }

    public String getQtyFromPosition(String symbol) throws ApiException {
        AlpacaAPI alpacaAPI = new AlpacaAPI(keyID, secretKey, endpointType, sourceType);
        List<String> list = alpacaAPI.trader().positions().getAllOpenPositions().stream().map(Position::getSymbol).toList();
        if (list.contains(symbol)){
            return alpacaAPI.trader().positions().getOpenPosition(symbol).getQty();
        }else{
            return "0";
        }
    }

    public String partitionedSale(String qty, String symbol) throws ApiException {
        logger.info("Attempting sale of {} of {}", qty, symbol);
        double position = Double.parseDouble(getQtyFromPosition(symbol));
        if (position>0 && position<Double.parseDouble(qty)){
            logger.info("Partitioning sale request");
            String remainder = String.valueOf(Double.parseDouble(qty) - position);
            clearPosition(symbol);
            try{
                Thread.sleep(2000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            return String.valueOf(Double.parseDouble(sell(remainder, symbol, true))+position);
        }if (position>Double.parseDouble(qty)){
            return sell(qty, symbol);
        }
        return sell(qty, symbol, true);
    }

    public String partitionedBuy(String qty, String symbol) throws Exception {
        logger.info("Attempting buy of {} of {}", qty, symbol);
        double position = Double.parseDouble(getQtyFromPosition(symbol));
        if (position<0 && -1*position<Double.parseDouble(qty)){
            logger.info("Partitioning buy request");
            String remainder = String.valueOf(Double.parseDouble(qty)+position);
            clearPosition(symbol);
            try{
                Thread.sleep(2000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            buy(remainder, symbol);
            return qty;
        }
        return buy(qty, symbol);
    }

    public void clearPosition(String symbol) throws ApiException {
        AlpacaAPI alpacaAPI = new AlpacaAPI(keyID, secretKey, endpointType, sourceType);
        String qty = alpacaAPI.trader().positions().getOpenPosition(symbol).getQty();
        logger.info("Clearing {} of {}", qty, symbol);
        alpacaAPI.trader().positions().deleteOpenPosition(symbol,  null, new BigDecimal("100"));
    }

    private String buy(String qty, String symbol) throws ApiException {
        AlpacaAPI alpacaAPI = new AlpacaAPI(keyID, secretKey, endpointType, sourceType);
        logger.info("Buying {} of {}", qty, symbol);
        String orderId = alpacaAPI.trader().orders()
                .postOrder(new PostOrderRequest()
                        .symbol(symbol)
                        .qty(qty)
                        .side(OrderSide.BUY)
                        .type(OrderType.MARKET)
                        .timeInForce(TimeInForce.DAY)).getId();
        try{
            Thread.sleep(2000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        return alpacaAPI.trader().orders().getOrderByOrderID(UUID.fromString(orderId), false).getFilledQty();
    }

    private String sell(String qty, String symbol) throws ApiException {
        AlpacaAPI alpacaAPI = new AlpacaAPI(keyID, secretKey, endpointType, sourceType);
        logger.info("Selling {} of {}", qty, symbol);
        String orderId = alpacaAPI.trader().orders()
                .postOrder(new PostOrderRequest()
                        .symbol(symbol)
                        .qty(String.valueOf(qty))
                        .side(OrderSide.SELL)
                        .type(OrderType.MARKET)
                        .timeInForce(TimeInForce.DAY)).getId();
        try{
            Thread.sleep(2000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        return alpacaAPI.trader().orders().getOrderByOrderID(UUID.fromString(orderId), false).getFilledQty();
    }

    private String sell(String qty, String symbol, boolean shortSell) throws ApiException {
        if (shortSell){
            return sell(String.valueOf(Math.floor(Double.parseDouble(qty))), symbol);
        }
        return sell(qty, symbol);
    }
}
