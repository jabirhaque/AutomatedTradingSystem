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
        List<Position> list = alpacaApiWrapper.getPositions();
        List<String> symbols = list.stream().map(Position::getSymbol).toList();
        if (!symbols.contains(symbol)){
            return "0";
        }
        for (Position position : list) {
            if (position.getSymbol().equals(symbol)) {
                return position.getQty();
            }
        }
        return "0";
    }

    public String partitionedSale(String symbol, String qty, boolean exit) throws ApiException {
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
            return String.valueOf(Double.parseDouble(shortSell(symbol, remainder, exit))+position);
        }if (position>Double.parseDouble(qty)){
            return sell(symbol, qty, exit);
        }
        return shortSell(symbol, qty, exit);
    }

    public String partitionedBuy(String symbol, String qty, boolean exit) throws Exception {
        logger.info("Attempting {} buy of {} of {}", symbol, qty, (exit?"scheduled exit":""));
        double position = Double.parseDouble(getQtyFromPosition(symbol));
        boolean partition = position < 0 && -1 * position < Double.parseDouble(qty);
        if (!partition){
            return buy(symbol, qty, exit);
        }
        logger.info("Partitioning buy request");
        String remainder = String.valueOf(Double.parseDouble(qty)+position);
        String clear = clearPosition(symbol, exit);
        try{
            Thread.sleep(2000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        String buy = buy(symbol, remainder, exit);
        return String.valueOf(Double.parseDouble(clear)+Double.parseDouble(buy));
    }

    public String clearPosition(String symbol, boolean exit) throws ApiException {
        String qty = alpacaApiWrapper.getPositionFromSymbol(symbol);
        logger.info("Clearing {} of {}", qty, symbol);
        Order order = alpacaApiWrapper.clearPosition(symbol);
        Transaction transaction = Transaction.builder().symbol(symbol).transactionType(order.getSide().toString()).qty(Double.parseDouble(order.getFilledQty())).exit(exit).submittedTimestamp(LocalDateTime.now()).build();
        transactionRepository.save(transaction);
        return order.getFilledQty();
    }

    public String buy(String symbol, String qty, boolean exit) throws ApiException {
        logger.info("Buying {} of {}", qty, symbol);
        Order order = alpacaApiWrapper.buy(symbol, qty);
        try{
            Thread.sleep(2000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        Transaction transaction = Transaction.builder().symbol(symbol).transactionType(order.getSide().toString()).qty(Double.parseDouble(order.getFilledQty())).exit(exit).submittedTimestamp(LocalDateTime.now()).build();
        transactionRepository.save(transaction);
        return order.getFilledQty();
    }

    public String sell(String symbol, String qty, boolean exit) throws ApiException {
        logger.info("Selling {} of {}", symbol, qty);
        Order order = alpacaApiWrapper.sell(symbol, qty);
        try{
            Thread.sleep(2000);
        } catch(InterruptedException e){
            e.printStackTrace();
        }
        Transaction transaction = Transaction.builder().symbol(qty).transactionType(order.getSide().toString()).qty(Double.parseDouble(order.getFilledQty())).exit(exit).submittedTimestamp(LocalDateTime.now()).build();
        transactionRepository.save(transaction);
        return order.getFilledQty();
    }

    public String shortSell(String symbol, String qty, boolean exit) throws ApiException {
        return sell(symbol, String.valueOf(Math.floor(Double.parseDouble(qty))), exit);
    }
}
