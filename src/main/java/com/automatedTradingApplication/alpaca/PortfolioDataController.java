package com.automatedTradingApplication.alpaca;

import lombok.extern.slf4j.Slf4j;
import net.jacobpeterson.alpaca.openapi.trader.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class PortfolioDataController {

    @Autowired
    private PortfolioDataService portfolioDataService;

    @GetMapping("api/portfolio/week")
    public ResponseEntity<List<PortfolioData>> getPortfolioDataWeek() throws ApiException {
        return ResponseEntity.ok(portfolioDataService.portfolioDataWeek());
    }

    @GetMapping("api/portfolio/month")
    public ResponseEntity<List<PortfolioData>> getPortfolioDataMonth() throws ApiException {
        return ResponseEntity.ok(portfolioDataService.portfolioDataMonth());
    }

    @GetMapping("api/portfolio/year")
    public ResponseEntity<List<PortfolioData>> getPortfolioDataYear() throws ApiException {
        return ResponseEntity.ok(portfolioDataService.portfolioDataYear());
    }

}
