package com.example.keepstock.dto.order;

import com.example.keepstock.dto.customer.CustomerDto;
import com.example.keepstock.dto.product.ProductDto;
import com.example.keepstock.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * DTO for {@link com.example.keepstock.entity.Order}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private UUID id;
    @NotNull
    private CustomerDto customer;
    @NotNull
    @Size(max = 25)
    private OrderStatus status;
    @NotNull
    @Size(max = 255)
    private String deliveryAddress;
    private Set<ProductDto> products = new HashSet<>();
}