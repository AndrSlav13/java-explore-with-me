package ru.practicum.explorewithme.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.event.dto.EventDTO;
import ru.practicum.explorewithme.event.model.Event;

import java.util.List;

public interface EventService {
    Event findEventById(Long id);

    List<Event> findEventsByIdIn(List<Long> ids);

    List<EventDTO.Controller.EventShortDto> findEventsByIdInDTO(List<Long> ids);

    List<EventDTO.Controller.EventShortDto> findAllByInitiatorDTO(Long userId, Pageable pg);

    EventDTO.Controller.EventFullDto findEventByIdDTOFull(Long id);

    EventDTO.Controller.EventFullDto findEventByInitiatorIdAndIdDTOFull(Long userId, Long id);

    Event addEvent(EventDTO.Controller.NewEventDto event, Long userId);

    EventDTO.Controller.EventFullDto patchEventByInitiatorIdAndEventId(EventDTO.Controller.UpdateEventUserRequest event,
                                                                       Long userId,
                                                                       Long eventId);

    List<EventDTO.Controller.EventFullDto> findEventsAdmin(List<Long> users,
                                                           List<String> states,
                                                           List<Long> categories,
                                                           String rangeStart,
                                                           String rangeEnd,
                                                           Integer from,
                                                           Integer size);

    EventDTO.Controller.EventFullDto patchEventByEventIdAdmin(EventDTO.Controller.UpdateEventAdminRequest event,
                                                              Long eventId);

    List<EventDTO.Controller.EventShortDto> findEventsPublic(String text,
                                                             List<Long> categories,
                                                             Boolean paid,
                                                             String rangeStart,
                                                             String rangeEnd,
                                                             Boolean onlyAvailable,
                                                             String sort,
                                                             Integer from,
                                                             Integer size);

}
