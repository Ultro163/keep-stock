package com.example.keepstock.dto.order;

import com.example.keepstock.dto.customer.CustomerDto;
import com.example.keepstock.dto.product.OrderProductResponse;
import com.example.keepstock.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * DTO for {@link com.example.keepstock.entity.Order}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private UUID id;
    private CustomerDto customer;
    private OrderStatus status;
    private String deliveryAddress;
    private List<OrderProductResponse> products = new ArrayList<>();
    private BigDecimal totalPrice;
}