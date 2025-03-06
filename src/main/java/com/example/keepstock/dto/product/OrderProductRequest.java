package com.example.keepstock.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for {@link com.example.keepstock.dto.product.ProductDto}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductRequest {
    private UUID id;
    private Long quantity;
}