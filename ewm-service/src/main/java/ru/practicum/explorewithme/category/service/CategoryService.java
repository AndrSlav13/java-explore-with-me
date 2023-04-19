package ru.practicum.explorewithme.category.service;

import ru.practicum.explorewithme.category.dto.CategoryDTO;
import ru.practicum.explorewithme.category.model.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO.Controller.CategoryDto> findCategories(int from, int size);

    Category findCategoryById(Long id);

    CategoryDTO.Controller.CategoryDto findCategoryByIdDTO(Long id);

    CategoryDTO.Controller.CategoryDto save(CategoryDTO.Controller.NewCategoryDto categoryDTO);

    void delete(Long id);

    CategoryDTO.Controller.CategoryDto patchCategoryById(CategoryDTO.Controller.NewCategoryDto categoryDTO, Long id);

    List<CategoryDTO.Controller.CategoryDto> findCategoriesByIdInDTO(List<Long> ids);
}
