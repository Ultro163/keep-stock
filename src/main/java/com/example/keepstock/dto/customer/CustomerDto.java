package com.example.keepstock.dto.customer;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link com.example.keepstock.entity.Customer}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    private Long id;
    @NotNull
    @Size(max = 55)
    private String login;
    @NotNull
    @Size(max = 55)
    private String email;
    @NotNull
    private Boolean isActive = false;
}