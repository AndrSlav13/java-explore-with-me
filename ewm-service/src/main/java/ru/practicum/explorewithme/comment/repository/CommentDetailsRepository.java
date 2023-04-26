package ru.practicum.explorewithme.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.comment.model.CommentDetails;

import java.util.List;

public interface CommentDetailsRepository extends JpaRepository<CommentDetails, Long> {
    List<CommentDetails> findAllByIdIn(List<Long> ids);
}