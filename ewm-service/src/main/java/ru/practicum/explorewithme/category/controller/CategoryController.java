package ru.practicum.explorewithme.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.category.dto.CategoryDTO;
import ru.practicum.explorewithme.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/categories")
    @ResponseBody
    public List<CategoryDTO.Controller.CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.findCategories(from, size);
    }

    @GetMapping("/categories/{id}")
    @ResponseBody
    public CategoryDTO.Controller.CategoryDto getCategoryById(@PathVariable Long id) {
        return categoryService.findCategoryByIdDTO(id);
    }

    @PatchMapping("/admin/categories/{id}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public CategoryDTO.Controller.CategoryDto patchCategoryById(@RequestBody @Valid CategoryDTO.Controller.NewCategoryDto categoryDTO, @PathVariable Long id) {
        return categoryService.patchCategoryById(categoryDTO, id);
    }

    @PostMapping("/admin/categories")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO.Controller.CategoryDto addCategory(@Valid @NotNull @RequestBody(required = false) CategoryDTO.Controller.NewCategoryDto categoryDTO) {
        return categoryService.save(categoryDTO);
    }

    @DeleteMapping(path = ("/admin/categories/{id}"))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable Long id) {
        categoryService.delete(id);
    }

}
