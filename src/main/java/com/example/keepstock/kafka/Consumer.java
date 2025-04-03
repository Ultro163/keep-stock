package com.example.keepstock.kafka;

import com.example.keepstock.dto.event.EventSource;
import com.example.keepstock.service.handler.EventHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnExpression(value = "'${app.kafka.enabled}' == 'true'")
public class Consumer {

    private final Set<EventHandler<EventSource>> eventHandlers;

    @KafkaListener(
            topics = "keep-stock",
            containerFactory = "kafkaListenerContainerFactoryString",
            groupId = "keep-stock-group"
    )
    public void listenOrderEventMessage(String message) {
        log.info("Receive message: {}", message);

        final ObjectMapper objectMapper = new ObjectMapper();

        try {
            final KafkaEvent eventSource = objectMapper.readValue(message, KafkaEvent.class);
            log.info("EventSource: {}", eventSource);

            String result = eventHandlers.stream()
                    .filter(eventSourceEventHandler -> eventSourceEventHandler.canHandle(eventSource))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Handler for eventsource not found"))
                    .handleEvent(eventSource);

            log.info("Event processed with result: {}", result);

        } catch (JsonProcessingException e) {
            log.error("Couldn't parse message: {}; exception: ", message, e);
        }
    }
}