/*
Преобразования в соответствии с частотой использования
event -> shortEvent -> fullEvent
**/
package ru.practicum.explorewithme.event.dto;

import io.micrometer.core.lang.Nullable;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.HttpStatus;
import ru.practicum.explorewithme.category.dto.CategoryDTO;
import ru.practicum.explorewithme.dto.StatDTO;
import ru.practicum.explorewithme.event.dto.constraints.DateConstrain;
import ru.practicum.explorewithme.event.dto.constraints.EnumStateActionEventConstrain;
import ru.practicum.explorewithme.event.dto.constraints.EnumStateEventConstrain;
import ru.practicum.explorewithme.event.dto.constraints.LocationConstrain;
import ru.practicum.explorewithme.event.model.Location;
import ru.practicum.explorewithme.event.model.Sort;
import ru.practicum.explorewithme.event.model.StateActionEvent;
import ru.practicum.explorewithme.exceptions.WrongEnumStatusException;
import ru.practicum.explorewithme.request.dto.EnumStatusEventConstrain;
import ru.practicum.explorewithme.user.dto.UserDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public enum EventDTO {
    ;

    private interface AnnotationI {
        @NotNull
        @Length(min = 20, max = 2000)
        String getAnnotation();
    }

    private interface CategoryI {
        @NotNull
        Long getCategory();
    }

    private interface DescriptionI {
        @NotNull
        @Length(min = 20, max = 7000)
        String getDescription();
    }

    private interface EventDateI {
        @NotNull
        @DateConstrain
        String getEventDate();
    }

    private interface LocationI {
        @Nullable
        @LocationConstrain()
        String getLocation();
    }

    private interface PaidI {
        @NotNull
        Boolean getPaid();
    }

    private interface ParticipantLimitI {
        @PositiveOrZero
        Integer getParticipantLimit();
    }

    private interface RequestModerationI {
        @NotNull
        Boolean getRequestModeration();
    }

    private interface StateActionUser {   //Состояние публикации события
        @Nullable
        @EnumStateActionEventConstrain(values = {StateActionEvent.SEND_TO_REVIEW, StateActionEvent.CANCEL_REVIEW})
        String getStateAction();
    }

    private interface StateActionAdmin {   //Состояние публикации события
        @Nullable
        @EnumStateActionEventConstrain(values = {StateActionEvent.REJECT_EVENT, StateActionEvent.PUBLISH_EVENT})
        String getStateAction();
    }

    private interface TitleI {
        @NotNull
        @Length(min = 3, max = 120)
        String getTitle();
    }

    private interface StateI {   //Состояние публикации события
        @NotNull
        @EnumStateEventConstrain
        String getState();
    }

    private interface StatusI {   //Статус события
        @NotNull
        @EnumStatusEventConstrain
        String getStatus();
    }

    public enum Controller {
        ;

        @Data
        @Builder
        public static class EventFullDto {
            private Long id;
            private String annotation;
            private CategoryDTO.Controller.CategoryDto category;
            private Long confirmedRequests;
            private String createdOn;   //"yyyy-MM-dd HH:mm:ss"
            private String description;
            private String eventDate;   //"yyyy-MM-dd HH:mm:ss"
            private UserDTO.Controller.UserShortDto initiator;
            private Location location;  //координаты
            private Boolean paid;   //платное ли участие
            private Integer participantLimit;
            private String publishedOn;
            private Boolean requestModeration;  //нужна ли модерация
            private String state;
            private String title;
            private Long views;
        }

        @Data
        @Builder
        @AllArgsConstructor
        public static class EventShortDto {
            private Long id;
            private String annotation;
            private CategoryDTO.Controller.CategoryDto category;
            private Long confirmedRequests;
            private String eventDate;   //"yyyy-MM-dd HH:mm:ss"
            private UserDTO.Controller.UserShortDto initiator;
            private Boolean paid;   //платное ли участие
            private String title;
            private Long views;

            public static class Comparator implements java.util.Comparator<EventShortDto> {
                private final Sort sort;

                public Comparator(Sort sort) {
                    this.sort = sort;
                }

                @Override
                public int compare(EventShortDto a1, EventShortDto a2) {
                    switch (sort) {
                        case EVENT_DATE:
                            if (LocalDateTime.parse(a1.eventDate, StatDTO.formatDateTime)
                                    .isBefore(LocalDateTime.parse(a2.eventDate, StatDTO.formatDateTime))) return 1;
                            return -1;
                        case VIEWS:
                            if (a1.views < a2.views) return 1;
                            return -1;
                        default:
                            throw new WrongEnumStatusException(HttpStatus.BAD_REQUEST, "wrong enum Sort parameter");
                    }
                }
            }
        }

        @Data
        @Builder
        public static class NewEventDto implements AnnotationI, CategoryI, DescriptionI, EventDateI, ParticipantLimitI {
            private String annotation;
            private Long category;
            private String description;
            private String eventDate;   //"yyyy-MM-dd HH:mm:ss"
            private Location location;  //координаты
            private boolean paid;   //платное ли участие
            private Integer participantLimit;
            private boolean requestModeration;  //нужна ли модерация
            private String title;
        }

        @Data
        @Builder
        public static class UpdateEventRequestBase {
            private String annotation;
            private Long category;
            private String description;
            private String eventDate;   //"yyyy-MM-dd HH:mm:ss"
            private Location location;  //координаты
            private Boolean paid;   //платное ли участие
            private Integer participantLimit;
            private Boolean requestModeration;  //нужна ли модерация
            private String stateAction;
            private String title;
        }

        @Getter
        @Setter
        public static class UpdateEventUserRequest extends UpdateEventRequestBase implements StateActionUser {
            @Builder(builderMethodName = "updateEventUserRequestBuilder")
            public UpdateEventUserRequest(String annotation,
                                          Long category,
                                          String description,
                                          String eventDate,
                                          Location location,
                                          Boolean paid,
                                          Integer participantLimit,
                                          Boolean requestModeration,
                                          String stateAction,
                                          String title) {
                super(annotation, category, description, eventDate,
                        location, paid, participantLimit, requestModeration,
                        stateAction, title);
            }
        }

        @Getter
        @Setter
        public static class UpdateEventAdminRequest extends UpdateEventRequestBase implements StateActionAdmin {
            @Builder(builderMethodName = "updateEventAdminRequestBuilder")
            public UpdateEventAdminRequest(String annotation,
                                           Long category,
                                           String description,
                                           String eventDate,
                                           Location location,
                                           Boolean paid,
                                           Integer participantLimit,
                                           Boolean requestModeration,
                                           String stateAction,
                                           String title) {
                super(annotation, category, description, eventDate,
                        location, paid, participantLimit, requestModeration,
                        stateAction, title);
            }
        }

    }

}
