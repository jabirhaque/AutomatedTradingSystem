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
    ScheduledTimeService scheduledTimeService;

    Logger logger = LoggerFactory.getLogger(ScheduledNewsCall.class);

    @Scheduled(fixedRate = 15000)
    public void makeNewsCall() throws Exception {
        logger.info("Scheduled article call...");
        boolean marketOpen = alpacaApiWrapper.isMarketOpen();
        logger.info("Market open: {}", marketOpen);
        ArticleSentiment articleSentiment = sentimentService.callArticleSentiment();
        ArticleSentiment lastArticleSentiment = articleSentimentRepository.findTopByOrderByCreatedDesc();
        if (marketOpen && (lastArticleSentiment == null || !lastArticleSentiment.getArticle().equals(articleSentiment.getArticle()))){
            articleSentimentRepository.save(articleSentiment);
            logger.info("Ticker: {}  Publisher: {}, Score: {}", articleSentiment.getTicker(), articleSentiment.getPublisher(), articleSentiment.getScore());
            double score = articleSentiment.getScore();
            String ticker = articleSentiment.getTicker();
            if (score>0){
                String qty = String.valueOf(alpacaClient.getQtyFromPrice(ticker,score*10000));
                alpacaClient.partitionedBuy(qty, ticker, false);
                Runnable sellBackTask = () -> {
                    try {
                        alpacaClient.partitionedSale(qty, ticker, true);
                    } catch (Exception e) {
                        logger.debug("Error in the sale request of {} of {}", qty, ticker);
                    }
                };
                LocalDateTime scheduledTime = scheduledTimeService.getScheduledExitTime(LocalDateTime.now());
                scheduledTaskExecutor.scheduleTaskAtSpecificTime(sellBackTask, scheduledTime);
            }else{
                String qty = String.valueOf(alpacaClient.getQtyFromPrice(ticker, score*-10000));
                String resultQty = alpacaClient.partitionedSale(qty, ticker, false);
                Runnable buyBackTask = () -> {
                    try {
                        alpacaClient.partitionedBuy(resultQty, ticker, true);
                    } catch (Exception e) {
                        logger.debug("Error in the purchase request of {} of {}", qty, ticker);
                    }
                };
                LocalDateTime scheduledTime = scheduledTimeService.getScheduledExitTime(LocalDateTime.now());
                scheduledTaskExecutor.scheduleTaskAtSpecificTime(buyBackTask, scheduledTime);
            }
        }
    }
}
