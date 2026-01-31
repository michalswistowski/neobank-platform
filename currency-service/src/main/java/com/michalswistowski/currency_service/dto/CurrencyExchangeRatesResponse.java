package com.michalswistowski.currency_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record CurrencyExchangeRatesResponse(@JsonProperty("data") Data data) {
    public record Data(@JsonProperty("currency") String currency,@JsonProperty("rates") Map<String, String> rates) {
    }
}
