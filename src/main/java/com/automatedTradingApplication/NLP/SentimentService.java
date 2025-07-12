package com.automatedTradingApplication.NLP;

import com.automatedTradingApplication.news.ArticleSentiment;
import com.automatedTradingApplication.news.JsonAttributeExtractor;
import com.automatedTradingApplication.news.NewsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SentimentService {
    @Autowired
    private NewsClient newsClient;

    @Autowired
    JsonAttributeExtractor jsonAttributeExtractor;

    @Autowired
    SentimentAnalyser sentimentAnalyser;

    public ArticleSentiment callArticleSentiment(){
        ResponseEntity<String> response = newsClient.getNews();

        String jsonString = response.getBody();
        String article = jsonAttributeExtractor.extractTitle(jsonString) + " " + jsonAttributeExtractor.extractDescription(jsonString);
        String publisher = jsonAttributeExtractor.extractPublisherName(jsonString);
        List<String> tickers = jsonAttributeExtractor.extractTickers(jsonString);
        double sentiment = sentimentAnalyser.analyzeSentiment(article);

        return ArticleSentiment.builder().article(article).publisher(publisher).score(sentiment).ticker(tickers.getFirst()).created(LocalDateTime.now()).build();
    }
}
