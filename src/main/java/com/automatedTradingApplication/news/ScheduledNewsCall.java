package com.automatedTradingApplication.news;

import com.automatedTradingApplication.alpaca.AlpacaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledNewsCall {

    @Autowired
    private SentimentService sentimentService;

    @Autowired
    private AlpacaClient alpacaClient;

    @Autowired
    private ArticleSentimentRepository articleSentimentRepository;

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
                String volume = String.valueOf(Math.round((score*10000) * 100.0) / 100.0);
                alpacaClient.buyVolume(volume, ticker);
            }else{
                double volume = Math.round((score*-1000) * 100.0) / 100.0;
                alpacaClient.shortVolume(volume, ticker);
            }
        }
    }
}
