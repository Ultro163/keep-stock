package com.example.keepstock.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for {@link CategoryDto}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCategoryDto {
    private UUID id;
    private String name;
}