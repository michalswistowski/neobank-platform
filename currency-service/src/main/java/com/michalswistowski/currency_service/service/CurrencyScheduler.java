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

    @Scheduled(initialDelay = 30000, fixedDelay = 30000)
    public void requestAndUpdateExchangeRates() {

        System.out.println("Updating exchange rates...");

        List<CurrencyResponse> activeCurrencies = currencyService.getAllActiveCurrencies();

        activeCurrencies.forEach(currency ->
                currencyService.getAndUpdateRate(currency.symbol(), activeCurrencies));

        System.out.println("Updated exchange rates");
    }
}
