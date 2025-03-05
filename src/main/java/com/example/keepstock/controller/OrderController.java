package com.example.keepstock.controller;

import com.example.keepstock.dto.mappers.OrderMapper;
import com.example.keepstock.dto.order.NewOrderRequest;
import com.example.keepstock.dto.order.OrderInfo;
import com.example.keepstock.dto.order.ResponseOrderDto;
import com.example.keepstock.dto.order.UpdateOrderRequest;
import com.example.keepstock.model.OrderStatus;
import com.example.keepstock.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public UUID newOrder(@RequestBody @Valid NewOrderRequest newOrderRequest, @RequestHeader Long customerId) {
        return orderService.addOrder(customerId, newOrderRequest.getDeliveryAddress(), newOrderRequest.getProducts());
    }

    @PatchMapping("/{orderId}")
    public UUID updateOrder(@RequestBody @Valid UpdateOrderRequest updateOrderRequest,
                            @PathVariable UUID orderId, @RequestHeader Long customerId) {
        return orderService.updateOrder(customerId, orderId,
                updateOrderRequest.getDeliveryAddress(), updateOrderRequest.getProducts());
    }

    @GetMapping("/{orderId}")
    public ResponseOrderDto getCustomerOrder(@PathVariable UUID orderId, @RequestHeader Long customerId) {
        return orderMapper.toResponseOrderDto(orderService.getCustomerOrder(orderId, customerId));
    }

    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable UUID orderId, @RequestHeader Long customerId) {
        orderService.deleteOrder(customerId, orderId);
    }

    @PatchMapping("/{orderId}/confirm")
    public void confirmOrder(@PathVariable UUID orderId, @RequestHeader Long customerId) {
        orderService.confirmOrder(orderId, customerId);
    }

    @PatchMapping("/{orderId}/status")
    public void changeOrderStatus(@PathVariable UUID orderId, @RequestHeader Long customerId,
                                  @RequestBody OrderStatus orderStatus) {
        orderService.changeOrderStatus(orderId, customerId, orderStatus);
    }

    @GetMapping("/info")
    public Map<UUID, List<OrderInfo>> getProductOrdersWithClients() {
        return orderService.getProductOrdersWithClients();
    }

}