package ru.practicum.explorewithme.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.comment.model.Comment;
import ru.practicum.explorewithme.comment.model.StateComment;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.StateEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {


    @Query("select e from Comment as e left join CommentDetails as d on e.id = d.id " +
            "where (coalesce(:users , null) is null OR e.commenter.id in :users) " +
            "AND (coalesce(:events , null) is null OR e.commented.id in :events) " +
            "AND (coalesce(:states , null) is null OR (d is not null AND d.stateComment in :states)) " +
            "AND (coalesce(:rangeStart , null) is null OR (e.isPublished = true AND e.publishedOn >= :rangeStart)) " +
            "AND (coalesce(:rangeEnd , null) is null OR (e.isPublished = true AND e.publishedOn <= :rangeEnd)) ")
    List<Comment> getCommentsAdmin(@Param("events") List<Long> eventId,
                                   @Param("users") List<Long> userId,
                                   @Param("states") List<StateComment> states,
                                   @Param("rangeStart") LocalDateTime rangeStart,
                                   @Param("rangeEnd") LocalDateTime rangeEnd,
                                   Pageable pg);

    @Query("select e from Comment as e " +
            "where " +
            "(coalesce(:eventIds , null) is null OR e.commented.id in :eventIds ) AND " +
            "(coalesce(:text , null) is null OR lower(e.message) LIKE lower(concat('%',:text,'%')) ) AND " +
            "(coalesce(:rangeStart , null) is null OR e.publishedOn >= :rangeStart) AND " +
            "(coalesce(:rangeEnd , null) is null OR e.publishedOn <= :rangeEnd) ")
    List<Comment> getCommentsPublic(@Param("eventIds") List<Long> eventId,
                                    @Param("text") String text,
                                    @Param("rangeStart") LocalDateTime rangeStart,
                                    @Param("rangeEnd") LocalDateTime rangeEnd,
                                    Pageable pg);

    List<Comment> findByParentCommentIsNull(Pageable pg);  //Комментарии к событию
    List<Comment> findByParentCommentIsNullAndCommentedId(Long id, Pageable pg);  //Комментарии к событию

    List<Comment> findAllByParentCommentIdIn(@Param("ids") List<Long> ids);
}