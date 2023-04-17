package ru.practicum.explorewithme.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.explorewithme.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public enum UserDTO {
    ;

    private interface Email {
        @Pattern(regexp = "[a-zA-Z_-][a-zA-Z0-9_.-]+@[a-zA-Z]+\\.[a-zA-Z]+")
        @NotNull
        String getEmail();
    }

    private interface Name {
        @NotNull
        @NotBlank
        String getName();
    }

    public enum Controller {
        ;

        public interface UserShortDtoI {
            Long getId();

            String getName();
        }

        @Data
        @Builder
        public static class UserShortDto implements UserShortDtoI {
            Long id;
            String name;
        }

        @Data
        @Builder
        public static class UserDto {   //for return
            Long id;
            String name;
            String email;
        }

        @Builder
        @Getter
        @Setter
        public static class UpdateUserRequest {
            String name;
            String email;

            public UpdateUserRequest(String name, String email) {
                this.email = email;
                this.name = name;
            }
        }

        @Getter
        @Setter
        public static class NewUserRequest extends UpdateUserRequest implements Email, Name {
            @Builder(builderMethodName = "newUserDTORequestBuilder")
            public NewUserRequest(String name, String email) {
                super(name, email);
            }
        }

        public static class Mapper {
            public static UserDto toUserDtoRequest(User user) {
                UserDto item = UserDto.builder()
                        .name(user.getName())
                        .id(user.getId())
                        .email(user.getEmail())
                        .build();

                return item;
            }

            public static User toUser(NewUserRequest userDTO) {
                User item = User.builder()
                        .id(null)
                        .name(userDTO.getName())
                        .email(userDTO.getEmail())
                        .build();

                return item;
            }

        }

    }

}
