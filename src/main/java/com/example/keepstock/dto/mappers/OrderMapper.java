package com.example.keepstock.dto.mappers;

import com.example.keepstock.dto.order.OrderDto;
import com.example.keepstock.dto.order.ResponseOrderDto;
import com.example.keepstock.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CustomerMapper.class, ProductMapper.class})
public interface OrderMapper {

    OrderDto toOrderDto(Order order);

    ResponseOrderDto toResponseOrderDto(OrderDto orderDto);
}