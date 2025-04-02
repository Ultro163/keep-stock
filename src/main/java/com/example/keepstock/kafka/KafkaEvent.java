package com.example.keepstock.kafka;

import com.example.keepstock.dto.event.EventSource;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "event"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderCreatedEventDate.class, name = "CREATE_ORDER"),
        @JsonSubTypes.Type(value = OrderUpdatedEventDate.class, name = "UPDATE_ORDER"),
        @JsonSubTypes.Type(value = OrderDeletedEventDate.class, name = "DELETE_ORDER"),
        @JsonSubTypes.Type(value = OrderUpdatedStatusEventDate.class, name = "UPDATE_ORDER_STATUS")
})
public interface KafkaEvent extends EventSource {
}