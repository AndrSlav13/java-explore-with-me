package ru.practicum.explorewithme.user.repository;

import org.hibernate.Session;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.request.dto.RequestDTO;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class UserRepositoryCriteriaImpl implements UserRepositoryCriteria {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public RequestDTO.Controller.ParticipationRequestDto save(Long userId, Long eventId) {
        Session session = entityManager.unwrap(Session.class);
        User user = session.load(User.class, userId);
        Event event = session.load(Event.class, eventId);
        Request request = user.addEventRequest(event, null);
        entityManager.persist(request);
        return RequestDTO.Controller.Mapper.toParticipationRequestDto(request);
    }
}
