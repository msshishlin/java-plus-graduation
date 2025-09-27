package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.UserAction;

import java.util.Collection;

/**
 * Контракт хранилища данных о действиях пользователей над мероприятиями.
 */
public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    /**
     * Найти все действия пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return коллекция действий пользователя.
     */
    Collection<UserAction> findByUserId(long userId);

    /**
     * Найти действия всех пользователей по идентификатору события.
     *
     * @param eventId идентификатор события.
     * @return коллекция действий пользователя.
     */
    Collection<UserAction> findByEventId(long eventId);
}
