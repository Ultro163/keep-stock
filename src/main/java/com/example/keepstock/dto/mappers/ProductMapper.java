package com.example.keepstock.dto.mappers;

import com.example.keepstock.dto.product.NewProductDto;
import com.example.keepstock.dto.product.NewProductRequest;
import com.example.keepstock.dto.product.OrderProductRequest;
import com.example.keepstock.dto.product.OrderProductResponse;
import com.example.keepstock.dto.product.ProductDto;
import com.example.keepstock.dto.product.ProductResponseDto;
import com.example.keepstock.dto.product.UpdateProductDto;
import com.example.keepstock.dto.product.UpdateProductRequest;
import com.example.keepstock.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {CategoryMapper.class})
public interface ProductMapper {
    Product toEntity(ProductDto productDto);

    ProductDto toProductDto(Product product);

    NewProductDto toNewProductDto(ProductDto productDto);

    @Mapping(source = "categoryId", target = "category.id")
    ProductDto toProductDtoFromNewProductRequest(NewProductRequest newProductRequest);

    UpdateProductDto toUpdateProductDto(ProductDto productDto);

    @Mapping(source = "categoryId", target = "category.id")
    ProductDto toProductDtoFromUpdateProductRequest(UpdateProductRequest updateProductRequest);

    ProductResponseDto toProductResponseDto(ProductDto productDto);

    ProductDto toProductDtoFromOrderProductRequest(OrderProductRequest orderProductRequest);

    OrderProductRequest toOrderProductDto(ProductDto productDto);

    ProductDto toProductDtoFromOrderProductResponse(OrderProductResponse orderProductResponse);

    OrderProductResponse toOrderProductResponse(ProductDto productDto);
}