package com.example.keepstock.dto.order;

import com.example.keepstock.dto.product.OrderProductRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for {@link OrderDto}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderRequest {
    @NotNull
    private Set<OrderProductRequest> products;
}