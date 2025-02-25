package com.example.keepstock.service;

import com.example.keepstock.dto.product.ExchangeRate;
import com.example.keepstock.error.exception.ExchangeRateException;
import com.example.keepstock.model.Currency;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private static final String FILE_PATH = "src/main/resources/exchange-rate.json";

    private final ObjectMapper objectMapper;


    private ExchangeRate getSaveExchangeRate() throws IOException {
        return objectMapper.readValue(new File(FILE_PATH), ExchangeRate.class);
    }

    public BigDecimal getActualRate(Currency currency) {
        try {
            ExchangeRate exchangeRate = getSaveExchangeRate();
            return switch (currency) {
                case RUB -> BigDecimal.ONE;
                case CNY -> exchangeRate.getExchangeRateCNY();
                case USD -> exchangeRate.getExchangeRateUSD();
                case EUR -> exchangeRate.getExchangeRateEUR();
            };
        } catch (IOException e) {
            log.error("Error");
            throw new ExchangeRateException("Failed to fetch exchange rate");
        }
    }
}