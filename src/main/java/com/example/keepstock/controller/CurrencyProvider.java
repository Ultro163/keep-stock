package com.example.keepstock.controller;

import com.example.keepstock.model.Currency;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
@CacheConfig(cacheNames = "currencyCache")
public class CurrencyProvider {

    private Currency currency = Currency.RUB;

    @CacheEvict(key = "'currency_' + T(java.util.UUID).randomUUID().toString()")
    public void setCurrency(String currency) {
        this.currency = Currency.valueOf(currency);
    }

    @Cacheable(key = "'currency_' + T(java.util.UUID).randomUUID().toString()", unless = "#result == null")
    public Currency getCurrency() {
        return currency;
    }
}
