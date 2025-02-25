package com.example.keepstock.service;

import com.example.keepstock.model.Currency;

import java.math.BigDecimal;


public class CurrencyServiceMock implements CurrencyService {


    @Override
    public BigDecimal getActualRate(Currency currency) {
        return null;
    }
}
