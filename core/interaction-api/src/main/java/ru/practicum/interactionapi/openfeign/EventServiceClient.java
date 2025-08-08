package ru.practicum.interactionapi.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.interactionapi.dto.eventservice.EventDto;
import ru.practicum.interactionapi.exception.eventservice.EventNotFoundException;

/**
 * Контракт клиента сервиса для работы с событиями.
 */
@FeignClient(value = "event-service")
public interface EventServiceClient {
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
     * @throws EventNotFoundException событие с идентификатором {@code eventId} не найдено.
     */
    @PatchMapping(value = "/admin/events/{eventId}/participation/confirm")
    void confirmParticipation(@PathVariable Long eventId);
}
