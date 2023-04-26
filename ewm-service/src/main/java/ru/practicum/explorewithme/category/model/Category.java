package ru.practicum.explorewithme.category.model;

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
@Table(name = "categories", schema = "public")
public class Category implements EntityInterfaces {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;
    @Column(unique = true, length = 200)
    String name;
    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "category")   //Добавление связи со стороны event
    Set<Event> events = new HashSet<>();

    public void addEvent(Event event) {
        events.add(event);
    }

    public void removeEvent(Event event) {
        events.remove(event);
    }

    public Boolean isAttachedToEvents() {
        return !events.isEmpty();
    }

    public void onRemoveEntity() {
        events.stream().forEach(a -> a.setCategory(null));
        events.removeAll(events);
    }
}