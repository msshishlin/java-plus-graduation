package ru.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Сходство мероприятий.
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Getter
@IdClass(EventSimilarityId.class)
@NoArgsConstructor
@Setter
@Table(name = "event_similarities", schema = "public")
@ToString
public class EventSimilarity {
    /**
     * Идентификатор первого мероприятия.
     */
    @Id
    @Column(name = "event_a", nullable = false)
    Long eventA;

    /**
     * Идентификатор второго мероприятия.
     */
    @Id
    @Column(name = "event_b", nullable = false)
    Long eventB;

    /**
     * Значение рассчитанного сходства мероприятий.
     */
    @Column(name = "score", nullable = false)
    Double score;

    /**
     * Метка времени, когда было совершено действие, инициирующее расчет.
     */
    @Column(name = "timestamp", nullable = false)
    Instant timestamp;
}
