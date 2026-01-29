package com.michalswistowski.currency_service.dto;

import java.util.Map;

public record CurrencyExchangeRatesResponse(Data data) {
    public record Data(String currency, Map<String, String> rates) {
        public Data {
            System.out.println(rates.getClass().getName());
        }
    }
}
