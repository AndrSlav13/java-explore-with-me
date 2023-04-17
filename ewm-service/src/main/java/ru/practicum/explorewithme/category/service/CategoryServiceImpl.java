package ru.practicum.explorewithme.category.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.category.dto.CategoryDTO;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.category.repository.CategoryRepository;
import ru.practicum.explorewithme.exceptions.ApiErrorException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryDTO.Controller.CategoryDto> findCategories(int from, int size) {
        Pageable pg = PageRequest.of(from, size);
        return categoryRepository.findAll(pg).getContent().stream().map(a -> CategoryDTO.Controller.Mapper.toCategoryDto(a)).collect(Collectors.toList());
    }

    @Override
    public Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new ApiErrorException(HttpStatus.NOT_FOUND,
                        "Category with id=" + id + " was not found",
                        "The required object was not found.")
        );
    }

    @Override
    public CategoryDTO.Controller.CategoryDto findCategoryByIdDTO(Long id) {
        Category category = findCategoryById(id);
        return CategoryDTO.Controller.CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    @Transactional
    @Override
    public CategoryDTO.Controller.CategoryDto save(CategoryDTO.Controller.NewCategoryDto categoryDTO) {
        Category category = categoryRepository.save(Category.builder().id(null).name(categoryDTO.getName()).build());
        return CategoryDTO.Controller.Mapper.toCategoryDto(category);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Category category = findCategoryById(id);
        if (category.isAttachedToEvents())
            throw new ApiErrorException(HttpStatus.CONFLICT,
                    "Category with id=" + id + " is attached to events",
                    "The category can't be deleted.");
        Long numRemoved = categoryRepository.removeById(id);
        if (numRemoved != 1)
            throw new ApiErrorException(HttpStatus.NOT_FOUND,
                    "Category with id=" + id + " was not found",
                    "The required object was not found.");
    }

    @Transactional
    @Override
    public CategoryDTO.Controller.CategoryDto patchCategoryById(CategoryDTO.Controller.NewCategoryDto categoryDTO, Long id) {
        Category category = findCategoryById(id);
        if (categoryDTO != null && categoryDTO.getName() != null) category.setName(categoryDTO.getName());
        return CategoryDTO.Controller.Mapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDTO.Controller.CategoryDto> findCategoriesByIdInDTO(List<Long> ids) {
        return categoryRepository.findCategoriesByIdIn(ids).stream().map(a -> CategoryDTO.Controller.CategoryDto.builder()
                .name(a.getName())
                .id(a.getId())
                .build()).collect(Collectors.toList());
    }

}
