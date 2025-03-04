package com.example.keepstock.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for {@link ProductDto}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductResponse {
    private UUID id;
    private String name;
    private BigDecimal price;
    private Long quantity;
}