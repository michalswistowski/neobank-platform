package com.michalswistowski.currency_service.exception;

import java.time.LocalDateTime;

public record ExceptionResponse(LocalDateTime time, int statusCode, String message) {
}
