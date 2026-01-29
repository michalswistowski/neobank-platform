package com.michalswistowski.currency_service.dto;

import java.time.LocalDateTime;

public record CurrencyResponse(Long id, String symbol, boolean active, LocalDateTime createdAt) {}
