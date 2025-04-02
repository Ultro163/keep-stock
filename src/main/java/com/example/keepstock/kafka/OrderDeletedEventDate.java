package com.example.keepstock.kafka;

import com.example.keepstock.dto.event.Event;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderDeletedEventDate implements KafkaEvent {

    private Long customerId;
    private UUID orderId;

    @Override
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Event getEvent() {
        return Event.DELETE_ORDER;
    }
}
