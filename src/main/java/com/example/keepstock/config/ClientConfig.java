package com.example.keepstock.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.client.DefaultClientRequestObservationConvention;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    public WebClient accountServiceWebClient(WebClient.Builder keepStockServicesWebClientBuilder) {
        return keepStockServicesWebClientBuilder
                .filter(peerServiceTaggingFilter("account-service"))
                .baseUrl("http://account-service:9191")
                .build();
    }

    @Bean
    public WebClient crmServiceWebClient(WebClient.Builder keepStockServicesWebClientBuilder) {
        return keepStockServicesWebClientBuilder
                .filter(peerServiceTaggingFilter("crm-service"))
                .baseUrl("http://crm-service:9292")
                .build();
    }

    @Bean
    public WebClient exchangeRatesServiceWebClient(WebClient.Builder keepStockServicesWebClientBuilder) {
        return keepStockServicesWebClientBuilder
                .filter(peerServiceTaggingFilter("exchangerates-service"))
                .baseUrl("http://exchange-rates:9393")
                .build();
    }

    @Bean
    @Scope("prototype")
    public WebClient.Builder keepStockServicesWebClientBuilder(ObservationRegistry observationRegistry) {
        return WebClient.builder()
                .observationRegistry(observationRegistry)
                .observationConvention(new DefaultClientRequestObservationConvention());
    }

    private ExchangeFilterFunction peerServiceTaggingFilter(String peerServiceName) {
        return (request, next) -> {
            request.attributes().put("peer.service", peerServiceName);
            return next.exchange(request);
        };
    }
}
