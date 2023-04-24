package ru.practicum.explorewithme.comment.dto;

import ru.practicum.explorewithme.comment.model.Comment;
import ru.practicum.explorewithme.comment.model.CommentDetails;
import ru.practicum.explorewithme.dto.StatDTO;
import ru.practicum.explorewithme.event.dto.EventDTO;
import ru.practicum.explorewithme.user.dto.UserDTO;
import ru.practicum.explorewithme.user.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public interface CommentMapper {
    static CommentDTO.Controller.CommentDto toCommentDto(Comment comment,
                                                         UserDTO.Controller.UserShortDto userDto,
                                                         List<CommentDTO.Controller.CommentDto> commentsDto,
                                                         EventDTO.Controller.EventForCommentDto eventDto) {
        CommentDTO.Controller.CommentDto item = CommentDTO.Controller.CommentDto.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .publishedOn(comment.getPublishedOn().format(StatDTO.formatDateTime))
                .childComments(commentsDto)
                .commenter(userDto)
                .event(eventDto)
                .build();

        return item;
    }

    static CommentDetailsDTO.CommentDetailsDto toCommentDetailsDto(CommentDetails commentDetails) {
        if(commentDetails == null) return null;
        CommentDetailsDTO.CommentDetailsDto commentDetailsDto = CommentDetailsDTO.CommentDetailsDto.builder()
                .stateComment(commentDetails.getStateComment().name())
                .date(commentDetails.getDate().format(StatDTO.formatDateTime))
                .description(commentDetails.getDescription())
                .build();

        return commentDetailsDto;
    }

    static CommentDTO.Controller.CommentAdminDto toCommentAdminDto(Comment comment, Optional<CommentDetails> commentDetails,
                                                              UserDTO.Controller.UserShortDto userDto,
                                                              EventDTO.Controller.EventForCommentDto eventDto) {
        CommentDetailsDTO.CommentDetailsDto commentDetailsDto = commentDetails.isPresent() ? CommentMapper.toCommentDetailsDto(commentDetails.get()) : null;
        CommentDTO.Controller.CommentAdminDto item = CommentDTO.Controller.CommentAdminDto.builder()
                .id(comment.getId())
                .message(comment.getMessage())
                .publishedOn(comment.getPublishedOn().format(StatDTO.formatDateTime))
                .commenter(userDto)
                .event(eventDto)
                .commentDetails(commentDetailsDto)
                .build();

        return item;
    }

    static Comment toComment(CommentDTO.Controller.NewCommentDto commentDto, User commenter, Comment parentComment) {
        Comment item = Comment.builder()
                .id(null)
                .parentComment(parentComment)
                .publishedOn(null)
                .commenter(commenter)
                .build();

        return item;
    }

}