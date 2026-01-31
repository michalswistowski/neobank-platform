package com.michalswistowski.currency_service.service;

import com.michalswistowski.currency_service.dto.CurrencyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyScheduler {

    private final CurrencyService currencyService;

    @Scheduled(initialDelay = 30000, fixedDelay = 300000)
    public void requestAndUpdateExchangeRates() {

        System.out.println("Updating exchange rates...");

//      todo change for active only
        List<CurrencyResponse> activeCurrencies = currencyService.getAllCurrencies();

        activeCurrencies.forEach(currency ->
                currencyService.updateExchangeRates(currency.symbol()));
        System.out.println("Updated exchange rates");
    }
}
