package com.example.keepstock.dto.mappers;

import com.example.keepstock.dto.customer.CustomerDto;
import com.example.keepstock.dto.customer.CustomerInfo;
import com.example.keepstock.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerMapper {

    CustomerDto toCustomerDto(Customer customer);

    Customer toEntity(CustomerInfo customerInfo);

    CustomerInfo toCustomerDetailsDTO(Customer customer);
}