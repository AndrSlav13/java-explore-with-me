package ru.practicum.explorewithme.request.dto;

import ru.practicum.explorewithme.request.model.Request;

public interface RequestMapper {
    static RequestDTO.Controller.ParticipationRequestDto toParticipationRequestDto(Request request) {
        RequestDTO.Controller.ParticipationRequestDto item = RequestDTO.Controller.ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus().name())
                .build();
        return item;
    }
}
