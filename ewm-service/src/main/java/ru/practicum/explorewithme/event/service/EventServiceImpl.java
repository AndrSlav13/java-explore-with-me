package ru.practicum.explorewithme.event.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.category.dto.CategoryDTO;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.category.service.CategoryService;
import ru.practicum.explorewithme.client.StatisticsClient;
import ru.practicum.explorewithme.dto.StatDTO;
import ru.practicum.explorewithme.event.dto.EventDTO;
import ru.practicum.explorewithme.event.dto.EventMapper;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.SortEvent;
import ru.practicum.explorewithme.event.model.StateActionEvent;
import ru.practicum.explorewithme.event.model.StateEvent;
import ru.practicum.explorewithme.event.repository.EventRepository;
import ru.practicum.explorewithme.exceptions.ApiErrorException;
import ru.practicum.explorewithme.request.model.StatusEventParticipation;
import ru.practicum.explorewithme.request.repository.RequestRepository;
import ru.practicum.explorewithme.user.dto.UserDTO;
import ru.practicum.explorewithme.user.model.User;
import ru.practicum.explorewithme.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Math.max;


@Service
@Log4j2
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final RequestRepository requestRepository;
    private final StatisticsClient statisticsClient;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, CategoryService categoryService, UserService userService,
                            RequestRepository requestRepository, StatisticsClient statisticsClient) {
        this.eventRepository = eventRepository;
        this.categoryService = categoryService;
        this.userService = userService;
        this.requestRepository = requestRepository;
        this.statisticsClient = statisticsClient;

    }

    @Override
    public EventDTO.Controller.EventFullDto findEventByIdDTOFull(Long id) {
        Event event = findEventById(id);
        EventDTO.Controller.EventShortDto eDto = findEventsByIdInDTO(List.of(id)).get(0);
        return EventMapper.toEventFullDto(eDto, event);
    }

    @Override
    public Event findEventById(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(
                () -> new ApiErrorException(404, "Event not found", "Event id=" + id + " is absent")
        );
        return event;
    }

    @Override
    public List<EventDTO.Controller.EventShortDto> findAllByInitiatorDTO(Long userId, Pageable pg) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pg);
        return getShortEventsDTO(events, SortEvent.EVENT_DATE.toString());
    }

    @Override
    public EventDTO.Controller.EventFullDto findEventByInitiatorIdAndIdDTOFull(Long userId, Long id) {
        Event event = eventRepository.findEventByInitiatorIdAndId(userId, id).orElseThrow(
                () -> new ApiErrorException(404, "Event not found", "Event id=" + id + " is absent")
        );
        EventDTO.Controller.EventShortDto eventShortDto = getShortEventsDTO(List.of(event), SortEvent.EVENT_DATE.toString()).get(0);
        return EventMapper.toEventFullDto(eventShortDto, event);
    }

    public Event findEventByInitiatorIdAndId(Long userId, Long id) {
        Event event = eventRepository.findEventByInitiatorIdAndId(userId, id).orElseThrow(
                () -> new ApiErrorException(404, "Event not found", "Event id=" + id + " is absent")
        );
        return event;
    }

    @Transactional
    @Override
    public Event addEvent(EventDTO.Controller.NewEventDto eventDto, Long userId) {
        if (LocalDateTime.parse(eventDto.getEventDate(), StatDTO.formatDateTime).isBefore(LocalDateTime.now()))
            throw new ApiErrorException(409, "event is rejected", "the event has already happened");
        Category category = categoryService.findCategoryById(eventDto.getCategory());
        User user = userService.findUserById(userId);
        Event event = EventMapper.toEvent(eventDto);
        event.setCategory(category);
        user.addEventInited(event);
        eventRepository.save(event);
        return event;
    }

    @Transactional
    @Override
    public EventDTO.Controller.EventFullDto patchEventByInitiatorIdAndEventId(EventDTO.Controller.UpdateEventUserRequest eventDto,
                                                                              Long userId,
                                                                              Long eventId) {
        Event event = findEventById(eventId);
        if (!event.getInitiator().getId().equals(userId))
            throw new ApiErrorException(409, "event moderation is rejected", "the user isn't owner");
        if (event.getState() == StateEvent.PUBLISHED)
            throw new ApiErrorException(409, "event moderation is rejected", "the event is published");

        if (eventDto.getAnnotation() != null) event.setAnnotation(eventDto.getAnnotation());
        if (eventDto.getCategory() != null && !eventDto.getCategory().equals(event.getCategory().getId()))
            event.setCategory(categoryService.findCategoryById(eventDto.getCategory()));
        if (eventDto.getDescription() != null) event.setDescription(eventDto.getDescription());
        if (eventDto.getEventDate() != null) {
            if (LocalDateTime.parse(eventDto.getEventDate(), StatDTO.formatDateTime).minusHours(2).isBefore(LocalDateTime.now()) ||
                    event.getEventDate().minusHours(2).isBefore(LocalDateTime.now())
            )
                throw new ApiErrorException(409, "event can't be modified", "event id=" + eventId + " can't be modified");
            event.setEventDate(LocalDateTime.parse(eventDto.getEventDate(), StatDTO.formatDateTime));
        }
        if (eventDto.getLocation() != null) event.setLocation(eventDto.getLocation().toString());
        if (eventDto.getPaid() != null) event.setPaid(eventDto.getPaid());
        if (eventDto.getParticipantLimit() != null) event.setParticipantLimit(eventDto.getParticipantLimit());
        if (eventDto.getRequestModeration() != null) event.setRequestModeration(eventDto.getRequestModeration());
        if (eventDto.getStateAction() != null) {
            if (event.getState() == StateEvent.PUBLISHED)
                throw new ApiErrorException(409, "event is published", "event id=" + eventId + " can't be modified");
            if (eventDto.getStateAction().equals(StateActionEvent.CANCEL_REVIEW.name()))
                event.setState(StateEvent.CANCELED);
            if (eventDto.getStateAction().equals(StateActionEvent.SEND_TO_REVIEW.name()))
                event.setState(StateEvent.PENDING);
        }
        if (eventDto.getTitle() != null) event.setTitle(eventDto.getTitle());
        eventRepository.save(event);

        return findEventByInitiatorIdAndIdDTOFull(userId, eventId);
    }

    @Override
    public List<EventDTO.Controller.EventFullDto> findEventsAdmin(List<Long> users,
                                                                  List<String> states,
                                                                  List<Long> categories,
                                                                  String rangeStart,
                                                                  String rangeEnd,
                                                                  Integer from,
                                                                  Integer size) {
        Pageable pg = PageRequest.of(from, size);
        List<Event> events = eventRepository.getEventsAdmin(users, states == null ? null : states.stream().map(a -> StateEvent.valueOf(a)).collect(Collectors.toList()), categories,
                rangeStart == null ? null : LocalDateTime.parse(rangeStart, StatDTO.formatDateTime), rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, StatDTO.formatDateTime), pg);
        Map<Long, Event> mapEvent = events.stream().collect(Collectors.toMap(a -> a.getId(), a -> a, (a, b) -> a));
        List<EventDTO.Controller.EventShortDto> eventsDto = getShortEventsDTO(events, SortEvent.EVENT_DATE.toString());
        return eventsDto.stream().map(a -> EventMapper.toEventFullDto(a, mapEvent.get(a.getId()))).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventDTO.Controller.EventFullDto patchEventByEventIdAdmin(EventDTO.Controller.UpdateEventAdminRequest eventDto,
                                                                     Long eventId) {
        Event event = findEventById(eventId);

        if (eventDto.getAnnotation() != null) event.setAnnotation(eventDto.getAnnotation());
        if (eventDto.getCategory() != null && !eventDto.getCategory().equals(event.getCategory().getId()))
            event.setCategory(categoryService.findCategoryById(eventDto.getCategory()));
        if (eventDto.getDescription() != null) event.setDescription(eventDto.getDescription());
        if (eventDto.getEventDate() != null) {
            if (LocalDateTime.parse(eventDto.getEventDate(),
                    StatDTO.formatDateTime).minusHours(1).isBefore(LocalDateTime.now()) ||
                    event.getEventDate().minusHours(1).isBefore(LocalDateTime.now())
            )
                throw new ApiErrorException(409, "event can't be modified", "event id=" + eventId + " can't be modified");

            event.setEventDate(LocalDateTime.parse(eventDto.getEventDate(), StatDTO.formatDateTime));
        }
        if (eventDto.getLocation() != null) event.setLocation(eventDto.getLocation().toString());
        if (eventDto.getPaid() != null) event.setPaid(eventDto.getPaid());
        if (eventDto.getParticipantLimit() != null) event.setParticipantLimit(eventDto.getParticipantLimit());
        if (eventDto.getRequestModeration() != null) event.setRequestModeration(eventDto.getRequestModeration());
        if (eventDto.getStateAction() != null) {
            if (event.getState() != StateEvent.PENDING &&
                    (eventDto.getStateAction().equals(StateActionEvent.PUBLISH_EVENT.name()) || eventDto.getStateAction().equals(StateActionEvent.REJECT_EVENT.name())))
                throw new ApiErrorException(409, "the event can't be modified", "event id=" + eventId + " can't be modified");

            if (eventDto.getStateAction().equals(StateActionEvent.PUBLISH_EVENT.name())) {
                event.setState(StateEvent.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
            if (eventDto.getStateAction().equals(StateActionEvent.REJECT_EVENT.name()))
                event.setState(StateEvent.CANCELED);
        }
        if (eventDto.getTitle() != null) event.setTitle(eventDto.getTitle());
        eventRepository.save(event);

        return findEventByInitiatorIdAndIdDTOFull(event.getInitiator().getId(), eventId);
    }


    @Override
    public List<EventDTO.Controller.EventShortDto> findEventsPublic(String text,
                                                                    List<Long> categories,
                                                                    Boolean paid,
                                                                    String rangeStart,
                                                                    String rangeEnd,
                                                                    Boolean onlyAvailable,
                                                                    String sort,
                                                                    Integer from,
                                                                    Integer size) {
        Pageable pg = PageRequest.of(from, size);
        List<Event> ee = eventRepository.findAll();
        List<Event> events = eventRepository.getEventsPublic(text, categories, paid,
                rangeStart == null ? null : LocalDateTime.parse(rangeStart, StatDTO.formatDateTime),
                rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, StatDTO.formatDateTime), onlyAvailable, pg);
        return getShortEventsDTO(events, sort);
    }

    @Override
    public List<EventDTO.Controller.EventShortDto> findEventsByIdInDTO(List<Long> ids) {
        Pageable pg = PageRequest.of(0, max(ids.size(), 1));
        List<Event> events = eventRepository.findAllByIdIn(ids);
        return getShortEventsDTO(events, SortEvent.EVENT_DATE.toString());
    }

    @Override
    public List<Event> findEventsByIdIn(List<Long> ids) {
        Pageable pg = PageRequest.of(0, max(ids.size(), 1));
        List<Event> events = eventRepository.findAllByIdIn(ids);
        return events;
    }


    //Преобразование Event в ShortEvent (самое частое)
    private List<EventDTO.Controller.EventShortDto> getShortEventsDTO(List<Event> events,
                                                                      String sort) {
        if (events.isEmpty()) return List.of();
        //Загрузка данных в jpa чтобы потом не грузить по-отдельности
        Map<Long, CategoryDTO.Controller.CategoryDto> mapCategory = categoryService.findCategoriesByIdInDTO(events.stream().map(a -> a.getCategory().getId()).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(a -> a.getId(), a -> a, (a, b) -> a));    //categoryId->categoryDTO
        List<UserDTO.Controller.UserShortDto> uuu = userService.findUsersByIdInDTO(events.stream().map(a -> a.getInitiator().getId()).collect(Collectors.toList()));
        Map<Long, UserDTO.Controller.UserShortDto> mapInitiator = userService.findUsersByIdInDTO(events.stream().map(a -> a.getInitiator().getId()).collect(Collectors.toList()))
                .stream().collect(Collectors.toMap(a -> a.getId(), a -> a, (a, b) -> a));    //userId->userDTO
        Map<Long, Long> mapNumConfirmed = requestRepository.countAllByEventIdInAndStatusIn(events.stream().map(a -> a.getId()).collect(Collectors.toList()), List.of(StatusEventParticipation.CONFIRMED));   //eventId->numRequests
        List<String> uris = events.stream().map(w -> "events/" + w.getId()).collect(Collectors.toList());

        ResponseEntity<Object> responseBody = statisticsClient.getRecords(0L, LocalDateTime.now().minusYears(1000), LocalDateTime.now().plusYears(1000),
                uris, true, 0, events.size() + 1);

        List<StatDTO.ReturnStatDTO> viewStatsDtos = new ObjectMapper().convertValue(responseBody.getBody(), new TypeReference<>() {
        });
        Map<Long, Long> eventViews = viewStatsDtos.stream().collect(Collectors.toMap(
                w -> Long.valueOf(w.getUri().replaceAll("[^0-9]", "")), w -> w.getHits(), (q, w) -> q + w
        ));     //eventId->numHits


        return events.stream().map(a ->
                EventMapper.toEventShortDto(
                        a, mapCategory.get(a.getCategory().getId()),
                        mapNumConfirmed.get(a.getId()) == null ? 0 : mapNumConfirmed.get(a.getId()),
                        mapInitiator.get(a.getInitiator().getId()),
                        eventViews.get(a.getId())
                )
        ).sorted(new EventDTO.Controller.EventShortDto.Comparator(SortEvent.valueOf(sort))).collect(Collectors.toList());
    }
}

