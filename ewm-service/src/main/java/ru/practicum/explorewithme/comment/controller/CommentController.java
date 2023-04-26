package ru.practicum.explorewithme.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.comment.dto.CommentDTO;
import ru.practicum.explorewithme.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Validated
public class CommentController {
    private final CommentService commentService;

    //Добавление комментария от юзера userId к событию eventId
    @PostMapping(path = "/users/{userId}/events/{eventId}/comments")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentDTO.Controller.CommentDto addCommentForEventUser(@PathVariable Long userId,
                                                                   @PathVariable Long eventId,
                                                                   @Valid @NotNull @RequestBody CommentDTO.Controller.NewCommentDto commentDto) {
        return commentService.addCommentToEventUser(commentDto, userId, eventId);
    }

    //Добавление комментария от юзера userId к комментарию commentId
    @PostMapping(path = "/users/{userId}/events/comments/{commentId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentDTO.Controller.CommentDto addCommentForCommentUser(@PathVariable Long userId,
                                                                     @PathVariable Long commentId,
                                                                     @Valid @NotNull @RequestBody CommentDTO.Controller.NewCommentDto commentDto) {
        return commentService.addCommentToCommentUser(commentDto, userId, commentId);
    }

    //Получить комментарии к событию
    @GetMapping(path = "/users/{userId}/events/{eventId}/comments")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentDTO.Controller.CommentDto> getCommentsUser(@PathVariable Long userId,
                                                                  @PathVariable Long eventId,
                                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentsUser(userId, eventId, from, size);
    }

    //Удалить комментарий
    @DeleteMapping(path = "/users/{userId}/events/comments/{commentId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public CommentDTO.Controller.CommentDto removeCommentUser(@PathVariable Long userId,
                                                              @PathVariable Long commentId) {
        return commentService.removeCommentUser(userId, commentId);
    }

    //Отправить запрос на правку комментария
    @PatchMapping(path = "/users/{userId}/events/comments/{commentId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public CommentDTO.Controller.CommentDto patchCommentUser(@RequestBody @Valid CommentDTO.Controller.PatchCommentUserDto commentDto,
                                                             @PathVariable Long userId,
                                                             @PathVariable Long commentId) {
        return commentService.patchCommentUser(commentDto, userId, commentId);
    }


    @GetMapping(path = "/admin/events/comments/{commentId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public CommentDTO.Controller.CommentAdminDto getCommentAdmin(@PathVariable Long commentId) {
        return commentService.getCommentAdmin(commentId);
    }

    @GetMapping(path = "/admin/events/comments")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentDTO.Controller.CommentAdminDto> getCommentsAdmin(@RequestParam(required = false) List<Long> eventId,
                                                                        @RequestParam(required = false) List<Long> userId,
                                                                        @RequestParam(required = false) List<String> stateComments,
                                                                        @RequestParam(required = false) String rangeStart,
                                                                        @RequestParam(required = false) String rangeEnd,
                                                                        @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                        @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentsAdmin(eventId, userId, stateComments, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping(path = "/admin/events/comments/{commentId}")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public CommentDTO.Controller.CommentAdminDto patchCommentAdmin(@RequestBody @Valid CommentDTO.Controller.PatchCommentAdminDto commentDto,
                                                                   @PathVariable Long commentId) {
        return commentService.patchCommentAdmin(commentDto, commentId);
    }

    @DeleteMapping(path = "/admin/events/comments/{commentId}")
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    public CommentDTO.Controller.CommentAdminDto removeCommentAdmin(@PathVariable Long commentId) {
        return commentService.removeCommentAdmin(commentId);
    }


    @GetMapping(path = "/events/comments")
    @ResponseBody
    @ResponseStatus(value = HttpStatus.OK)
    public List<CommentDTO.Controller.CommentDto> getCommentsPublic(@RequestParam(required = false) List<Long> eventId,
                                                                    @RequestParam(required = false) String text,
                                                                    @RequestParam(required = false) String rangeStart,
                                                                    @RequestParam(required = false) String rangeEnd,
                                                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                    @Positive @RequestParam(defaultValue = "10") Integer size) {
        return commentService.getCommentsPublic(eventId, text, rangeStart, rangeEnd, from, size);
    }

}
