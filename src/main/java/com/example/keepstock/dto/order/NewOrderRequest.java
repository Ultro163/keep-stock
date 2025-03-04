package com.example.keepstock.dto.order;

import com.example.keepstock.dto.product.OrderProductRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for {@link OrderDto}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewOrderRequest {
    @NotBlank
    private String deliveryAddress;
    @NotNull
    private List<OrderProductRequest> products;
}