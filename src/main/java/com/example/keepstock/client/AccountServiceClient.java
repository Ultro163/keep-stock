package com.example.keepstock.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class AccountServiceClient {

    private final WebClient webClient;
    @Value("${app.account-service.methods.get-accounts}")
    private String getAccountsUri;

    public AccountServiceClient(WebClient.Builder webClientBuilder, @Value("${app.account-service.host}") String host) {
        this.webClient = webClientBuilder.baseUrl(host).build();
    }

    public CompletableFuture<Map<String, String>> getAccountsAsync(List<String> customersLogin) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(getAccountsUri)
                        .queryParam("customersLogin", String.join(",", customersLogin))
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })
                .toFuture()
                .exceptionally(ex -> {
                    log.error("Ошибка при получении счетов: {}", ex.getMessage());
                    return null;
                });
    }
}