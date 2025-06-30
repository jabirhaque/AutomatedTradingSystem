package com.automatedTradingApplication.news;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class NewsController {

    @Autowired
    private NewsClient newsClient;

    @Autowired
    SentimentService sentimentService;

    @GetMapping("api/news")
    public ResponseEntity<String> getNews(){
        return newsClient.getNews();
    }

    @GetMapping("api/news/sentiment")
    public ResponseEntity<ArticleSentiment> getSentiment(){
        return ResponseEntity.ok(sentimentService.callArticleSentiment());
    }

}
