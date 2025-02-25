package com.example.keepstock.error.exception;

public class ExchangeRateException extends RuntimeException {
    public ExchangeRateException(String message) {
        super(message);
    }
}