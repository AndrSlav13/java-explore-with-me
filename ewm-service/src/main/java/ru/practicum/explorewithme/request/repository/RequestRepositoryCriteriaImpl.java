package ru.practicum.explorewithme.request.repository;

import ru.practicum.explorewithme.request.model.StatusEventParticipation;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestRepositoryCriteriaImpl implements RequestRepositoryCriteria {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Map<Long, Long> countAllByEventIdInAndStatusIn(List<Long> idsEvent, List<StatusEventParticipation> status) {
        Map<Long, Long> rezult = entityManager.createQuery("select e.id as id, count(e.id) as cnt from Event as e " +
                "left join Request r on e.id = r.event.id " +
                "where e.id in (?1) AND r.status in (?2) " +
                "group by id ", Tuple.class).setParameter(1, idsEvent).setParameter(2, status).getResultStream().collect(Collectors.toMap(
                tuple -> ((Number) tuple.get("id")).longValue(),
                tuple -> ((Number) tuple.get("cnt")).longValue(), (a, b) -> a)
        );
        return idsEvent.stream().collect(Collectors.toMap(a -> a, a -> rezult.isEmpty() || rezult.get(a) == null ? 0 : rezult.get(a), (a, b) -> a));
    }

}
