/*
Преобразования в соответствии с частотой использования
event -> shortEvent -> fullEvent
**/
package ru.practicum.explorewithme.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.practicum.explorewithme.dto.StatDTO;
import ru.practicum.explorewithme.event.dto.EventDTO;
import ru.practicum.explorewithme.user.dto.UserDTO;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public enum CommentDTO {
    ;

    private interface MessageI {
        @NotNull
        @Length(min = 5, max = 2000)
        String getMessage();
    }

    public enum Controller {
        ;

        @Data
        @Builder(toBuilder = true)
        public static class CommentDto {
            private Long id;
            private EventDTO.Controller.EventForCommentDto event;
            private String message;
            private UserDTO.Controller.UserShortDto commenter;
            private String publishedOn;   //"yyyy-MM-dd HH:mm:ss"
            private List<CommentDto> childComments;

            public static class Comparator {

                public static int compare(CommentDto a1, CommentDto a2) {
                    if (LocalDateTime.parse(a1.publishedOn, StatDTO.formatDateTime)
                            .isBefore(LocalDateTime.parse(a2.publishedOn, StatDTO.formatDateTime))) return 1;
                    return -1;
                }
            }
        }


        @Data
        @Builder(toBuilder = true)
        public static class CommentAdminDto {
            private Long id;
            private Long parentId;
            private EventDTO.Controller.EventForCommentDto event;
            private String message;
            private UserDTO.Controller.UserShortDto commenter;
            private String publishedOn;   //"yyyy-MM-dd HH:mm:ss"
            private CommentDetailsDTO.CommentDetailsDto commentDetails;

            public static class Comparator {

                public static int compare(CommentAdminDto a1, CommentAdminDto a2) {
                    if (LocalDateTime.parse(a1.publishedOn, StatDTO.formatDateTime)
                            .isBefore(LocalDateTime.parse(a2.publishedOn, StatDTO.formatDateTime))) return 1;
                    return -1;
                }
            }

        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class NewCommentDto implements MessageI {
            private String message;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class PatchCommentAdminDto implements MessageI {
            private String message;
            private String state;
            private String description;
        }

        @Data
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class PatchCommentUserDto {
            private String state;
            private String description;
        }

    }

}
