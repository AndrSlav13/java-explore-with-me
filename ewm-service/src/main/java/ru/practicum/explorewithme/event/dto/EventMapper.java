package ru.practicum.explorewithme.event.dto;

import ru.practicum.explorewithme.category.dto.CategoryDTO;
import ru.practicum.explorewithme.dto.StatDTO;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.Location;
import ru.practicum.explorewithme.event.model.StateEvent;
import ru.practicum.explorewithme.user.dto.UserDTO;

import java.time.LocalDateTime;

public interface EventMapper {
    static EventDTO.Controller.EventShortDto toEventShortDto(Event event, CategoryDTO.Controller.CategoryDto categoryDto,
                                                             Long numConfirmedRequests,
                                                             UserDTO.Controller.UserShortDto initiatorDto,
                                                             Long numViews) {
        EventDTO.Controller.EventShortDto item = EventDTO.Controller.EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .confirmedRequests(numConfirmedRequests)
                .eventDate(event.getEventDate().format(StatDTO.formatDateTime))
                .category(categoryDto)
                .initiator(initiatorDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(numViews)
                .build();

        return item;
    }

    static EventDTO.Controller.EventFullDto toEventFullDto(EventDTO.Controller.EventShortDto event, Event e) {
        EventDTO.Controller.EventFullDto item = EventDTO.Controller.EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(event.getCategory())
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(e.getCreatedOn().format(StatDTO.formatDateTime))
                .description(e.getDescription())
                .eventDate(event.getEventDate())
                .initiator(event.getInitiator())
                .location(Location.toLocation(e.getLocation()))
                .paid(event.getPaid())
                .participantLimit(e.getParticipantLimit())
                .publishedOn(e.getState() == StateEvent.PUBLISHED ? e.getPublishedOn().format(StatDTO.formatDateTime) : null)
                .requestModeration(e.getRequestModeration())
                .state(e.getState().toString())
                .title(event.getTitle())
                .views(event.getViews())
                .build();

        return item;
    }

    static Event toEvent(EventDTO.Controller.NewEventDto eventDTO) {
        Event item = Event.builder()
                .id(null)
                .annotation(eventDTO.getAnnotation())
                .eventDate(LocalDateTime.parse(eventDTO.getEventDate(), StatDTO.formatDateTime))
                .paid(eventDTO.isPaid())
                .title(eventDTO.getTitle())
                .description(eventDTO.getDescription())
                .createdOn(LocalDateTime.parse(eventDTO.getEventDate(), StatDTO.formatDateTime))
                .state(StateEvent.PENDING)
                .location(eventDTO.getLocation().toString()) //Не jpa, поэтому String
                .participantLimit(eventDTO.getParticipantLimit())
                .requestModeration(eventDTO.isRequestModeration())
                .build();

        return item;
    }

    static EventDTO.Controller.EventFullDto toEventFullDto(Event event, CategoryDTO.Controller.CategoryDto categoryDto,
                                                           Long numConfirmedRequests,
                                                           UserDTO.Controller.UserShortDto initiatorDto,
                                                           Long numViews) {
        return EventDTO.Controller.EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(numConfirmedRequests)
                .createdOn(event.getCreatedOn().format(StatDTO.formatDateTime))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(StatDTO.formatDateTime))
                .initiator(initiatorDto)
                .location(Location.toLocation(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn().format(StatDTO.formatDateTime))
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .views(numViews)
                .build();
    }
}
