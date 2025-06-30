package com.automatedTradingApplication.news;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JsonAttributeExtractor {

    public String extractPublisherName(String jsonString) {
        return extractAttribute(jsonString, "\"publisher\"\\s*:\\s*\\{[^\\}]*\"name\"\\s*:\\s*\"([^\"]*)\"");
    }

    public String extractTitle(String jsonString) {
        return extractAttribute(jsonString, "\"title\"\\s*:\\s*\"([^\"]*)\"");
    }

    public String extractPublishedUtc(String jsonString) {
        return extractAttribute(jsonString, "\"published_utc\"\\s*:\\s*\"([^\"]*)\"");
    }

    public List<String> extractTickers(String jsonString) {
        return extractTickers(jsonString, "\"tickers\"\\s*:\\s*\\[([^\\]]*)\\]");
    }

    public String extractDescription(String jsonString) {
        return extractAttribute(jsonString, "\"description\"\\s*:\\s*\"([^\"]*)\"");
    }

    private String extractAttribute(String jsonString, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsonString);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private List<String> extractTickers(String jsonString, String regex) {
        String tickersString = extractAttribute(jsonString, regex);
        if (tickersString != null) {
            return Arrays.asList(tickersString.replaceAll("\"", "").split(","));
        }
        return null;
    }
}