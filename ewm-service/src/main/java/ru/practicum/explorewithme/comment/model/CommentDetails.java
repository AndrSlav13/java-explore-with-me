/**
 * Создается запись, если есть проблемы с комментарием
 */
package ru.practicum.explorewithme.comment.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "commentDetails", schema = "public")
public class CommentDetails {
    @Id
    @EqualsAndHashCode.Include
    Long id;
    @Enumerated(EnumType.STRING)
    StateComment stateComment = StateComment.MODERATION;
    @Column(length = 500)
    String description;
    LocalDateTime date;

    @OnDelete(action = OnDeleteAction.CASCADE)  //unidirectional связь - cascade и orphanRemoval не применить
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    Comment comment;
}
