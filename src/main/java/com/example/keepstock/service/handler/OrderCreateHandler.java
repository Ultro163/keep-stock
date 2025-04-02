package com.example.keepstock.service.handler;

import com.example.keepstock.dto.event.Event;
import com.example.keepstock.dto.event.EventSource;
import com.example.keepstock.kafka.OrderCreatedEventDate;
import com.example.keepstock.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class OrderCreateHandler implements EventHandler<OrderCreatedEventDate> {

    private final OrderService orderService;

    @Override
    public boolean canHandle(final EventSource eventSource) {
        Assert.notNull(eventSource, "EventSource must not be null");
        return Event.CREATE_ORDER.equals(eventSource.getEvent());
    }

    @Override
    public String handleEvent(OrderCreatedEventDate eventSource) {
        return orderService.addOrder(eventSource.getCustomerId(),
                eventSource.getDeliveryAddress(),
                eventSource.getProducts()).toString();
    }
}
