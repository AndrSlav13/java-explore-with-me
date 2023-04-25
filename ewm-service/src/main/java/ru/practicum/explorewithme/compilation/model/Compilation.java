package ru.practicum.explorewithme.compilation.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.EntityInterfaces;
import ru.practicum.explorewithme.event.model.Event;

import javax.persistence.*;
import java.util.HashSet;
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
@Table(name = "compilations", schema = "public")
public class Compilation implements EntityInterfaces {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;
    @Column(length = 200)
    String title;
    Boolean pinned;
    @Builder.Default
    @ToString.Exclude
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "event_compilation",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    Set<Event> events = new HashSet<>();

    public void addEvent(Event event) {
        events.add(event);
        event.getCompilations().add(this);
    }

    public void removeEvent(Event event) {
        event.getCompilations().remove(this);
        events.remove(event);
    }

    public void onRemoveEntity() {
        events.stream().forEach(a -> a.getCompilations().remove(a));
            events.removeAll(events);
    }
}
