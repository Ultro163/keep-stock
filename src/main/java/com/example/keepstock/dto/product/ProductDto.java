package com.example.keepstock.dto.product;

import com.example.keepstock.dto.category.CategoryDto;
import com.example.keepstock.entity.Product;
import com.example.keepstock.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO for {@link Product}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDto {
    private UUID id;
    private String name;
    private String article;
    private String description;
    private CategoryDto category;
    private BigDecimal price;
    private Long quantity;
    private OffsetDateTime lastQuantityUpdate;
    private OffsetDateTime createdAt;
    private Currency currency;
}