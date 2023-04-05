package ru.practicum.explorewithme.statistics.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "statistics")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Statistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "time_stamp")
    LocalDateTime timestamp;
    String uri;
    String ip;
    String app;

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;
        if (this == o || this.getId().equals(((Statistics) o).getId())) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}