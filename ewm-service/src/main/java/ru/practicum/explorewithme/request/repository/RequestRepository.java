package ru.practicum.explorewithme.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.request.model.StatusEventParticipation;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long>, RequestRepositoryCriteria {
    //list of (eventId - requestsNum)
    //Количество подтвержденных запросов на участие не может превышать заданного лимита
    List<Request> findAllByRequesterId(Long userId, Pageable pg);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByIdInAndStatusIn(List<Long> id, List<StatusEventParticipation> status);
}