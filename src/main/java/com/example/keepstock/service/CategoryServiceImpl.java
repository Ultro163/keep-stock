package com.example.keepstock.service;

import com.example.keepstock.dto.category.CategoryDto;
import com.example.keepstock.dto.mappers.CategoryMapper;
import com.example.keepstock.entity.Category;
import com.example.keepstock.error.exception.EntityNotFoundException;
import com.example.keepstock.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto save(CategoryDto dto) {
        Category category = categoryMapper.toEntity(dto);
        category = categoryRepository.save(category);
        categoryRepository.flush();
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto update(CategoryDto dto) {
        Category category = categoryRepository.findById(dto.getId())
                .orElseThrow(() -> categoryNotFound(dto.getId()));
        category.setName(dto.getName());
        categoryRepository.flush();
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public void delete(UUID id) {
        checkCategoryExists(id);
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getById(UUID id) {
        return categoryMapper.toCategoryDto(categoryRepository.findById(id)
                .orElseThrow(() -> categoryNotFound(id)));
    }

    private void checkCategoryExists(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw categoryNotFound(id);
        }
    }

    private EntityNotFoundException categoryNotFound(UUID id) {
        return new EntityNotFoundException("Category with ID=%s not found".formatted(id));
    }
}