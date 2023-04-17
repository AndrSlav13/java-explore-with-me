package ru.practicum.explorewithme.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.compilation.model.Compilation;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
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
@Table(name = "events", schema = "public")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;
    @Column(length = 200)
    String title;
    @Column(length = 1200)
    String annotation;
    Category category;
    Boolean paid;
    LocalDateTime eventDate;
    @ToString.Exclude
    User initiator;
    @Column(length = 1200)
    String description;
    Integer participantLimit;
    @Enumerated(EnumType.STRING)
    StateEvent state = StateEvent.PENDING;
    LocalDateTime createdOn;
    LocalDateTime publishedOn;
    String location;
    Boolean requestModeration;

    @Access(AccessType.PROPERTY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    public Category getCategory() {
        return this.category;
    }

    @Access(AccessType.PROPERTY)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User getInitiator() {
        return this.initiator;
    }

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    List<Request> requesters = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(
            name = "event_compilation",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "compilation_id")
    )
    List<Compilation> compilations = new ArrayList<>();

    public void setCategory(Category category) {
        if (category != null) category.removeEvent(this);
        this.category = category;
        category.addEvent(this);
    }
}
