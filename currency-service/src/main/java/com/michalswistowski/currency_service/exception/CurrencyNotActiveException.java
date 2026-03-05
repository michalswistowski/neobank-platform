package com.michalswistowski.currency_service.exception;

public class CurrencyNotActiveException extends RuntimeException {
    public CurrencyNotActiveException(String message) {
        super(message);
    }
}
