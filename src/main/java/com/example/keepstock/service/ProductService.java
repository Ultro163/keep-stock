package com.example.keepstock.service;

import com.example.keepstock.dto.product.FilterCriteriaDto;
import com.example.keepstock.dto.product.ProductDto;
import com.example.keepstock.dto.product.ProductFilterDto;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService extends CrudService<ProductDto, UUID> {
    List<ProductDto> findAllProductsByFilters(ProductFilterDto filter);

    List<ProductDto> findProductsByMultipleFilters(List<FilterCriteriaDto> filters, Pageable pageable);
}