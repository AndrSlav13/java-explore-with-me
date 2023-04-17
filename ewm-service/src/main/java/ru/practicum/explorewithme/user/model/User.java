package ru.practicum.explorewithme.user.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.request.model.Request;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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
public class User {
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
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<Event> eventsInited = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "requester",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<Request> eventsRequested = new ArrayList<>();

    public Event addEventInited(Event event) {
        event.setInitiator(this);
        eventsInited.add(event);
        return event;
    }

    public Request addEventRequest(Event event, Request request) {
        eventsRequested.add(request);
        event.getRequesters().add(request);
        return request;
    }

}
