package com.example.keepstock.dto.order;

import com.example.keepstock.dto.product.OrderProductResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link OrderDto}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseOrderDto {
    private UUID id;
    private Set<OrderProductResponse> products;
    private BigDecimal totalPrice;

}