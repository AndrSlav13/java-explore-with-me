package ru.practicum.explorewithme.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public enum CategoryDTO {
    ;

    private interface NameI {
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
        public static class NewCategoryDto implements NameI {
            String name;
        }

    }

}
