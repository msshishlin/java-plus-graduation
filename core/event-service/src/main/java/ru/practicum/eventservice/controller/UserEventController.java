package ru.practicum.eventservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventservice.service.EventService;
import ru.practicum.interactionapi.dto.eventservice.CreateEventDto;
import ru.practicum.interactionapi.dto.eventservice.EventDto;
import ru.practicum.interactionapi.dto.eventservice.UpdateEventDto;
import ru.practicum.interactionapi.exception.eventservice.AccessToEventForbiddenException;
import ru.practicum.interactionapi.exception.eventservice.EventEditingException;
import ru.practicum.interactionapi.exception.eventservice.EventNotFoundException;
import ru.practicum.interactionapi.exception.eventservice.InvalidEventDateException;

import java.util.Collection;

/**
 * Контроллер для работы с событиями (API авторизованного пользователя).
 */
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@RestController
@Slf4j
public class UserEventController {
    /**
     * Сервис для работы с событиями.
     */
    private final EventService eventService;

    /**
     * Добавить новое событие.
     *
     * @param userId         идентификатор текущего пользователя.
     * @param createEventDto трансферный объект, содержащий данные для добавления нового события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws InvalidEventDateException дата и время события меньше текущих даты и времени менее, чем на 2 часа.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@PathVariable @Positive Long userId,
                                @RequestBody @Valid CreateEventDto createEventDto) throws InvalidEventDateException {
        log.info("Create event - {} by user with id={}", createEventDto, userId);
        return eventService.createEvent(userId, createEventDto);
    }

    /**
     * Получить коллекцию событий
     *
     * @param userId идентификатор текущего пользователя.
     * @param from   количество событий, которое необходимо пропустить.
     * @param size   количество событий, которое необходимо получить.
     * @return трансферный объект, содержащий данные о событии.
     */
    @GetMapping
    public Collection<EventDto> getEvents(@PathVariable @Positive Long userId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Get events for user with id={} from {}, size={}", userId, from, size);
        return eventService.getEvents(userId, from, size);
    }

    /**
     * Получить информацию о событии.
     *
     * @param userId  идентификатор текущего пользователя.
     * @param eventId идентификатор события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws AccessToEventForbiddenException доступ к событию с идентификатором {@code eventId} запрещён.
     * @throws EventNotFoundException          событие с идентификатором {@code eventId} не найдено.
     */
    @GetMapping("/{eventId}")
    public EventDto getEventById(@PathVariable Long userId,
                                 @PathVariable @Positive Long eventId) throws AccessToEventForbiddenException, EventNotFoundException {
        log.info("Get event with id={} for user with id = {}", eventId, userId);
        return eventService.getEventById(userId, eventId);
    }

    /**
     * Обновить событие.
     *
     * @param userId         идентификатор пользователя.
     * @param eventId        идентификатор события.
     * @param updateEventDto трансферный объект, содержащий данные для обновления события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws AccessToEventForbiddenException доступ к событию с идентификатором {@code eventId} запрещён.
     * @throws EventEditingException           событие с идентификатором {@code eventId} запрещено редактировать.
     * @throws EventNotFoundException          событие с идентификатором {@code eventId} не найдено или ещё не опубликовано.
     * @throws InvalidEventDateException       дата и время события меньше текущих даты и времени менее, чем на 2 часа.
     */
    @PatchMapping("/{eventId}")
    public EventDto updateEventByUser(@PathVariable Long userId,
                                      @PathVariable @Positive Long eventId,
                                      @RequestBody @Valid UpdateEventDto updateEventDto) throws AccessToEventForbiddenException, EventEditingException, EventNotFoundException, InvalidEventDateException {
        log.info("Update event {} with id={} by user with id={}", updateEventDto, eventId, userId);
        return eventService.updateEventByUser(userId, eventId, updateEventDto);
    }
}
