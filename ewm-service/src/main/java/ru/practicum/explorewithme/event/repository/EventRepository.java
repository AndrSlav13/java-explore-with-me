package ru.practicum.explorewithme.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.event.model.StateEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pg);

    Optional<Event> findEventByInitiatorIdAndId(Long userId, Long eventId);

    Optional<Event> findEventByIdAndCategory(Long id, String category);

    @Query("select e from Event as e " +
            "where (coalesce(:users , null) is null OR e.initiator.id in :users) " +
            "AND (coalesce(:states , null) is null OR e.state in :states) " +
            "AND (coalesce(:categories , null) is null OR e.category.id in :categories) " +
            "AND (coalesce(:rangeStart , null) is null OR e.eventDate >= :rangeStart) " +
            "AND (coalesce(:rangeEnd , null) is null OR e.eventDate <= :rangeEnd) ")
    List<Event> getEventsAdmin(@Param("users") List<Long> users,
                               @Param("states") List<StateEvent> states,
                               @Param("categories") List<Long> categories,
                               @Param("rangeStart") LocalDateTime rangeStart,
                               @Param("rangeEnd") LocalDateTime rangeEnd,
                               Pageable pg);

    @Query("select e from Event as e " +
            "   left join Request as r on e.id = r.event.id " +
            "   left join Category as cat on e.category.id = cat.id " +
            "where " +
            "( coalesce(:onlyAvailable , null) is null OR :onlyAvailable = false OR e.participantLimit = 0 OR e.participantLimit is null OR " +
            "e.id in (select distinct d.id from Request as r " +
            "   left join Event as d on d.id = r.event.id " +
            "   where r.status = ru.practicum.explorewithme.event.model.StateEvent.PUBLISHED " +
            "   group by d.id " +
            "having count(d.id) < max(d.participantLimit) " +
            ") " +
            ") AND " +
            "(coalesce(:text , null) is null OR lower(e.annotation) LIKE lower(concat('%',:text,'%')) OR lower(e.description) LIKE lower(concat('%',:text,'%'))) AND " +
            "(coalesce(:categories , null) is null OR cat.id in :categories) AND " +
            "(coalesce(:paid , null) is null OR e.paid = :paid) AND " +
            "(coalesce(:rangeStart , null) is null OR e.eventDate >= :rangeStart) AND " +
            "(coalesce(:rangeEnd , null) is null OR e.eventDate <= :rangeEnd) ")
    List<Event> getEventsPublic(@Param("text") String text,
                                @Param("categories") List<Long> categories,
                                @Param("paid") Boolean paid,
                                @Param("rangeStart") LocalDateTime rangeStart,
                                @Param("rangeEnd") LocalDateTime rangeEnd,
                                @Param("onlyAvailable") Boolean onlyAvailable,
                                Pageable pg);

    List<Event> findAllByIdIn(List<Long> ids);
}