package com.automatedTradingApplication.news;

import com.automatedTradingApplication.ScheduledTaskExecutor;
import com.automatedTradingApplication.alpaca.AlpacaClient;
import net.jacobpeterson.alpaca.openapi.trader.model.Position;
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

    @Scheduled(fixedRate = 12000)
    public void makeNewsCall() throws Exception {
        ArticleSentiment articleSentiment = sentimentService.callArticleSentiment();
        ArticleSentiment lastArticleSentiment = articleSentimentRepository.findTopByOrderByCreatedDesc();
        if (lastArticleSentiment == null || !lastArticleSentiment.getArticle().equals(articleSentiment.getArticle())){
            articleSentimentRepository.save(articleSentiment);
            logger.info("Ticker: {}  Publisher: {}, Score: {}, Article: {}", articleSentiment.getTicker(), articleSentiment.getPublisher(), articleSentiment.getScore(), articleSentiment.getArticle());
            double score = articleSentiment.getScore();
            String ticker = articleSentiment.getTicker();
            if (score>0){
                Position position = alpacaClient.getOrders("ADBE");
                String volume = String.valueOf(Math.round((score*10000) * 100.0) / 100.0);
                alpacaClient.buyVolume(volume, ticker);
                Runnable sellBackTask = () -> {
                    try {
                        alpacaClient.sellBack(volume, ticker);
                    } catch (Exception e) {
                        logger.debug(e.toString());
                    }
                };
                LocalDateTime scheduledTime = LocalDateTime.now().plusSeconds(10);
                scheduledTaskExecutor.scheduleTaskAtSpecificTime(sellBackTask, scheduledTime);
            }else{
                Position position = alpacaClient.getOrders("ADBE");
                double volume = Math.round((score*-10000) * 100.0) / 100.0;
                double qty = alpacaClient.shortVolume(volume, ticker);
                Runnable buyBackTask = () -> {
                    try {
                        alpacaClient.buyBack(qty, ticker);
                    } catch (Exception e) {
                        logger.debug(e.toString());
                    }
                };
                LocalDateTime scheduledTime = LocalDateTime.now().plusSeconds(10);
                scheduledTaskExecutor.scheduleTaskAtSpecificTime(buyBackTask, scheduledTime);
            }
        }
    }
}
