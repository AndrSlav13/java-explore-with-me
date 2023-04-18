package ru.practicum.explorewithme.category.dto;

import ru.practicum.explorewithme.category.model.Category;

public interface CategoryMapper {
    static CategoryDTO.Controller.CategoryDto toCategoryDto(Category category) {
        CategoryDTO.Controller.CategoryDto item = CategoryDTO.Controller.CategoryDto.builder()
                .name(category.getName())
                .id(category.getId())
                .build();

        return item;
    }

    static Category toCategory(CategoryDTO.Controller.NewCategoryDto categoryDto) {
        Category item = Category.builder()
                .id(null)
                .name(categoryDto.getName())
                .build();

        return item;
    }
}
