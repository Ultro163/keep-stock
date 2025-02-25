package com.example.keepstock.common;

import com.example.keepstock.model.Currency;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Getter
@Component
@SessionScope
public class CurrencyProvider {

    private Currency currency = Currency.RUB;

    public void setCurrency(String currency) {
        this.currency = Currency.valueOf(currency);
    }
}