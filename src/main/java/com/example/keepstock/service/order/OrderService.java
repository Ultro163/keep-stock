package com.example.keepstock.service.order;

import com.example.keepstock.dto.order.OrderDto;
import com.example.keepstock.service.CrudService;

import java.util.UUID;

public interface OrderService extends CrudService<OrderDto, UUID> {

    OrderDto getCustomerOrder(UUID orderId, Long customerId);
}