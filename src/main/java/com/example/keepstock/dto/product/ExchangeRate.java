package com.example.keepstock.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {
    private BigDecimal exchangeRateCNY;
    private BigDecimal exchangeRateUSD;
    private BigDecimal exchangeRateEUR;
}