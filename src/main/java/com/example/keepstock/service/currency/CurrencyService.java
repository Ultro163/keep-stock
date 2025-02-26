package com.example.keepstock.service.currency;

import com.example.keepstock.model.Currency;

import java.math.BigDecimal;

public interface CurrencyService {

    BigDecimal getExchangeRate(Currency currency);
}