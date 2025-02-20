package com.example.keepstock.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for {@link com.example.keepstock.model.Category}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    private UUID id;
    private String name;
}