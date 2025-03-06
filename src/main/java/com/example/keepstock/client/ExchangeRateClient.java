package com.example.keepstock.client;

import com.example.keepstock.dto.product.ExchangeRate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@CacheConfig(cacheNames = "exchangeRateCache")
@Component
public class ExchangeRateClient {

    private final WebClient webClient;
    @Value("${app.exchange-rate.methods.get-actual-rate}")
    private String getActualRateUri;

    public ExchangeRateClient(WebClient.Builder webClientBuilder, @Value("${app.exchange-rate.host}") String host) {
        this.webClient = webClientBuilder.baseUrl(host).build();
    }

    @Cacheable(key = "'exchangeRate'", unless = "#result == null")
    public ExchangeRate getActualRate() {
        try {
            return webClient.get()
                    .uri(getActualRateUri)
                    .retrieve()
                    .bodyToMono(ExchangeRate.class)
                    .switchIfEmpty(Mono.error(new RuntimeException("Получен null")))
                    .retry(2)
                    .block();
        } catch (Exception e) {
            log.error("Ошибка при получении курса", e);
            return null;
        }
    }
}