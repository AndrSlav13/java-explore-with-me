package ru.practicum.explorewithme.compilation.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.event.model.Event;

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
@Table(name = "compilations", schema = "public")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;
    @Column(length = 200)
    String title;
    Boolean pinned;
    @Builder.Default
    @ToString.Exclude
    @ManyToMany(mappedBy = "compilations")
    List<Event> events = new ArrayList<>();

    public void addEvent(Event event) {
        events.add(event);
        event.getCompilations().add(this);
    }

    public void removeEvent(Event event) {
        event.getCompilations().remove(this);
        events.remove(event);
    }

    public void removeEvents() {
        events.stream().forEach(a -> {
            events.remove(a);
            a.getCompilations().remove(a);
        });
    }
}
