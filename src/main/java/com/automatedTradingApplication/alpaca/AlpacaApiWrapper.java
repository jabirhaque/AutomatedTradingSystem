package com.automatedTradingApplication.alpaca;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.util.apitype.MarketDataWebsocketSourceType;
import net.jacobpeterson.alpaca.model.util.apitype.TraderAPIEndpointType;
import net.jacobpeterson.alpaca.openapi.marketdata.model.StockFeed;
import net.jacobpeterson.alpaca.openapi.trader.ApiException;
import net.jacobpeterson.alpaca.openapi.trader.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class AlpacaApiWrapper {
    /**
     * Wrapper service for the AlpacaAPI dependency, enabling dependency injection and thus better testing
     * As this class simply calls the AlpacaAPI class, testing is not necessary for this component
     * This class should NOT contain any logic further than AlpacaAPI calls
     */

    private final AlpacaAPI alpacaAPI;

    AlpacaApiWrapper(@Value("${alpaca.api.keyID}") String keyId, @Value("${alpaca.api.secretKey}") String secretKey){
        this.alpacaAPI = new AlpacaAPI(keyId, secretKey, TraderAPIEndpointType.PAPER, MarketDataWebsocketSourceType.IEX);
    }

    public PortfolioHistory portfolioHistory() throws ApiException {
        PortfolioHistory portfolioHistory = alpacaAPI.trader().portfolioHistory().getAccountPortfolioHistory("1D", "1Min", "continuous", OffsetDateTime.now().minusDays(2), null, null, null, "false");
        return portfolioHistory; //TODO: Create an API and processing service that calls the portfolio history, processes it in the suitable format [Timestamp, open, close], and exposes this resource via an endpoint
    }

    public LocalDateTime nextOpening() throws ApiException {
        return alpacaAPI.trader().clock().getClock().getNextOpen().toLocalDateTime().plusHours(5);
    }

    public LocalDateTime nextClosing() throws ApiException {
        return alpacaAPI.trader().clock().getClock().getNextClose().toLocalDateTime().plusHours(5);
    }

    public boolean isMarketOpen() throws ApiException {
        return Boolean.TRUE.equals(alpacaAPI.trader().clock().getClock().getIsOpen());
    }

    public double getLatestTradePrice(String symbol) throws net.jacobpeterson.alpaca.openapi.marketdata.ApiException {
        return alpacaAPI.marketData().stock().stockLatestTradeSingle(symbol, StockFeed.IEX, null).getTrade().getP();
    }

    public List<Position> getPositions() throws ApiException {
        return alpacaAPI.trader().positions().getAllOpenPositions();
    }

    public String getPositionFromSymbol(String symbol) throws ApiException {
        return alpacaAPI.trader().positions().getOpenPosition(symbol).getQty();
    }

    public Order clearPosition(String symbol) throws ApiException {
        return alpacaAPI.trader().positions().deleteOpenPosition(symbol,  null, new BigDecimal("100"));
    }

    public Order buy(String symbol, String qty) throws ApiException {
        return alpacaAPI.trader().orders()
                .postOrder(new PostOrderRequest()
                        .symbol(symbol)
                        .qty(qty)
                        .side(OrderSide.BUY)
                        .type(OrderType.MARKET)
                        .timeInForce(TimeInForce.DAY)
                );
    }

    public Order sell(String symbol, String qty) throws ApiException {
        return alpacaAPI.trader().orders()
                .postOrder(new PostOrderRequest()
                        .symbol(symbol)
                        .qty(String.valueOf(qty))
                        .side(OrderSide.SELL)
                        .type(OrderType.MARKET)
                        .timeInForce(TimeInForce.DAY)
                );
    }
}
