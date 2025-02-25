package com.example.keepstock.controller;

import com.example.keepstock.dto.category.CategoryDto;
import com.example.keepstock.dto.category.CategoryResponseDto;
import com.example.keepstock.dto.category.NewCategoryDto;
import com.example.keepstock.dto.category.NewCategoryRequest;
import com.example.keepstock.dto.category.UpdateCategoryDto;
import com.example.keepstock.dto.category.UpdateCategoryRequest;
import com.example.keepstock.dto.mappers.CategoryMapper;
import com.example.keepstock.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewCategoryDto create(@RequestBody @Valid NewCategoryRequest newCategoryRequest) {
        CategoryDto dto = categoryMapper.toCategoryDtoFromNewCategoryRequest(newCategoryRequest);
        return categoryMapper.toNewCategoryDtoFromCategoryDto(categoryService.save(dto));
    }

    @PatchMapping("/{id}")
    public UpdateCategoryDto update(@RequestBody @Valid UpdateCategoryRequest updateCategoryRequest,
                                    @PathVariable UUID id) {
        CategoryDto dto = categoryMapper.toCategoryDtoFromUpdateCategoryRequest(updateCategoryRequest);
        dto.setId(id);
        return categoryMapper.toUpdateCategoryDtoFromCategoryDto(categoryService.update(dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        categoryService.delete(id);
    }

    @GetMapping("/{id}")
    public CategoryResponseDto getById(@PathVariable UUID id) {
        return categoryMapper.toCategoryResponseDto(categoryService.getById(id));
    }
}