package com.example.keepstock.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
public class UpdateProductRequest {
    @Size(max = 55)
    @NotBlank
    private String name;
    @Size(max = 255)
    @NotBlank
    private String description;
    private UUID categoryId;
    @PositiveOrZero
    private BigDecimal price;
    @PositiveOrZero
    private Long quantity;
}