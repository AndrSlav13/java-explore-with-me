package ru.practicum.explorewithme.compilation.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.event.dto.EventDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public enum CompilationDTO {
    ;

    private interface TitleI {
        @NotNull
        @NotBlank
        String getTitle();
    }

    private interface EventsI {
        @NotNull
        List<Long> getEvents();
    }

    public enum Controller {
        ;

        @Data
        @Builder
        public static class CompilationDto {
            private Long id;
            private Boolean pinned;
            private String title;
            private List<EventDTO.Controller.EventShortDto> events;
        }

        @Data
        @Builder
        public static class NewCompilationDto implements TitleI, EventsI {
            private Boolean pinned;
            private String title;
            private List<Long> events;
        }

        @Data
        @Builder
        public static class UpdateCompilationRequest {
            private Boolean pinned;
            private String title;
            private List<Long> events;
        }

    }

}
