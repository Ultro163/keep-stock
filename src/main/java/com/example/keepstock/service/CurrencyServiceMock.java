package com.example.keepstock.service;

import com.example.keepstock.dto.product.ExchangeRate;
import com.example.keepstock.model.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Slf4j
@Service
@ConditionalOnExpression(value = "'${app.currency-service.model}' == 'mock'")
public class CurrencyServiceMock implements CurrencyService {
    private final ExchangeRate exchangeRate;

    public CurrencyServiceMock() {
        this.exchangeRate = new ExchangeRate();
        Random random = new Random();
        exchangeRate.setExchangeRateCNY(BigDecimal.valueOf(11 + (4 * random.nextDouble())));
        exchangeRate.setExchangeRateUSD(BigDecimal.valueOf(90 + (4 * random.nextDouble())));
        exchangeRate.setExchangeRateEUR(BigDecimal.valueOf(100 + (4 * random.nextDouble())));
    }

    @Override
    public BigDecimal getExchangeRate(Currency currency) {
        log.info("Get Random Exchange Rate for {}", currency);
        return switch (currency) {
            case RUB -> BigDecimal.ONE;
            case CNY -> exchangeRate.getExchangeRateCNY();
            case USD -> exchangeRate.getExchangeRateUSD();
            case EUR -> exchangeRate.getExchangeRateEUR();
        };
    }
}