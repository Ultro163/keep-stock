package com.example.keepstock.service;

import com.example.keepstock.dto.product.ProductDto;

import java.util.UUID;

public interface ProductService extends CrudService<ProductDto, UUID> {
}