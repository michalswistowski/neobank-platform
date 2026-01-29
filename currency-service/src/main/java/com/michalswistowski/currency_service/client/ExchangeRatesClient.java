package com.michalswistowski.currency_service.client;

import com.michalswistowski.currency_service.dto.CurrencyExchangeRatesResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(accept = "application/json")
public interface ExchangeRatesClient {

    @GetExchange("/v2/exchange-rates?currency={currencySymbol}")
    CurrencyExchangeRatesResponse getExchangeRates(@PathVariable String currencySymbol);
}
