package ru.practicum.explorewithme.comment.dto;

import lombok.Builder;
import lombok.Data;

public class CommentDetailsDTO {
    @Data
    @Builder
    public static class CommentDetailsDto {
        private String description;
        private String stateComment;
        private String date;
    }

}
