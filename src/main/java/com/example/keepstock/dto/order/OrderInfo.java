package com.example.keepstock.dto.order;

import com.example.keepstock.dto.customer.CustomerInfo;
import com.example.keepstock.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for {@link com.example.keepstock.entity.Order}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInfo {
    private UUID id;
    private CustomerInfo customer;
    private OrderStatus status;
    private String deliveryAddress;
    private Long quantity;
}