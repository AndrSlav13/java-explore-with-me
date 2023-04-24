package ru.practicum.explorewithme.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.comment.model.CommentDetails;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.StateEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentDetailsRepository extends JpaRepository<CommentDetails, Long> {
    List<CommentDetails> findAllByIdIn(List<Long> ids);
}