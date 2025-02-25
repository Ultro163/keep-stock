package com.example.keepstock.service;

import com.example.keepstock.model.Currency;

import java.math.BigDecimal;

public interface CurrencyService {

    BigDecimal getExchangeRate(Currency currency);
}