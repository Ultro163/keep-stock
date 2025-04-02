package com.example.keepstock.kafka;

import com.example.keepstock.dto.event.Event;
import com.example.keepstock.dto.product.OrderProductRequest;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderCreatedEventDate implements KafkaEvent {

    private Long customerId;
    private String deliveryAddress;
    private List<OrderProductRequest> products;

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Event getEvent() {
        return Event.CREATE_ORDER;
    }
}