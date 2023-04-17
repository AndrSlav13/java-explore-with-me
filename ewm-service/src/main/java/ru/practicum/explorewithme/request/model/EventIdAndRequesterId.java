package ru.practicum.explorewithme.request.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EventIdAndRequesterId implements Serializable {
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "requester_id")
    private Long requesterId;

    public EventIdAndRequesterId() {
    }

    public EventIdAndRequesterId(Long eventId, Long requesterId) {
        this.eventId = eventId;
        this.requesterId = requesterId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        EventIdAndRequesterId that = (EventIdAndRequesterId) o;
        return Objects.equals(eventId, that.eventId) &&
                Objects.equals(requesterId, that.requesterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, requesterId);
    }
}
