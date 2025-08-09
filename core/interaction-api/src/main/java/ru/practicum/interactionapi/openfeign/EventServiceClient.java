package ru.practicum.interactionapi.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.interactionapi.dto.eventservice.EventDto;

/**
 * Контракт клиента сервиса для работы с событиями.
 */
@FeignClient(value = "event-service")
public interface EventServiceClient {
    /**
     * Проверить существует ли событие.
     *
     * @param eventId идентификатор события.
     * @return признак существует ли событие.
     */
    @GetMapping("/admin/events/{eventId}/check/existence")
    boolean isEventExists(@PathVariable Long eventId);

    /**
     * Проверить существуют ли события с данной категорией.
     *
     * @param categoryId идентификатор категории.
     * @return признак существуют ли события с данной категорией.
     */
    @GetMapping("/admin/events/check/existence/with/category/{categoryId}")
    boolean isEventsWithCategoryExists(@PathVariable Long categoryId);

    /**
     * Проверить опубликовано ли событие.
     *
     * @param eventId идентификатор события.
     * @return признак, опубликовано ли событие.
     */
    @GetMapping("/admin/events/{eventId}/check/publication")
    boolean isEventPublished(@PathVariable Long eventId);

    /**
     * Получить информацию об опубликованном событии.
     *
     * @param eventId идентификатор события.
     * @return трансферный объект, содержащий данные о событии.
     */
    @GetMapping("/events/{eventId}")
    EventDto getPublishedEventById(@PathVariable Long eventId);

    /**
     * Подтвердить участие в событии.
     *
     * @param eventId идентификатор события.
     */
    @PatchMapping(value = "/admin/events/{eventId}/participation/confirm")
    void confirmParticipation(@PathVariable Long eventId);

    /**
     * Отменить участие в событии.
     *
     * @param eventId идентификатор события.
     */
    @PatchMapping(value = "/admin/events/{eventId}/participation/reject")
    void rejectParticipation(@PathVariable Long eventId);
}
