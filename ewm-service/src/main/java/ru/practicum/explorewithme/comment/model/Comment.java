/**
 * Комментарий к событию может иметь вложенный комментарий. Остальные на том же уровне.
 * Comment
 *       \
 *       Comment
 *       Comment
 *             \
 *       Comment
 *       Comment
 */
package ru.practicum.explorewithme.comment.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.explorewithme.EntityInterfaces;
import ru.practicum.explorewithme.event.model.Event;
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
@Table(name = "comments", schema = "public")
public class Comment implements EntityInterfaces {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    Long id;
    @Column(length = 4000)
    String message;
    @ToString.Exclude
    User commenter;
    @ToString.Exclude
    Event commented;
    LocalDateTime publishedOn;
    @ToString.Exclude
    Comment parentComment;
    Boolean isPublished;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,
               mappedBy = "parentComment",
               orphanRemoval = true,
            cascade = CascadeType.ALL)
    Set<Comment> childComments = new HashSet<>();
    @Access(AccessType.PROPERTY)    //bidirectional
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    public Comment getParentComment() {
        return this.parentComment;
    }

    @Access(AccessType.PROPERTY)    //unidirectional
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commenter_id")
    public User getCommenter() {
        return this.commenter;
    }

    @Access(AccessType.PROPERTY)    //bidirectional
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commented_id")
    public Event getCommented() {
        return this.commented;
    }

    public void addComment(Comment comment) {   //Допустим один уровень вложенности комментария
        if(comment.parentComment != null) comment.parentComment.addComment(comment);
        else {
            childComments.add(comment);
            comment.setParentComment(this);
        }
    }

    public void removeComment() {
        if(parentComment != null) {
            parentComment.getChildComments().remove(this);
            parentComment.setParentComment(null);
        } else {
            commented.removeComment(this);
        }
    }

    public void onRemoveEntity() {
        if(parentComment == null) removeComment();
        childComments.stream().forEach(a -> {a.onRemoveEntity(); a.setParentComment(null);});
        childComments.removeAll(childComments);

        setCommented(null);
        setCommenter(null);
    }
}
