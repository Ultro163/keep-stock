package com.example.keepstock.dto.category;

import com.example.keepstock.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for {@link Category}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryDto {
    private UUID id;
    private String name;
}