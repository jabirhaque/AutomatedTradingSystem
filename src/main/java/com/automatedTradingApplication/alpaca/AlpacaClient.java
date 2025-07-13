package com.automatedTradingApplication.alpaca;

import com.automatedTradingApplication.transaction.Transaction;
import com.automatedTradingApplication.transaction.TransactionRepository;
import net.jacobpeterson.alpaca.openapi.trader.ApiException;
import net.jacobpeterson.alpaca.openapi.trader.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AlpacaClient {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    AlpacaApiWrapper alpacaApiWrapper;

    Logger logger = LoggerFactory.getLogger(AlpacaClient.class);

    public double getQtyFromPrice(String symbol, double price) throws net.jacobpeterson.alpaca.openapi.marketdata.ApiException {
        return price/alpacaApiWrapper.getLatestTradePrice(symbol);
    }

    public String getQtyFromPosition(String symbol) throws ApiException {
        List<String> list = alpacaApiWrapper.getPositions().stream().map(Position::getSymbol).toList();
        if (list.contains(symbol)){
            return getQtyFromPosition(symbol);
        }else{
            return "0";
        }
    }

    public String partitionedSale(String qty, String symbol, boolean exit) throws ApiException {
        logger.info("Attempting {} sale of {} of {}", qty, symbol, (exit?"scheduled exit":""));
        double position = Double.parseDouble(getQtyFromPosition(symbol));
        if (position>0 && position<Double.parseDouble(qty)){
            logger.info("Partitioning sale request");
            String remainder = String.valueOf(Double.parseDouble(qty) - position);
            clearPosition(symbol, exit);
            try{
                Thread.sleep(2000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            return String.valueOf(Double.parseDouble(sell(remainder, symbol, exit, true))+position);
        }if (position>Double.parseDouble(qty)){
            return sell(qty, symbol, exit);
        }
        return sell(qty, symbol, exit, true);
    }

    public String partitionedBuy(String qty, String symbol, boolean exit) throws Exception {
        logger.info("Attempting {} buy of {} of {}", qty, symbol, (exit?"scheduled exit":""));
        double position = Double.parseDouble(getQtyFromPosition(symbol));
        if (position<0 && -1*position<Double.parseDouble(qty)){
            logger.info("Partitioning buy request");
            String remainder = String.valueOf(Double.parseDouble(qty)+position);
            clearPosition(symbol, exit);
            try{
                Thread.sleep(2000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            buy(remainder, symbol, exit);
            return qty;
        }
        return buy(qty, symbol, exit);
    }

    public void clearPosition(String symbol, boolean exit) throws ApiException {
        String qty = alpacaApiWrapper.getPositionFromSymbol(symbol);
        logger.info("Clearing {} of {}", qty, symbol);
        Order order = alpacaApiWrapper.clearPosition(symbol);
        Transaction transaction = Transaction.builder().ticker(symbol).transactionType(order.getSide().toString()).qty(Double.parseDouble(order.getFilledQty())).exit(exit).submittedTimestamp(LocalDateTime.now()).build();
        transactionRepository.save(transaction);
    }

    private String buy(String qty, String symbol, boolean exit) throws ApiException {
        logger.info("Buying {} of {}", qty, symbol);
        Order order = alpacaApiWrapper.buy(symbol, qty);
        try{
            Thread.sleep(2000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        Transaction transaction = Transaction.builder().ticker(symbol).transactionType(order.getSide().toString()).qty(Double.parseDouble(order.getFilledQty())).exit(exit).submittedTimestamp(LocalDateTime.now()).build();
        transactionRepository.save(transaction);
        return order.getFilledQty();
    }

    private String sell(String qty, String symbol, boolean exit) throws ApiException {
        logger.info("Selling {} of {}", qty, symbol);
        Order order = alpacaApiWrapper.sell(symbol, qty);
        try{
            Thread.sleep(2000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        Transaction transaction = Transaction.builder().ticker(symbol).transactionType(order.getSide().toString()).qty(Double.parseDouble(order.getFilledQty())).exit(exit).submittedTimestamp(LocalDateTime.now()).build();
        transactionRepository.save(transaction);
        return order.getFilledQty();
    }

    private String sell(String qty, String symbol, boolean exit, boolean shortSell) throws ApiException {
        if (shortSell){
            return sell(String.valueOf(Math.floor(Double.parseDouble(qty))), symbol, exit);
        }
        return sell(qty, symbol, exit);
    }
}
