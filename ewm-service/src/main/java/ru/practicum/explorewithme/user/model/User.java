package ru.practicum.explorewithme.user.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.EntityInterfaces;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.request.model.Request;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "users", schema = "public")
public class User implements EntityInterfaces {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;
    @Column(length = 200)
    String name;
    @Column(unique = true, length = 200)
    String email;
    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "initiator",
            cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
            })
    Set<Event> eventsInited = new HashSet<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "requester",
            cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
            },
            orphanRemoval = true)
    Set<Request> eventsRequested = new HashSet<>();

    public Event addEventInited(Event event) {
        event.setInitiator(this);
        eventsInited.add(event);
        return event;
    }

    public void removeEventInited(Event event) {
        eventsInited.remove(event);
        event.setInitiator(null);
    }

    public Request addEventRequest(Event event, Request request) {
        eventsRequested.add(request);
        event.getRequesters().add(request);
        return request;
    }

    public void removeEventRequest(Event event) {
        for (Iterator<Request> iterator = eventsRequested.iterator();
             iterator.hasNext(); ) {
            Request request = iterator.next();

            if (request.getRequester().equals(this) &&
                    request.getEvent().equals(event)) {
                iterator.remove();
                request.getEvent().getRequesters().remove(request);
                request.setRequester(null);
                request.setEvent(null);
            }
        }
    }

    public void onRemoveEntity() {
        eventsRequested.stream().forEach(a -> removeEventRequest(a.getEvent()));
            eventsRequested.removeAll(eventsRequested);
        eventsInited.stream().forEach(a -> a.setInitiator(null));
            eventsInited.removeAll(eventsInited);
    }

}
