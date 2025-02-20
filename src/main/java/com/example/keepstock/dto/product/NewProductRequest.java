package com.example.keepstock.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class NewProductRequest {
    @NotNull
    @Size(max = 55)
    @NotBlank
    private String name;
    @NotNull
    private String article;
    @NotNull
    @Size(max = 255)
    @NotBlank
    private String description;
    @NotNull
    private UUID categoryId;
    @NotNull
    @PositiveOrZero
    private BigDecimal price;
    @NotNull
    @PositiveOrZero
    private Long quantity;
}