package ru.practicum.explorewithme.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.EntityInterfaces;
import ru.practicum.explorewithme.category.model.Category;
import ru.practicum.explorewithme.comment.model.Comment;
import ru.practicum.explorewithme.compilation.model.Compilation;
import ru.practicum.explorewithme.exceptions.ApiErrorException;
import ru.practicum.explorewithme.request.model.Request;
import ru.practicum.explorewithme.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
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
@Table(name = "events", schema = "public")
public class Event implements EntityInterfaces {
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
    @ManyToOne(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
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
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            orphanRemoval = true)   //Удаление индексов в таблице смежности (?)
    Set<Request> requesters = new HashSet<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "commented",
            orphanRemoval = true,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    Set<Comment> comments = new HashSet<>();

    @Builder.Default
    @ToString.Exclude
    @ManyToMany(mappedBy = "events")
    Set<Compilation> compilations = new HashSet<>();

    public void setCategory(Category category) {
        if (this.category != null) this.category.removeEvent(this);
        this.category = category;
        category.addEvent(this);
    }

    public void addComment(Comment comment) {
        if (comment == null)
            throw new ApiErrorException(409, "comment isn't added", "comment object is null");
        comment.setCommented(this);
        comments.add(comment);
    }

    public void removeComment(Comment comment) {
        comment.setCommented(null);
        comments.remove(comment);
    }

    //Чтобы обеспечить разрыв связи сущностей и orphanRemoval при необходимости
    public void onRemoveEntity() {
        comments.stream().forEach(a -> {
            a.onRemoveEntity();
            a.setCommented(null);
        });
        comments.removeAll(comments);
        category.removeEvent(this);
        category = null;
        initiator.removeEventInited(this);
        requesters.stream().forEach(a -> a.getRequester().removeEventRequest(this));
        requesters.removeAll(requesters);
        compilations.stream().forEach(a -> a.removeEvent(this));
        compilations.removeAll(compilations);
    }
}
