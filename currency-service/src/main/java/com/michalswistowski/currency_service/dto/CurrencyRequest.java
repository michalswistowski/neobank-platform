package com.michalswistowski.currency_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CurrencyRequest(
        @NotBlank(message = "Symbol must not be blank!")
        @Size(max = 3, message = "Symbol must be 3 characters!")
        @Size(min = 3, message = "Symbol must be 3 characters!")
        String symbol) {}
