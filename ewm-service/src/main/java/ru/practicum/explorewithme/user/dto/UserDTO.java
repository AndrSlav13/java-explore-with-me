package ru.practicum.explorewithme.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public enum UserDTO {
    ;

    private interface EmailI {
        @Email
        @NotEmpty
        String getEmail();
    }

    private interface NameI {
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

        @Getter
        @Setter
        public static class UserRequestBase implements EmailI, NameI {
            String name;
            String email;

            public UserRequestBase(String name, String email) {
                this.email = email;
                this.name = name;
            }
        }

        @Getter
        @Setter
        public static class NewUserRequest extends UserRequestBase {
            @Builder(builderMethodName = "newUserDTORequestBuilder")
            public NewUserRequest(String name, String email) {
                super(name, email);
            }
        }

        @Getter
        @Setter
        public static class UpdateUserRequest extends UserRequestBase {
            @Builder(builderMethodName = "updateUserDTORequestBuilder")
            public UpdateUserRequest(String name, String email) {
                super(name, email);
            }
        }

    }

}
