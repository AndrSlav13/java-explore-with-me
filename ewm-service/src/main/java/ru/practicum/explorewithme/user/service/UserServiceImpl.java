package ru.practicum.explorewithme.user.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.repository.EventRepository;
import ru.practicum.explorewithme.exceptions.ApiErrorException;
import ru.practicum.explorewithme.request.dto.RequestDTO;
import ru.practicum.explorewithme.request.dto.RequestMapper;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.request.model.StatusEventParticipation;
import ru.practicum.explorewithme.request.repository.RequestRepository;
import ru.practicum.explorewithme.user.dto.UserDTO;
import ru.practicum.explorewithme.user.dto.UserMapper;
import ru.practicum.explorewithme.user.model.User;
import ru.practicum.explorewithme.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, EventRepository eventRepository, RequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public List<UserDTO.Controller.UserDto> findUsersByIdInDTOFULL(List<Long> ids, int from, int size) {
        Pageable pg = PageRequest.of(from, size);
        return userRepository.findUsersByIdIn(ids, pg).stream()
                .map(a -> UserMapper.toUserDtoRequest(a))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO.Controller.UserShortDto> findUsersByIdInDTO(List<Long> ids) {
        return userRepository.findUsersByIdIn(ids).stream().map(a -> UserDTO.Controller.UserShortDto.builder()
                .name(a.getName())
                .id(a.getId())
                .build()).collect(Collectors.toList());
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ApiErrorException(404, "user not found", "user id=" + id + " is absent")
        );
    }

    @Transactional
    @Override
    public UserDTO.Controller.UserDto save(UserDTO.Controller.NewUserRequest userDTO) {
        User usr = userRepository.save(UserMapper.toUser(userDTO).toBuilder().id(null).build());
        return UserMapper.toUserDtoRequest(usr);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        User user = findUserById(id);
        user.onRemoveEntity();
        userRepository.deleteById(id);
    }

    @Transactional
    @Override
    public RequestDTO.Controller.ParticipationRequestDto addEventParticipationRequest(Long userId, Long eventId, Boolean reuestModeration) {
        User user = findUserById(userId);
        Event event = eventRepository.findById(eventId).get();
        Request request = new Request(event, user);
        if (!reuestModeration) request.setStatus(StatusEventParticipation.CONFIRMED);
        user.addEventRequest(event, request);
        requestRepository.save(request);
        return RequestMapper.toParticipationRequestDto(request);
    }
}
