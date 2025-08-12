package com.automatedTradingApplication.scheduler;

import com.automatedTradingApplication.alpaca.AlpacaApiWrapper;
import com.automatedTradingApplication.alpaca.AlpacaClient;
import com.automatedTradingApplication.news.ArticleSentiment;
import com.automatedTradingApplication.news.ArticleSentimentRepository;
import com.automatedTradingApplication.NLP.SentimentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ScheduledNewsCall {

    @Autowired
    private SentimentService sentimentService;

    @Autowired
    private AlpacaClient alpacaClient;

    @Autowired
    private AlpacaApiWrapper alpacaApiWrapper;

    @Autowired
    private ArticleSentimentRepository articleSentimentRepository;

    @Autowired
    private ScheduledTaskExecutor scheduledTaskExecutor;

    @Autowired
    private ScheduledTimeService scheduledTimeService;

    Logger logger = LoggerFactory.getLogger(ScheduledNewsCall.class);

    @Scheduled(fixedRate = 15000)
    public void makeNewsCall() throws Exception {
        Runtime runtime = Runtime.getRuntime();

        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long currentHeap = runtime.totalMemory();
        long maxHeap = runtime.maxMemory();

        logger.info("Used: {} MB, Current Heap: {} MB, Max Heap: {} MB",
                usedMemory / (1024 * 1024),
                currentHeap / (1024 * 1024),
                maxHeap / (1024 * 1024));

        logger.info("Scheduled article call...");
        boolean marketOpen = alpacaApiWrapper.isMarketOpen();
        logger.info("Market open: {}", marketOpen);
        if (marketOpen){
            ArticleSentiment articleSentiment = sentimentService.callArticleSentiment();
            ArticleSentiment lastArticleSentiment = articleSentimentRepository.findTopByOrderByCreatedDesc();
            if (lastArticleSentiment == null || !lastArticleSentiment.getArticle().equals(articleSentiment.getArticle())){
                articleSentimentRepository.save(articleSentiment);
                logger.info("Symbol: {}  Publisher: {}, Score: {}", articleSentiment.getSymbol(), articleSentiment.getPublisher(), articleSentiment.getScore());
                double score = articleSentiment.getScore();
                String symbol = articleSentiment.getSymbol();
                if (score>0){
                    String qty = String.valueOf(alpacaClient.getQtyFromPrice(symbol,score*10000));
                    alpacaClient.partitionedBuy(symbol, qty, false);
                    Runnable sellBackTask = () -> {
                        try {
                            alpacaClient.partitionedSale(symbol, qty, true);
                        } catch (Exception e) {
                            logger.debug("Error in the sale request of {} of {}", qty, symbol);
                        }
                    };
                    LocalDateTime scheduledTime = scheduledTimeService.getScheduledExitTime(LocalDateTime.now());
                    scheduledTaskExecutor.scheduleTaskAtSpecificTime(sellBackTask, scheduledTime);
                }else{
                    String qty = String.valueOf(alpacaClient.getQtyFromPrice(symbol, score*-10000));
                    String resultQty = alpacaClient.partitionedSale(symbol, qty, false);
                    Runnable buyBackTask = () -> {
                        try {
                            alpacaClient.partitionedBuy(symbol, resultQty, true);
                        } catch (Exception e) {
                            logger.debug("Error in the purchase request of {} of {}", qty, symbol);
                        }
                    };
                    LocalDateTime scheduledTime = scheduledTimeService.getScheduledExitTime(LocalDateTime.now());
                    scheduledTaskExecutor.scheduleTaskAtSpecificTime(buyBackTask, scheduledTime);
                }
            }
        }
    }
}
