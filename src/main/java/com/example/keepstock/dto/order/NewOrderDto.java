package com.example.keepstock.dto.order;

import com.example.keepstock.dto.product.OrderProductRequest;
import com.example.keepstock.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link OrderDto}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewOrderDto {
    private UUID id;
    private Long customerId;
    private OrderStatus status;
    private String deliveryAddress;
    private Set<OrderProductRequest> products;
}