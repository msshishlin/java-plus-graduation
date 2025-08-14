package ru.practicum.requestservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.requestservice.model.Request;

import java.util.Collection;
import java.util.Optional;

/**
 * Контракт хранилища данных о заявках на участие в событиях.
 */
public interface RequestRepository extends JpaRepository<Request, Long> {
    /**
     * Найти все заявки пользователя по его идентификатору.
     *
     * @param requesterId идентификатор пользователя.
     * @return коллекция заявок на участие в событиях.
     */
    Collection<Request> findByRequesterId(Long requesterId);

    /**
     * Найти заявки на участие в событии.
     *
     * @param eventId идентификатор события.
     * @return заявка на участие в событии.
     */
    Collection<Request> findByEventId(Long eventId);

    /**
     * Найти заявку пользователя на участие в событии.
     *
     * @param requesterId идентификатор пользователя.
     * @param eventId     идентификатор события.
     * @return заявка на участие в событии.
     */
    Optional<Request> findByRequesterIdAndEventId(Long requesterId, Long eventId);
}
