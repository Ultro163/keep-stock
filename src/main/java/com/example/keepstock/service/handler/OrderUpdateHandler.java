package com.example.keepstock.service.handler;

import com.example.keepstock.dto.event.Event;
import com.example.keepstock.dto.event.EventSource;
import com.example.keepstock.kafka.OrderUpdatedEventDate;
import com.example.keepstock.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class OrderUpdateHandler implements EventHandler<OrderUpdatedEventDate> {
    private final OrderService orderService;

    @Override
    public boolean canHandle(EventSource eventSource) {
        Assert.notNull(eventSource, "EventSource must not be null");
        return Event.UPDATE_ORDER.equals(eventSource.getEvent());
    }

    @Override
    public String handleEvent(OrderUpdatedEventDate eventSource) {
        return orderService.updateOrder(eventSource.getCustomerId(),
                eventSource.getOrderId(),
                eventSource.getDeliveryAddress(),
                eventSource.getProducts()).toString();
    }
}
