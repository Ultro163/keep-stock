package com.example.keepstock.dto.mappers;

import com.example.keepstock.dto.order.NewOrderDto;
import com.example.keepstock.dto.order.NewOrderRequest;
import com.example.keepstock.dto.order.OrderDto;
import com.example.keepstock.dto.order.ResponseOrderDto;
import com.example.keepstock.dto.order.UpdateOrderDto;
import com.example.keepstock.dto.order.UpdateOrderRequest;
import com.example.keepstock.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CustomerMapper.class, ProductMapper.class})
public interface OrderMapper {
    Order toEntity(OrderDto orderDto);

    OrderDto toOrderDto(Order order);

    OrderDto toOrderDtoFromNewOrderRequest(NewOrderRequest newOrderRequest);

    @Mapping(source = "customer.id", target = "customerId")
    NewOrderDto toNewOrderDto(OrderDto orderDto);

    OrderDto toOrderDtoFromUpdateOrderRequest(UpdateOrderRequest updateOrderRequest);

    @Mapping(source = "customer.id", target = "customerId")
    UpdateOrderDto toUpdateOrderDto(OrderDto orderDto);

    ResponseOrderDto toResponseOrderDto(OrderDto orderDto);
}