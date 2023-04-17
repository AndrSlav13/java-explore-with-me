package ru.practicum.explorewithme.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.category.model.Category;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public enum CategoryDTO {
    ;

    private interface Name {
        @NotNull
        @NotBlank
        String getName();
    }

    public enum Controller {
        ;

        @Builder
        @Data
        public static class CategoryDto {
            Long id;
            String name;
        }

        @Builder
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NewCategoryDto implements Name {
            String name;
        }

        public static class Mapper {
            public static CategoryDto toCategoryDto(Category category) {
                CategoryDto item = CategoryDto.builder()
                        .name(category.getName())
                        .id(category.getId())
                        .build();

                return item;
            }

            public static Category toCategory(NewCategoryDto categoryDto) {
                Category item = Category.builder()
                        .id(null)
                        .name(categoryDto.getName())
                        .build();

                return item;
            }

        }

    }

}
