package com.example.keepstock.controller;

import com.example.keepstock.dto.customer.CustomerDto;
import com.example.keepstock.dto.mappers.OrderMapper;
import com.example.keepstock.dto.order.NewOrderDto;
import com.example.keepstock.dto.order.NewOrderRequest;
import com.example.keepstock.dto.order.OrderDto;
import com.example.keepstock.dto.order.ResponseOrderDto;
import com.example.keepstock.dto.order.UpdateOrderDto;
import com.example.keepstock.dto.order.UpdateOrderRequest;
import com.example.keepstock.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    public NewOrderDto newOrder(@RequestBody @Valid NewOrderRequest newOrderRequest, @RequestHeader Long customerId) {
        OrderDto dto = orderMapper.toOrderDtoFromNewOrderRequest(newOrderRequest);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customerId);
        dto.setCustomer(customerDto);
        return orderMapper.toNewOrderDto(orderService.save(dto));
    }

    @PatchMapping("/{orderId}")
    public UpdateOrderDto updateOrder(@RequestBody @Valid UpdateOrderRequest updateOrderRequest,
                                      @PathVariable UUID orderId, @RequestHeader Long customerId) {
        OrderDto dto = orderMapper.toOrderDtoFromUpdateOrderRequest(updateOrderRequest);
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(customerId);
        dto.setCustomer(customerDto);
        dto.setId(orderId);
        return orderMapper.toUpdateOrderDto(orderService.update(dto));
    }

    @GetMapping("/{orderId}")
    public ResponseOrderDto getById(@PathVariable UUID orderId) {
        return orderMapper.toResponseOrderDto(orderService.getById(orderId));
    }

    @GetMapping("/customer/order/{orderId}")
    public ResponseOrderDto getCustomerOrder(@PathVariable UUID orderId, @RequestHeader Long customerId) {
        return orderMapper.toResponseOrderDto(orderService.getCustomerOrder(orderId, customerId));
    }
}