package com.michalswistowski.currency_service.controller;

import com.michalswistowski.currency_service.dto.CurrencyExchangeRatesResponse;
import com.michalswistowski.currency_service.dto.CurrencyRequest;
import com.michalswistowski.currency_service.dto.CurrencyResponse;
import com.michalswistowski.currency_service.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private final CurrencyService currencyService;


    @GetMapping("/exchangeRates/{currencySymbol}")
    public ResponseEntity<CurrencyExchangeRatesResponse> getExchangeRate(
            @PathVariable String currencySymbol) {
        CurrencyExchangeRatesResponse response = currencyService.getExchangeRates(currencySymbol);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CurrencyResponse>> getAllCurrencies() {
        return ResponseEntity.ok(currencyService.getAllCurrencies());
    }

    @PostMapping
    public ResponseEntity<CurrencyResponse> addCurrency(@RequestBody CurrencyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(currencyService.addCurrency(request));
    }
}
