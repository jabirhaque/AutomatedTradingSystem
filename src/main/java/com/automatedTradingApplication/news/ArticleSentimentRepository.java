package com.automatedTradingApplication.news;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArticleSentimentRepository extends MongoRepository<ArticleSentiment, String> {
    ArticleSentiment findTopByOrderByCreatedDesc();
}
