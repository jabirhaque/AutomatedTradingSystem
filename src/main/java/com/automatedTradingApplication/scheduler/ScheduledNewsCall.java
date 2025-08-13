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
    private ScheduledTimeService scheduledTimeService;

    @Autowired
    private ScheduledJobRepository scheduledJobRepository;

    @Autowired
    private ScheduledTaskExecutor scheduledTaskExecutor;

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
        scheduledTaskExecutor.executeJobs();
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
                    LocalDateTime scheduledTime = scheduledTimeService.getScheduledExitTime(LocalDateTime.now());
                    ScheduledJob scheduledJob = ScheduledJob.builder().symbol(symbol).qty(qty).buy(false).sale(true).scheduledTime(scheduledTime).build();
                    scheduledJobRepository.save(scheduledJob);
                }else{
                    String qty = String.valueOf(alpacaClient.getQtyFromPrice(symbol, score*-10000));
                    String resultQty = alpacaClient.partitionedSale(symbol, qty, false);
                    LocalDateTime scheduledTime = scheduledTimeService.getScheduledExitTime(LocalDateTime.now());
                    ScheduledJob scheduledJob = ScheduledJob.builder().symbol(symbol).qty(resultQty).buy(true).sale(false).scheduledTime(scheduledTime).build();
                    scheduledJobRepository.save(scheduledJob);
                }
            }
        }
    }
}
