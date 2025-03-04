package com.example.keepstock.service.product;

import com.example.keepstock.dto.product.ProductDto;
import com.example.keepstock.dto.product.ProductFilterDto;
import com.example.keepstock.dto.product.criteria.FilterCriteriaDto;
import com.example.keepstock.service.CrudService;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ProductService extends CrudService<ProductDto, UUID> {
    List<ProductDto> findAllProductsByFilters(ProductFilterDto filter);

    List<ProductDto> findProductsByMultipleFilters(List<FilterCriteriaDto<?>> filters, Pageable pageable);
}