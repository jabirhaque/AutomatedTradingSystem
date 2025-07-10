package com.automatedTradingApplication.news;

import com.automatedTradingApplication.ScheduledTaskExecutor;
import com.automatedTradingApplication.alpaca.AlpacaClient;
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
    private ArticleSentimentRepository articleSentimentRepository;

    @Autowired
    private ScheduledTaskExecutor scheduledTaskExecutor;

    Logger logger = LoggerFactory.getLogger(ScheduledNewsCall.class);

    @Scheduled(fixedRate = 15000)
    public void makeNewsCall() throws Exception {
        logger.info("Scheduled article call...");
        logger.info("Market open: {}", alpacaClient.isMarketOpen());
        ArticleSentiment articleSentiment = sentimentService.callArticleSentiment();
        ArticleSentiment lastArticleSentiment = articleSentimentRepository.findTopByOrderByCreatedDesc();
        if (alpacaClient.isMarketOpen() && (lastArticleSentiment == null || !lastArticleSentiment.getArticle().equals(articleSentiment.getArticle()))){
            articleSentimentRepository.save(articleSentiment);
            logger.info("Ticker: {}  Publisher: {}, Score: {}, Article: {}", articleSentiment.getTicker(), articleSentiment.getPublisher(), articleSentiment.getScore(), articleSentiment.getArticle());
            double score = articleSentiment.getScore();
            String ticker = articleSentiment.getTicker();
            if (score>0){
                String qty = String.valueOf(alpacaClient.getQtyFromPrice(ticker,score*10000 , false));
                alpacaClient.partitionedBuy(qty, ticker, false);
                Runnable sellBackTask = () -> {
                    try {
                        alpacaClient.partitionedSale(qty, ticker, true);
                    } catch (Exception e) {
                        logger.debug(e.toString());
                    }
                };
                LocalDateTime scheduledTime = LocalDateTime.now().plusSeconds(60);
                scheduledTaskExecutor.scheduleTaskAtSpecificTime(sellBackTask, scheduledTime);
            }else{
                String qty = String.valueOf(alpacaClient.getQtyFromPrice(ticker, score*-10000, false));
                String resultQty = alpacaClient.partitionedSale(qty, ticker, false);
                Runnable buyBackTask = () -> {
                    try {
                        alpacaClient.partitionedBuy(resultQty, ticker, true);
                    } catch (Exception e) {
                        logger.debug(e.toString());
                    }
                };
                LocalDateTime scheduledTime = LocalDateTime.now().plusSeconds(60);
                scheduledTaskExecutor.scheduleTaskAtSpecificTime(buyBackTask, scheduledTime);
            }
        }
    }
}
