package ru.practicum.explorewithme.request.dto;

import io.micrometer.core.lang.Nullable;
import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.request.model.Request;

import java.time.LocalDateTime;
import java.util.List;

public enum RequestDTO {
    ;

    private interface StatusI {   //Состояние публикации события
        @Nullable
        @EnumStatusEventConstrain
        String getStatus();
    }

    public enum Controller {
        ;

        @Data
        @Builder
        //Заявка на участие в событии
        public static class ParticipationRequestDto {
            private LocalDateTime created;
            private Long event;
            private Long id;
            private Long requester;
            private String status;
        }

        @Data
        @Builder
        public static class EventRequestStatusUpdateRequest {
            private List<Long> requestIds;
            private String status;
        }

        @Data
        @Builder
        public static class EventRequestStatusUpdateResult {
            private List<RequestDTO.Controller.ParticipationRequestDto> confirmedRequests;
            private List<RequestDTO.Controller.ParticipationRequestDto> rejectedRequests;
        }

        public static class Mapper {
            public static ParticipationRequestDto toParticipationRequestDto(Request request) {
                ParticipationRequestDto item = ParticipationRequestDto.builder()
                        .id(request.getId())
                        .created(request.getCreated())
                        .event(request.getEvent().getId())
                        .requester(request.getRequester().getId())
                        .status(request.getStatus().name())
                        .build();
                return item;
            }

        }

    }

}
