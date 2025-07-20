package com.automatedTradingApplication.alpaca;

import net.jacobpeterson.alpaca.openapi.trader.ApiException;
import net.jacobpeterson.alpaca.openapi.trader.model.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PositionsController {

    @Autowired
    private AlpacaApiWrapper alpacaApiWrapper;

    @GetMapping("/api/positions")
    public ResponseEntity<List<Position>> getPositions() throws ApiException {
        return ResponseEntity.ok(alpacaApiWrapper.getPositions());
    }
}
