package ru.practicum.explorewithme.user.service;

import ru.practicum.explorewithme.request.dto.RequestDTO;
import ru.practicum.explorewithme.user.dto.UserDTO;
import ru.practicum.explorewithme.user.model.User;

import java.util.List;

public interface UserService {
    User findUserById(Long id);

    List<UserDTO.Controller.UserShortDto> findUsersByIdInDTO(List<Long> ids);

    List<UserDTO.Controller.UserDto> findUsersByIdInDTOFULL(List<Long> ids, int from, int size);

    UserDTO.Controller.UserDto save(UserDTO.Controller.NewUserRequest userDTO);

    void delete(Long id);

    RequestDTO.Controller.ParticipationRequestDto addEventParticipationRequest(Long userId, Long eventId, Boolean requestModeration);
}
