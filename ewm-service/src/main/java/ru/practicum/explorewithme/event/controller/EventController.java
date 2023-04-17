package ru.practicum.explorewithme.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.event.dto.EventDTO;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.service.EventService;
import ru.practicum.explorewithme.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Validated
public class EventController {
    private final EventService eventService;
    private final UserService userService;

    @GetMapping(path = "/users/{userId}/events")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public List<EventDTO.Controller.EventShortDto> getEventsByInitiatorId(@PathVariable Long userId,
                                                                          @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                          @Positive @RequestParam(defaultValue = "10") Integer size) {
        Pageable pg = PageRequest.of(from, size);
        return eventService.findAllByInitiatorDTO(userId, pg);
    }

    @GetMapping(path = "/users/{userId}/events/{eventId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public EventDTO.Controller.EventFullDto getEventByInitiatorIdAndEventId(@PathVariable Long userId,
                                                                            @PathVariable Long eventId) {
        return eventService.findEventByInitiatorIdAndIdDTOFull(userId, eventId);
    }

    @PostMapping(path = "/users/{userId}/events")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventDTO.Controller.EventFullDto addEvent(@PathVariable Long userId, @Valid @NotNull @RequestBody(required = false) EventDTO.Controller.NewEventDto eventDto) {
        Event event = eventService.addEvent(eventDto, userId);
        return eventService.findEventByIdDTOFull(event.getId());
    }

    @PatchMapping(path = "/users/{userId}/events/{eventId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public EventDTO.Controller.EventFullDto patchEventByInitiatorIdAndEventId(@RequestBody EventDTO.Controller.UpdateEventUserRequest event,
                                                                              @PathVariable Long userId,
                                                                              @PathVariable Long eventId) {
        return eventService.patchEventByInitiatorIdAndEventId(event, userId, eventId);
    }

    @GetMapping(path = "/admin/events")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public List<EventDTO.Controller.EventFullDto> getEventsAdmin(@RequestParam(required = false) List<Long> users,
                                                                 @RequestParam(required = false) List<String> states,
                                                                 @RequestParam(required = false) List<Long> categories,
                                                                 @RequestParam(required = false) String rangeStart,
                                                                 @RequestParam(required = false) String rangeEnd,
                                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        return eventService.findEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping(path = "/admin/events/{eventId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public EventDTO.Controller.EventFullDto patchEventByEventIdAdmin(@RequestBody @Valid EventDTO.Controller.UpdateEventAdminRequest event,
                                                                     @PathVariable Long eventId) {
        return eventService.patchEventByEventIdAdmin(event, eventId);
    }

    @GetMapping(path = "/events")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public List<EventDTO.Controller.EventShortDto> getEventsPublic(HttpServletRequest servletRequest,
                                                                   @RequestParam(required = false) String text,
                                                                   @RequestParam(required = false) List<Long> categories,
                                                                   @RequestParam(required = false) Boolean paid,
                                                                   @RequestParam(required = false) String rangeStart,
                                                                   @RequestParam(required = false) String rangeEnd,
                                                                   @RequestParam(required = false) Boolean onlyAvailable,
                                                                   @RequestParam(defaultValue = "VIEWS") String sort,
                                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        return eventService.findEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping(path = "/events/{eventId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public EventDTO.Controller.EventFullDto getEventByEventIdPublic(HttpServletRequest servlet, @PathVariable Long eventId) {
        return eventService.findEventByIdDTOFull(eventId);
    }
}
