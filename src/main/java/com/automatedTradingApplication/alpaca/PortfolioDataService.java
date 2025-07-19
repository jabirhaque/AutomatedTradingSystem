package com.automatedTradingApplication.alpaca;

import net.jacobpeterson.alpaca.openapi.trader.ApiException;
import net.jacobpeterson.alpaca.openapi.trader.model.PortfolioHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class PortfolioDataService {

    @Autowired
    private AlpacaApiWrapper alpacaApiWrapper;

    public List<PortfolioData> portfolioDataWeek() throws ApiException {
        PortfolioHistory portfolioHistory = alpacaApiWrapper.portfolioHistoryWeek();
        List<PortfolioData> list = new ArrayList<>();
        for (int i=0; i<portfolioHistory.getTimestamp().size(); i++){
            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(portfolioHistory.getTimestamp().get(i)), ZoneId.systemDefault());
            double close = portfolioHistory.getEquity().get(i).doubleValue();
            double open = portfolioHistory.getEquity().get(i==0?i:i-1).doubleValue();
            int sign = Double.compare(close, open);
            PortfolioData portfolioData = new PortfolioData(timestamp, open, close, open, close, close, sign);
            list.add(portfolioData);
        }
        return list;
    }

    public List<PortfolioData> portfolioDataMonth() throws ApiException {
        PortfolioHistory portfolioHistory = alpacaApiWrapper.portfolioHistoryMonth();
        List<PortfolioData> list = new ArrayList<>();
        for (int i=0; i<portfolioHistory.getTimestamp().size(); i++){
            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(portfolioHistory.getTimestamp().get(i)), ZoneId.systemDefault());
            double close = portfolioHistory.getEquity().get(i).doubleValue();
            double open = portfolioHistory.getEquity().get(i==0?i:i-1).doubleValue();
            int sign = Double.compare(close, open);
            PortfolioData portfolioData = new PortfolioData(timestamp, open, close, open, close, close, sign);
            list.add(portfolioData);
        }
        return list;
    }

    public List<PortfolioData> portfolioDataYear() throws ApiException {
        PortfolioHistory portfolioHistory = alpacaApiWrapper.portfolioHistoryYear();
        List<PortfolioData> list = new ArrayList<>();
        for (int i=0; i<portfolioHistory.getTimestamp().size(); i++){
            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(portfolioHistory.getTimestamp().get(i)), ZoneId.systemDefault());
            double close = portfolioHistory.getEquity().get(i).doubleValue();
            double open = portfolioHistory.getEquity().get(i==0?i:i-1).doubleValue();
            int sign = Double.compare(close, open);
            PortfolioData portfolioData = new PortfolioData(timestamp, open, close, open, close, close, sign);
            list.add(portfolioData);
        }
        return list;
    }

}
