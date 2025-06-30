package com.automatedTradingApplication.news;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NewsClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${polygon.api.key}")
    private String apiKey;

    public ResponseEntity<String> getNews(){
        String url = "https://api.polygon.io/v2/reference/news?limit=1&apiKey=" + apiKey;
        return restTemplate.getForEntity(url, String.class);
    }

}