package com.example.keepstock.dto.customer;

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
    private String login;
    private String email;
    private Boolean isActive;
}