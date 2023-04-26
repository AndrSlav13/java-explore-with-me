/**
 * Для User - при выводе комментария выводятся комментарий к событию и все вложенные
 * Для Admin - только конкретные комментарии и дополнительная информация, если есть
 */
package ru.practicum.explorewithme.comment.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.comment.dto.CommentDTO;
import ru.practicum.explorewithme.comment.dto.CommentDetailsDTO;
import ru.practicum.explorewithme.comment.model.Comment;
import ru.practicum.explorewithme.comment.model.CommentDetails;
import ru.practicum.explorewithme.comment.model.StateComment;
import ru.practicum.explorewithme.comment.repository.CommentDetailsRepository;
import ru.practicum.explorewithme.comment.repository.CommentRepository;
import ru.practicum.explorewithme.dto.StatDTO;
import ru.practicum.explorewithme.event.dto.EventDTO;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.service.EventService;
import ru.practicum.explorewithme.exceptions.ApiErrorException;
import ru.practicum.explorewithme.user.dto.UserDTO;
import ru.practicum.explorewithme.user.model.User;
import ru.practicum.explorewithme.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {
    private final EventService eventService;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final CommentDetailsRepository commentDetailsRepository;

    @Autowired
    public CommentServiceImpl(EventService eventService, UserService userService,
                              CommentRepository commentRepository, CommentDetailsRepository commentDetailsRepository) {
        this.eventService = eventService;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.commentDetailsRepository = commentDetailsRepository;
    }

    @Override
    public List<CommentDTO.Controller.CommentDto> getCommentsUser(Long userId, Long eventId, Integer from, Integer size) {
        userService.findUserById(userId); //Проверка наличия в бд
        Pageable pg = PageRequest.of(from, size);
        //Комментарии к событию (комментарии первого уровня)
        List<Comment> parentComments = commentRepository.findByParentCommentIsNullAndCommentedId(eventId, pg);
        List<Long> parentIds = parentComments.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<Comment> subComments = commentRepository.findAllByParentCommentIdIn(parentIds);
        Map<Comment, List<Comment>> mapSubComments = subComments.stream().collect(Collectors.toMap(
                a -> a.getParentComment(),
                a -> List.of(a),
                (a, b) -> {
                    ArrayList<Comment> ar = new ArrayList(a);
                    ar.addAll(b);
                    return ar;
                }
        ));
        for (Comment com : parentComments)
            if (com.getChildComments() == null || com.getChildComments().isEmpty()) mapSubComments.put(com, List.of());

        return outputCommentsUserDTOs(mapSubComments);
    }

    @Override
    public Comment findCommentById(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> new ApiErrorException(404, "comment not found", "comment id=" + id + " is absent")
        );
        return comment;
    }

    @Transactional
    @Override
    public CommentDTO.Controller.CommentDto addCommentToEventUser(CommentDTO.Controller.NewCommentDto commentDto, Long userId, Long eventId) {
        User user = userService.findUserById(userId);
        Event event = eventService.findEventById(eventId);
        Comment comment = Comment.builder()
                .commenter(user)    //unidirectional связь
                .id(null)
                .publishedOn(LocalDateTime.now())
                .isPublished(true)
                .message(commentDto.getMessage())
                .parentComment(null)
                .build();
        event.addComment(comment);  //bidirectional связь
        commentRepository.save(comment);

        return outputCommentsUserDTOs(Map.of(comment, List.of())).get(0);
    }

    @Transactional
    @Override
    public CommentDTO.Controller.CommentDto addCommentToCommentUser(CommentDTO.Controller.NewCommentDto commentDto, Long userId, Long parentCommentId) {
        User user = userService.findUserById(userId);
        Comment parentComment = findCommentById(parentCommentId);
        Comment comment = Comment.builder()
                .commenter(user)    //unidirectional связь
                .id(null)
                .publishedOn(LocalDateTime.now())
                .isPublished(true)
                .message(commentDto.getMessage())
                .commented(parentComment.getCommented())
                .build();
        parentComment.addComment(comment);  //bidirectional связь
        commentRepository.save(comment);

        return outputCommentsUserDTOs(Map.of(comment.getParentComment(), List.of(comment))).get(0);
    }

    @Transactional
    @Override
    public CommentDTO.Controller.CommentDto removeCommentUser(Long userId, Long commentId) {
        User user = userService.findUserById(userId);
        Comment comment = findCommentById(commentId);
        CommentDTO.Controller.CommentDto commentDto = outputCommentsDTO(comment);

        if (user.getId() != comment.getCommenter().getId())
            throw new ApiErrorException(409, "the comment can't be removed", "the user isn't author of the comment");

        comment.removeComment();

        return commentDto;
    }

    @Override
    public CommentDTO.Controller.CommentAdminDto getCommentAdmin(Long commentId) {
        Comment comment = findCommentById(commentId);
        List<CommentDTO.Controller.CommentAdminDto> commentAdminDto = outputCommentsAdminDTOs(List.of(comment));

        return commentAdminDto.get(0);
    }

    @Override
    public List<CommentDTO.Controller.CommentAdminDto> getCommentsAdmin(List<Long> eventId, List<Long> userId, List<String> stateComments,
                                                                        String rangeStart, String rangeEnd, Integer from, Integer size) {
        Pageable pg = PageRequest.of(from, size);
        List<Comment> comments = commentRepository.getCommentsAdmin(eventId, userId,
                stateComments == null ? null : stateComments.stream().map(a -> StateComment.valueOf(a)).collect(Collectors.toList()),
                rangeStart == null ? null : LocalDateTime.parse(rangeStart, StatDTO.formatDateTime),
                rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, StatDTO.formatDateTime), pg);
        List<CommentDTO.Controller.CommentAdminDto> commentAdminDto = outputCommentsAdminDTOs(comments);

        return commentAdminDto;
    }

    @Transactional
    @Override
    public CommentDTO.Controller.CommentDto patchCommentUser(CommentDTO.Controller.PatchCommentUserDto commentDto, Long userId, Long commentId) {
        Comment comment = findCommentById(commentId);
        CommentDetails commentDetails = commentDetailsRepository.findById(commentId).orElse(
                CommentDetails.builder().comment(comment).stateComment(StateComment.MODERATION).date(LocalDateTime.now()).build()
        );
        if (commentDto.getDescription() != null) commentDetails.setDescription(commentDto.getDescription());
        if (commentDto.getState() != null) commentDetails.setStateComment(StateComment.valueOf(commentDto.getState()));
        commentDetailsRepository.save(commentDetails);

        return outputCommentsDTO(comment);
    }

    @Transactional
    @Override
    public CommentDTO.Controller.CommentAdminDto patchCommentAdmin(CommentDTO.Controller.PatchCommentAdminDto commentDto, Long commentId) {
        Comment comment = findCommentById(commentId);
        if (commentDto.getMessage() != null) comment.setMessage(commentDto.getMessage());
        if (commentDto.getDescription() != null || commentDto.getState() != null) {
            CommentDetails commentDetails = commentDetailsRepository.findById(commentId).orElse(CommentDetails.builder()
                    .comment(comment)
                    .date(LocalDateTime.now())
                    .stateComment(comment.getIsPublished() ? StateComment.PUBLISHED : StateComment.MODERATION)
                    .description("The comment is being moderated")
                    .build());
            if (commentDto.getDescription() != null) commentDetails.setDescription(commentDto.getDescription());
            if (commentDto.getState() != null)
                commentDetails.setStateComment(StateComment.valueOf(commentDto.getState()));
            commentDetailsRepository.save(commentDetails);
        }

        return outputCommentsAdminDTOs(List.of(comment)).get(0);
    }

    @Transactional
    @Override
    public CommentDTO.Controller.CommentAdminDto removeCommentAdmin(Long commentId) {
        Comment comment = findCommentById(commentId);
        CommentDTO.Controller.CommentAdminDto commentAdminDto = outputCommentsAdminDTOs(List.of(comment)).get(0);
        comment.removeComment();

        return commentAdminDto;
    }

    @Override
    public List<CommentDTO.Controller.CommentDto> getCommentsPublic(List<Long> eventId, String text, String rangeStart, String rangeEnd, Integer from, Integer size) {
        Pageable pg = PageRequest.of(from, size);
        List<Comment> comments = commentRepository.getCommentsPublic(eventId, text,
                rangeStart == null ? null : LocalDateTime.parse(rangeStart, StatDTO.formatDateTime),
                rangeEnd == null ? null : LocalDateTime.parse(rangeEnd, StatDTO.formatDateTime), pg);
        Map<Comment, List<Comment>> mapComments = comments.stream().collect(Collectors.toMap(
                a -> {
                    a = a;
                    Comment f = a.getParentComment();
                    return a.getParentComment() == null ? a : a.getParentComment();
                },
                a -> a.getParentComment() == null ? List.of() : List.of(a),
                (a, b) -> {
                    ArrayList<Comment> ar = new ArrayList(a);
                    ar.addAll(b);
                    return ar;
                }
        ));
        return outputCommentsUserDTOs(mapComments);
    }

    ///////////////////////////////////////////////////ВЫВОД///////////////////////////////////////////////////////
    //Публичный вывод
    public List<CommentDTO.Controller.CommentDto> outputCommentsUserDTOs(Map<Comment, List<Comment>> mapParentChildComment) {
        Set<Comment> flatListComments = new HashSet(mapParentChildComment.keySet());
        for (Comment com : mapParentChildComment.keySet())
            flatListComments.addAll(mapParentChildComment.get(com));

        List<Long> userIds = flatListComments.stream().map(a -> a.getCommenter().getId()).distinct().collect(Collectors.toList());
        List<UserDTO.Controller.UserShortDto> users = userService.findUsersByIdInDTO(userIds);
        Map<Long, UserDTO.Controller.UserShortDto> mapUserDto = users.stream().collect(Collectors.toMap(
                a -> a.getId(), a -> a, (a, b) -> a
        ));

        List<Long> eventIds = mapParentChildComment.keySet().stream().map(a -> a.getCommented().getId()).distinct().collect(Collectors.toList());
        List<Event> events = eventService.findEventsByIdIn(eventIds);
        Map<Long, EventDTO.Controller.EventForCommentDto> mapEventDto =
                events.stream().collect(Collectors.toMap(
                        a -> a.getId(),
                        a -> EventDTO.Controller.EventForCommentDto.builder()
                                .id(a.getId())
                                .title(a.getTitle())
                                .build(),
                        (a, b) -> a
                ));

        Map<CommentDTO.Controller.CommentDto, List<CommentDTO.Controller.CommentDto>> mapSubComments =
                mapParentChildComment.keySet().stream().filter(p -> p.getIsPublished() == true).collect(Collectors.toMap(
                        a -> CommentDTO.Controller.CommentDto.builder()
                                .childComments(null)
                                .commenter(mapUserDto.get(a.getCommenter().getId()))
                                .id(a.getId())
                                .message(a.getMessage())
                                .publishedOn(a.getPublishedOn().format(StatDTO.formatDateTime))
                                .event(mapEventDto.get(a.getCommented().getId()))
                                .build(),
                        a -> mapParentChildComment.get(a).stream().filter(p -> p.getIsPublished() == true)
                                .map(p -> CommentDTO.Controller.CommentDto.builder()
                                        .childComments(null)
                                        .commenter(mapUserDto.get(p.getCommenter().getId()))
                                        .id(p.getId())
                                        .message(p.getMessage())
                                        .publishedOn(p.getPublishedOn().format(StatDTO.formatDateTime))
                                        .build()).collect(Collectors.toList()),
                        (a, b) -> a));

        List<CommentDTO.Controller.CommentDto> rez = mapSubComments.keySet().stream().sorted(CommentDTO.Controller.CommentDto.Comparator::compare).map(
                a -> a.toBuilder()
                        .childComments(mapSubComments.get(a).stream().sorted(CommentDTO.Controller.CommentDto.Comparator::compare).collect(Collectors.toList()))
                        .build()
        ).collect(Collectors.toList());

        return rez;
    }

    //Краткий вывод
    //Только рассматриваемый комментарий и корневой комментарий (к событию) при наличии
    public CommentDTO.Controller.CommentDto outputCommentsDTO(Comment comment) {
        Map<Comment, List<Comment>> mapComments = new HashMap<>();
        if (comment.getParentComment() == null) mapComments.put(comment, List.of());
        else mapComments.put(comment.getParentComment(), List.of(comment));
        return outputCommentsUserDTOs(mapComments).get(0);
    }

    //Админский вывод
    //Комментарии по-отдельности и дополнительная информация
    public List<CommentDTO.Controller.CommentAdminDto> outputCommentsAdminDTOs(List<Comment> comments) {
        List<Long> userIds = comments.stream().map(a -> a.getCommenter().getId()).distinct().collect(Collectors.toList());
        List<UserDTO.Controller.UserShortDto> users = userService.findUsersByIdInDTO(userIds);
        Map<Long, UserDTO.Controller.UserShortDto> mapUserDto = users.stream().collect(Collectors.toMap(
                a -> a.getId(), a -> a, (a, b) -> a
        ));

        List<Long> eventIds = comments.stream().map(a -> a.getCommented().getId()).distinct().collect(Collectors.toList());
        List<Event> events = eventService.findEventsByIdIn(eventIds);
        Map<Long, EventDTO.Controller.EventForCommentDto> mapEventDto =
                events.stream().collect(Collectors.toMap(
                        a -> a.getId(),
                        a -> EventDTO.Controller.EventForCommentDto.builder()
                                .id(a.getId())
                                .title(a.getTitle())
                                .build(),
                        (a, b) -> a
                ));

        List<Long> commentIds = comments.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<CommentDetails> commentDetails = commentDetailsRepository.findAllByIdIn(commentIds);
        Map<Long, CommentDetailsDTO.CommentDetailsDto> mapCommentDetails = commentDetails.stream().collect(Collectors.toMap(
                a -> a.getId(),
                a -> CommentDetailsDTO.CommentDetailsDto.builder()
                        .stateComment(a.getStateComment().name())
                        .description(a.getDescription())
                        .date(a.getDate().format(StatDTO.formatDateTime))
                        .build(),
                (a, b) -> a
        ));

        List<CommentDTO.Controller.CommentAdminDto> rez =
                comments.stream().map(
                        a -> CommentDTO.Controller.CommentAdminDto.builder()
                                .commenter(mapUserDto.get(a.getCommenter().getId()))
                                .id(a.getId())
                                .message(a.getMessage())
                                .publishedOn(a.getPublishedOn().format(StatDTO.formatDateTime))
                                .event(mapEventDto.get(a.getCommented().getId()))
                                .commentDetails(mapCommentDetails.get(a.getId()))
                                .parentId(a.getParentComment() == null ? null : a.getParentComment().getId())
                                .build()).sorted(CommentDTO.Controller.CommentAdminDto.Comparator::compare).collect(Collectors.toList());

        return rez;
    }

}

