package com.example.keepstock.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link com.example.keepstock.model.Category}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCategoryRequest {
    @NotNull
    @Size(max = 255)
    @NotBlank
    private String name;
}