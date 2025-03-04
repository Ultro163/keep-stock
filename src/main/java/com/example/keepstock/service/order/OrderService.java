package com.example.keepstock.service.order;

import com.example.keepstock.dto.order.OrderDto;
import com.example.keepstock.dto.product.OrderProductRequest;
import com.example.keepstock.model.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    UUID addOrder(Long customerId, String deliveryAddress, List<OrderProductRequest> productsRequest);

    UUID updateOrder(Long customerId, UUID orderId, String deliveryAddress, List<OrderProductRequest> productsRequest);

    void deleteOrder(Long customerId, UUID orderId);

    OrderDto getCustomerOrder(UUID orderId, Long customerId);

    void confirmOrder(UUID orderId, Long customerId);

    void changeOrderStatus(UUID orderId, Long customerId,
                           OrderStatus orderStatus);
}