package com.example.keepstock.service.handler;

import com.example.keepstock.dto.event.Event;
import com.example.keepstock.dto.event.EventSource;
import com.example.keepstock.kafka.OrderDeletedEventDate;
import com.example.keepstock.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class OrderDeleteHandler implements EventHandler<OrderDeletedEventDate> {
    private final OrderService orderService;

    @Override
    public boolean canHandle(EventSource eventSource) {
        Assert.notNull(eventSource, "EventSource must not be null");
        return Event.DELETE_ORDER.equals(eventSource.getEvent());
    }

    @Override
    public String handleEvent(OrderDeletedEventDate eventSource) {
        orderService.deleteOrder(eventSource.getCustomerId(), eventSource.getOrderId());
        return "Order deleted";
    }
}
