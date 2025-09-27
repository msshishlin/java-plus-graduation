package ru.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Действие пользователя.
 */
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Getter
@NoArgsConstructor
@Setter
@Table(name = "user_actions", schema = "public")
@ToString
public class UserAction {
    /**
     * Уникальный идентификатор действия пользователя.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор пользователя.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Идентификатор события.
     */
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    /**
     * Тип действия пользователя.
     */
    @Column(name = "action_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserActionType actionType;

    /**
     * Время совершения действия.
     */
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
}
