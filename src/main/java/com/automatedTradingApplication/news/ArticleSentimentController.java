package com.automatedTradingApplication.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ArticleSentimentController {

    @Autowired
    private ArticleSentimentRepository articleSentimentRepository;

    @GetMapping("api/articleSentiment")
    public ResponseEntity<List<ArticleSentiment>> getArticleSentiment(){
        return ResponseEntity.ok(articleSentimentRepository.findAll());
    }
}
