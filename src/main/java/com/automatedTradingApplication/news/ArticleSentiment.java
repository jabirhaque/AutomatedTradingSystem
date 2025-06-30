package com.automatedTradingApplication.news;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document
@Builder
public class ArticleSentiment {
    @Id
    private String id;
    private String article;
    private String publisher;
    private double score;
    private String ticker;
    private LocalDateTime created;
}