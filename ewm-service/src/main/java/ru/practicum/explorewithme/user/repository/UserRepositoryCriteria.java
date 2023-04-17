package ru.practicum.explorewithme.user.repository;

import ru.practicum.explorewithme.request.dto.RequestDTO;

public interface UserRepositoryCriteria {
    RequestDTO.Controller.ParticipationRequestDto save(Long userId, Long eventId);
}
