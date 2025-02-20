package com.example.keepstock.service;

import com.example.keepstock.dto.mappers.ProductMapper;
import com.example.keepstock.dto.product.ProductDto;
import com.example.keepstock.error.exception.EntityNotFoundException;
import com.example.keepstock.model.Product;
import com.example.keepstock.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDto save(ProductDto entity) {
        if (productRepository.existsByArticle(entity.getArticle())) {
            throw new IllegalStateException("Продукт с артикулом " + entity.getArticle() + " уже существует");
        }
        Product product = productMapper.toEntity(entity);
        productRepository.save(product);
        productRepository.flush();
        return productMapper.toProductDto(product);
    }

    @Override
    public ProductDto update(ProductDto dto) {
        Product product = productRepository.findById(dto.getId()).orElseThrow(() -> productNotFound(dto.getId()));
        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getCategory() != null) {
            product.getCategory().setId(dto.getCategory().getId());
        }
        if (dto.getQuantity() != null) {
            product.setQuantity(dto.getQuantity());
            product.setLastQuantityUpdate(OffsetDateTime.now());
        }
        return productMapper.toProductDto(product);
    }

    @Override
    public void delete(UUID id) {
        checkProductExists(id);
        productRepository.deleteById(id);
    }

    @Override
    public ProductDto getById(UUID id) {
        Product product = productRepository.findById(id).orElseThrow(() -> productNotFound(id));
        return productMapper.toProductDto(product);
    }

    private void checkProductExists(UUID id) {
        if (!productRepository.existsById(id)) {
            throw productNotFound(id);
        }
    }

    private EntityNotFoundException productNotFound(UUID id) {
        return new EntityNotFoundException("Product with ID=%s not found".formatted(id));
    }
}