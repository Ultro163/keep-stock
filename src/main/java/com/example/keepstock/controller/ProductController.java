package com.example.keepstock.controller;

import com.example.keepstock.dto.mappers.ProductMapper;
import com.example.keepstock.dto.product.NewProductDto;
import com.example.keepstock.dto.product.NewProductRequest;
import com.example.keepstock.dto.product.ProductDto;
import com.example.keepstock.dto.product.ProductFilterDto;
import com.example.keepstock.dto.product.ProductResponseDto;
import com.example.keepstock.dto.product.UpdateProductDto;
import com.example.keepstock.dto.product.UpdateProductRequest;
import com.example.keepstock.dto.product.criteria.FilterCriteriaDto;
import com.example.keepstock.service.image.ProductImageService;
import com.example.keepstock.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ProductImageService productImageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewProductDto create(@RequestBody @Valid NewProductRequest newProductRequest) {
        ProductDto dto = productMapper.toProductDtoFromNewProductRequest(newProductRequest);
        return productMapper.toNewProductDto(productService.save(dto));
    }

    @PatchMapping("/{id}")
    public UpdateProductDto update(@PathVariable UUID id, @RequestBody @Valid UpdateProductRequest updateProductRequest) {
        ProductDto dto = productMapper.toProductDtoFromUpdateProductRequest(updateProductRequest);
        dto.setId(id);
        return productMapper.toUpdateProductDto(productService.update(dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        productService.delete(id);
    }

    @GetMapping("/{id}")
    public ProductResponseDto getById(@PathVariable UUID id) {
        return productMapper.toProductResponseDto(productService.getById(id));
    }

    @GetMapping("/search")
    public List<ProductResponseDto> getAllProductsByFilters(@Valid ProductFilterDto filter) {
        return productService.findAllProductsByFilters(filter).stream()
                .map(productMapper::toProductResponseDto).toList();
    }

    @GetMapping("/search/multiple")
    public List<ProductResponseDto> findProductsByMultipleFilters(@RequestBody List<FilterCriteriaDto<?>> filters,
                                                                  Pageable pageable) {

        return productService.findProductsByMultipleFilters(filters, pageable).stream()
                .map(productMapper::toProductResponseDto).toList();
    }

    @PostMapping("/{productId}/upload")
    public ResponseEntity<String> uploadImageForProduct(@PathVariable UUID productId,
                                                        @RequestParam("file") MultipartFile file) {
        String response = productImageService.saveProductImage(productId, file);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}/download")
    public ResponseEntity<byte[]> downloadProductImages(@PathVariable UUID productId) {
        byte[] zipFile = productImageService.getProductImages(productId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=product_images.zip");
        headers.add("Content-Type", "application/zip");

        return new ResponseEntity<>(zipFile, headers, HttpStatus.OK);
    }
}