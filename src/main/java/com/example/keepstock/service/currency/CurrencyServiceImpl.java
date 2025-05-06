package com.example.keepstock.service.currency;

import com.example.keepstock.client.ExchangeRateClient;
import com.example.keepstock.dto.product.ExchangeRate;
import com.example.keepstock.error.exception.ExchangeRateException;
import com.example.keepstock.model.Currency;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

@Slf4j
@Service
@ConditionalOnExpression(value = "'${app.currency-service.model}' == 'work'")
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    @Value("${app.exchange-rate-file.path}")
    private String filePath;

    private final ObjectMapper objectMapper;

    private final ExchangeRateClient rateClient;

    @Override
    public BigDecimal getExchangeRate(Currency currency) {
        ExchangeRate exchangeRate = rateClient.getActualRate();
        if (exchangeRate == null) {
            log.info("Reading exchange rate from file {}", filePath);
            try {
                exchangeRate = getSaveExchangeRate();
            } catch (IOException e) {
                log.error("Error");
                throw new ExchangeRateException("Failed to fetch exchange rate");
            }
        }
        return switch (currency) {
            case RUB -> BigDecimal.ONE;
            case CNY -> exchangeRate.getExchangeRateCNY();
            case USD -> exchangeRate.getExchangeRateUSD();
            case EUR -> exchangeRate.getExchangeRateEUR();
        };
    }

    private ExchangeRate getSaveExchangeRate() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new ExchangeRateException("Exchange rate file not found in resources: " + filePath);
            }
            return objectMapper.readValue(inputStream, ExchangeRate.class);
        }
    }

}