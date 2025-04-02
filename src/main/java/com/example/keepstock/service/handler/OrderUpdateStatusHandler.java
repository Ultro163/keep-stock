package com.example.keepstock.service.handler;

import com.example.keepstock.dto.event.Event;
import com.example.keepstock.dto.event.EventSource;
import com.example.keepstock.kafka.OrderUpdatedStatusEventDate;
import com.example.keepstock.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class OrderUpdateStatusHandler implements EventHandler<OrderUpdatedStatusEventDate> {
    private final OrderService orderService;

    @Override
    public boolean canHandle(EventSource eventSource) {
        Assert.notNull(eventSource, "EventSource must not be null");
        return Event.UPDATE_ORDER_STATUS.equals(eventSource.getEvent());
    }

    @Override
    public String handleEvent(OrderUpdatedStatusEventDate eventSource) {
        orderService.changeOrderStatus(eventSource.getOrderId(),
                eventSource.getCustomerId(),
                eventSource.getStatus());
        return "Order status updated";
    }
}