package ru.practicum.explorewithme.user.dto;

import ru.practicum.explorewithme.user.model.User;

public interface UserMapper {
    static UserDTO.Controller.UserDto toUserDtoRequest(User user) {
        UserDTO.Controller.UserDto item = UserDTO.Controller.UserDto.builder()
                .name(user.getName())
                .id(user.getId())
                .email(user.getEmail())
                .build();

        return item;
    }

    static User toUser(UserDTO.Controller.NewUserRequest userDTO) {
        User item = User.builder()
                .id(null)
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .build();

        return item;
    }
}
