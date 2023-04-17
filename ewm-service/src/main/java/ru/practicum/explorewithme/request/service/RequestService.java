package ru.practicum.explorewithme.request.service;

import ru.practicum.explorewithme.request.dto.RequestDTO;
import ru.practicum.explorewithme.request.model.Request;

import java.util.List;

public interface RequestService {
    Request findRequestById(Long id);

    List<RequestDTO.Controller.ParticipationRequestDto> findRequestsByUserId(Long userId, Integer from, Integer size);

    List<RequestDTO.Controller.ParticipationRequestDto> findRequestsForEventOfUser(Long userId, Long eventId);

    RequestDTO.Controller.ParticipationRequestDto save(Long userId, Long eventId);

    RequestDTO.Controller.EventRequestStatusUpdateResult patchRequestsForEventsOfUser(RequestDTO.Controller.EventRequestStatusUpdateRequest updateRequest,
                                                                                      Long userId,
                                                                                      Long eventId);

    RequestDTO.Controller.ParticipationRequestDto cancel(Long userId, Long requestId);
}
