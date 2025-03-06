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
public class CrmServiceClient {

    private final WebClient webClient;
    @Value("${app.crm-service.methods.get-customer-inns}")
    private String getCustomerInnsUri;

    public CrmServiceClient(WebClient.Builder webClientBuilder, @Value("${app.crm-service.host}") String host) {
        this.webClient = webClientBuilder.baseUrl(host).build();
    }

    public CompletableFuture<Map<String, String>> getCustomerInnsAsync(List<String> customersLogin) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(getCustomerInnsUri)
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
