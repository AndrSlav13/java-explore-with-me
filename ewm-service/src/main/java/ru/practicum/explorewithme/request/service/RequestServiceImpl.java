package ru.practicum.explorewithme.request.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.event.dto.EventDTO;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.StateEvent;
import ru.practicum.explorewithme.event.service.EventService;
import ru.practicum.explorewithme.exceptions.ApiErrorException;
import ru.practicum.explorewithme.request.dto.RequestDTO;
import ru.practicum.explorewithme.request.dto.RequestMapper;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.request.model.StatusEventParticipation;
import ru.practicum.explorewithme.request.repository.RequestRepository;
import ru.practicum.explorewithme.user.service.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;


    @Autowired
    public RequestServiceImpl(RequestRepository requestRepository, UserService userService, EventService eventService) {
        this.requestRepository = requestRepository;
        this.userService = userService;
        this.eventService = eventService;
    }

    @Override
    public Request findRequestById(Long id) {
        Request request = requestRepository.findById(id).orElseThrow(
                () -> new ApiErrorException(404, "request not found", "request id=" + id + " is absent")
        );
        return request;
    }

    @Override
    public List<RequestDTO.Controller.ParticipationRequestDto> findRequestsByUserId(Long userId, Integer from, Integer size) {
        Pageable pg = PageRequest.of(from, size);
        List<Request> requests = requestRepository.findAllByRequesterId(userId, pg);
        return requests.stream().map(a -> RequestMapper.toParticipationRequestDto(a)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public RequestDTO.Controller.ParticipationRequestDto save(Long userId, Long eventId) {
        Event event = eventService.findEventById(eventId);
        if (event.getInitiator().getId().equals(userId))
            throw new ApiErrorException(409, "request is rejected", "you have created the event");
        if (event.getState() != StateEvent.PUBLISHED)
            throw new ApiErrorException(409, "request is rejected", "the event isn't published yet");
        Long numPart = requestRepository.countAllByEventIdInAndStatusIn(List.of(eventId), List.of(StatusEventParticipation.CONFIRMED)).get(eventId);
        Integer numLimit = event.getParticipantLimit();
        if (numLimit != 0 && numLimit != null && numLimit <= numPart)
            throw new ApiErrorException(409, "request is rejected", "limit of participants is reached");
        return userService.addEventParticipationRequest(userId, eventId, event.getRequestModeration());
    }

    @Override
    public List<RequestDTO.Controller.ParticipationRequestDto> findRequestsForEventOfUser(Long userId, Long eventId) {
        eventService.findEventByInitiatorIdAndIdDTOFull(userId, eventId);  //Проверка наличия и возможности доступа
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(a -> RequestMapper.toParticipationRequestDto(a)).collect(Collectors.toList());
    }


    @Transactional
    @Override
    public RequestDTO.Controller.EventRequestStatusUpdateResult patchRequestsForEventsOfUser(RequestDTO.Controller.EventRequestStatusUpdateRequest updateRequest,
                                                                                             Long userId,
                                                                                             Long eventId) {
        EventDTO.Controller.EventFullDto eventFullDto = eventService.findEventByInitiatorIdAndIdDTOFull(userId, eventId);
        Integer numLimit = eventFullDto.getParticipantLimit();
        Long numPart = requestRepository.countAllByEventIdInAndStatusIn(List.of(eventId), List.of(StatusEventParticipation.CONFIRMED)).get(eventId);
        if (numPart >= numLimit && updateRequest.getStatus() == StatusEventParticipation.CONFIRMED.name())
            throw new ApiErrorException(409, "the event is not accessible", "limit of participants is reached");
        List<Request> requests = requestRepository.findAllByIdInAndStatusIn(updateRequest.getRequestIds(),
                Arrays.stream(StatusEventParticipation.values()).collect(Collectors.toList()));
        Integer limit = eventFullDto.getParticipantLimit();
        Map<Long, Long> curNum = requestRepository.countAllByEventIdInAndStatusIn(List.of(eventId),
                List.of(StatusEventParticipation.CONFIRMED));
        StatusEventParticipation status = StatusEventParticipation.valueOf(updateRequest.getStatus());
        int numChanged = 0;
        for (Request r : requests) {
            if (r.getStatus() == StatusEventParticipation.CONFIRMED && status == StatusEventParticipation.REJECTED ||
                    r.getStatus() == StatusEventParticipation.REJECTED && status == StatusEventParticipation.CONFIRMED)
                throw new ApiErrorException(409, "event status can't be changed", "status of the event id=" + r.getId() + " can't be changed");
            if ((limit != 0 && limit != null && curNum.get(eventId) >= limit) && status == StatusEventParticipation.CONFIRMED) {
                if (r.getStatus() == StatusEventParticipation.PENDING) {
                    if (numChanged != 0) break;
                    throw new ApiErrorException(409, "hit limit of participants", "requests were not confirmed");
                }
            }
            if (r.getStatus() == StatusEventParticipation.CONFIRMED && status == StatusEventParticipation.CONFIRMED ||
                    r.getStatus() == StatusEventParticipation.REJECTED && status == StatusEventParticipation.REJECTED)
                continue;
            ++numChanged;

            r.setStatus(status);
        }

        requestRepository.flush();
        requests = requestRepository.findAllByIdInAndStatusIn(updateRequest.getRequestIds(),
                List.of(StatusEventParticipation.CONFIRMED, StatusEventParticipation.REJECTED));
        List<RequestDTO.Controller.ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<RequestDTO.Controller.ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        requests.stream().forEach(a -> {
            if (a.getStatus() == StatusEventParticipation.CONFIRMED)
                confirmedRequests.add(RequestMapper.toParticipationRequestDto(a));
            else rejectedRequests.add(RequestMapper.toParticipationRequestDto(a));
        });
        return RequestDTO.Controller.EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    @Transactional
    @Override
    public RequestDTO.Controller.ParticipationRequestDto cancel(Long userId, Long requestId) {
        Request request = findRequestById(requestId);
        if (!request.getRequester().getId().equals(userId))
            throw new ApiErrorException(404, "wrong user authentication", "forbidden request id=" + requestId + " modification");
        request.setStatus(StatusEventParticipation.CANCELED);
        return RequestMapper.toParticipationRequestDto(request);
    }
}
