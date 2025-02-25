package com.example.keepstock.dto.product;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductFilterDto {
    private String name;
    @Positive
    private BigDecimal price;
    @Positive
    private Long quantity;
    @PositiveOrZero
    private int page = 0;
    @Positive
    private int size = 10;
}