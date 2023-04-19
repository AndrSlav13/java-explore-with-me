package ru.practicum.explorewithme.request.model;
/*
Request is the linking table between user-requester and event-to-participate
It does function as table to fulfill Many-To-Many link operating with these tables pks, with complementary fields
**/

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.event.model.Event;
import ru.practicum.explorewithme.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "requests", schema = "public", uniqueConstraints = {@UniqueConstraint(name = "ComplexKeyConstraint", columnNames = {"eventId", "requesterId"})})
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;

    public Request() {
    }

    public Request(Event event, User requester) {
        this.event = event;
        this.requester = requester;
    }

    @ToString.Exclude
    Event event;
    @ToString.Exclude
    User requester;

    @Column
    LocalDateTime created = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    StatusEventParticipation status = StatusEventParticipation.PENDING;

    @Access(AccessType.PROPERTY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventId")
    public Event getEvent() {
        return event;
    }

    @Access(AccessType.PROPERTY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requesterId")
    public User getRequester() {
        return requester;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Request that = (Request) o;
        return Objects.equals(event, that.event) &&
                Objects.equals(requester, that.requester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, requester);
    }
}
