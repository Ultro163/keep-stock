package com.example.keepstock.service.product;

import com.example.keepstock.common.CurrencyProvider;
import com.example.keepstock.dto.mappers.ProductMapper;
import com.example.keepstock.dto.product.ProductDto;
import com.example.keepstock.dto.product.ProductFilterDto;
import com.example.keepstock.dto.product.criteria.FilterCriteriaDto;
import com.example.keepstock.entity.Product;
import com.example.keepstock.error.exception.EntityNotFoundException;
import com.example.keepstock.model.Currency;
import com.example.keepstock.repository.ProductRepository;
import com.example.keepstock.service.currency.CurrencyService;
import com.example.keepstock.service.search.ProductSpecification;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CurrencyProvider currencyProvider;
    private final CurrencyService currencyService;

    @Override
    public ProductDto save(ProductDto entity) {
        if (productRepository.existsByArticle(entity.getArticle())) {
            throw new IllegalStateException("Продукт с артикулом " + entity.getArticle() + " уже существует");
        }
        Product product = productMapper.toEntity(entity);
        if (product.getQuantity() != 0) {
            product.setIsAvailable(true);
        }
        productRepository.save(product);
        productRepository.flush();
        return productMapper.toProductDto(product);
    }

    @Override
    public ProductDto update(ProductDto dto) {
        Product product = productRepository.findById(dto.getId()).orElseThrow(() -> productNotFound(dto.getId()));
        if (dto.getName() != null && !dto.getName().isBlank()) {
            product.setName(dto.getName().trim());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            product.setDescription(dto.getDescription().trim());
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
            if (product.getQuantity() != 0) {
                product.setIsAvailable(true);
            }
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
        ProductDto dto = productMapper.toProductDto(product);
        return setCurrencyAndCalculatePrice(dto);
    }

    @Override
    public List<ProductDto> findAllProductsByFilters(final ProductFilterDto filter) {
        final PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        final Specification<Product> specification = (root, query, criteriaBuilder) -> {
            final List<Predicate> predicates = new ArrayList<>();
            if (filter.getName() != null) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + filter.getName() + "%"));
            }
            if (filter.getQuantity() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("quantity"), filter.getQuantity()));
            }
            if (filter.getPrice() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), filter.getPrice()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        final List<Product> products = productRepository.findAll(specification, pageRequest).getContent();
        return products.stream()
                .map(productMapper::toProductDto)
                .map(this::setCurrencyAndCalculatePrice).toList();
    }

    @Override
    public List<ProductDto> findProductsByMultipleFilters(List<FilterCriteriaDto<?>> filters, Pageable pageable) {
        Specification<Product> spec = new ProductSpecification(filters);
        List<Product> products = productRepository.findAll(spec, pageable).getContent();
        return products.stream()
                .map(productMapper::toProductDto)
                .map(this::setCurrencyAndCalculatePrice).toList();
    }

    private ProductDto setCurrencyAndCalculatePrice(ProductDto dto) {
        Currency currency = currencyProvider.getCurrency();
        BigDecimal exchangeRate = currencyService.getExchangeRate(currency);

        dto.setCurrency(currency);
        dto.setPrice(dto.getPrice().divide(exchangeRate, 2, RoundingMode.HALF_UP));
        return dto;
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