package com.example.keepstock.kafka;

import com.example.keepstock.dto.event.Event;
import com.example.keepstock.model.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderUpdatedStatusEventDate implements KafkaEvent {

    private UUID orderId;
    private Long customerId;
    private OrderStatus status;

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Event getEvent() {
        return Event.UPDATE_ORDER_STATUS;
    }
}