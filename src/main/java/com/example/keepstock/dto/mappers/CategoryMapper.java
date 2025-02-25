package com.example.keepstock.dto.mappers;

import com.example.keepstock.dto.category.CategoryDto;
import com.example.keepstock.dto.category.CategoryResponseDto;
import com.example.keepstock.dto.category.NewCategoryDto;
import com.example.keepstock.dto.category.NewCategoryRequest;
import com.example.keepstock.dto.category.UpdateCategoryDto;
import com.example.keepstock.dto.category.UpdateCategoryRequest;
import com.example.keepstock.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    Category toEntity(CategoryDto category);

    CategoryDto toCategoryDto(Category category);

    CategoryDto toCategoryDtoFromNewCategoryRequest(NewCategoryRequest category);

    NewCategoryDto toNewCategoryDtoFromCategoryDto(CategoryDto category);

    CategoryDto toCategoryDtoFromUpdateCategoryRequest(UpdateCategoryRequest updateCategoryRequest);

    UpdateCategoryDto toUpdateCategoryDtoFromCategoryDto(CategoryDto category);

    CategoryResponseDto toCategoryResponseDto(CategoryDto categoryDto);
}