package ru.practicum.explorewithme.comment.service;

import ru.practicum.explorewithme.comment.dto.CommentDTO;
import ru.practicum.explorewithme.comment.model.Comment;

import java.util.List;

public interface CommentService {
    Comment findCommentById(Long id);
    List<CommentDTO.Controller.CommentDto> getCommentsUser(Long userId, Long eventId, Integer from, Integer size);

    CommentDTO.Controller.CommentDto addCommentToEventUser(CommentDTO.Controller.NewCommentDto commentDto, Long userId, Long eventId);
    CommentDTO.Controller.CommentDto addCommentToCommentUser(CommentDTO.Controller.NewCommentDto commentDto, Long userId, Long commentId);

    CommentDTO.Controller.CommentDto removeCommentUser(Long userId, Long commentId);
    CommentDTO.Controller.CommentDto patchCommentUser(CommentDTO.Controller.PatchCommentUserDto commentDto, Long userId, Long commentId);

    CommentDTO.Controller.CommentAdminDto getCommentAdmin(Long commentId);

    List<CommentDTO.Controller.CommentAdminDto> getCommentsAdmin(List<Long> eventId, List<Long> userId, List<String> stateComments, String rangeStart, String rangeEnd, Integer from, Integer size);

    CommentDTO.Controller.CommentAdminDto patchCommentAdmin(CommentDTO.Controller.PatchCommentAdminDto commentDto, Long commentId);

    CommentDTO.Controller.CommentAdminDto deleteCommentAdmin(Long commentId);

    List<CommentDTO.Controller.CommentDto> getCommentsPublic(List<Long> eventId, String text, String rangeStart, String rangeEnd, Integer from, Integer size);
}