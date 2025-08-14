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
import ru.practicum.interactionapi.exception.categoryservice.CategoryNotFoundException;
import ru.practicum.interactionapi.exception.eventservice.AccessToEventForbiddenException;
import ru.practicum.interactionapi.exception.eventservice.EventEditingException;
import ru.practicum.interactionapi.exception.eventservice.EventNotFoundException;
import ru.practicum.interactionapi.exception.eventservice.InvalidEventDateException;
import ru.practicum.interactionapi.exception.userservice.UserNotFoundException;

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
     * @param initiatorId    идентификатор инициатора события.
     * @param createEventDto трансферный объект, содержащий данные для добавления нового события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws InvalidEventDateException дата и время события меньше текущих даты и времени менее, чем на 2 часа.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@PathVariable(name = "userId") @Positive Long initiatorId,
                                @RequestBody @Valid CreateEventDto createEventDto) throws InvalidEventDateException {
        log.info("Create event - {} by user with id={}", createEventDto, initiatorId);
        return eventService.createEvent(initiatorId, createEventDto);
    }

    /**
     * Получить коллекцию событий, добавленных текущим пользователем.
     *
     * @param initiatorId идентификатор инициатора событий.
     * @param from        количество событий, которое необходимо пропустить.
     * @param size        количество событий, которое необходимо получить.
     * @return трансферный объект, содержащий данные о событии.
     * @throws UserNotFoundException пользователь с идентификатором {@code initiatorId} не найден.
     */
    @GetMapping
    public Collection<EventDto> getEvents(@PathVariable(name = "userId") @Positive Long initiatorId,
                                          @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(defaultValue = "10") @Positive int size) throws UserNotFoundException {
        log.info("Get events for user with id={} from {}, size={}", initiatorId, from, size);
        return eventService.getEvents(initiatorId, from, size);
    }

    /**
     * Получить событие, добавленное текущим пользователем.
     *
     * @param initiatorId идентификатор инициатора события.
     * @param eventId     идентификатор события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws AccessToEventForbiddenException доступ к событию с идентификатором {@code eventId} запрещён.
     * @throws EventNotFoundException          событие с идентификатором {@code eventId} не найдено.
     */
    @GetMapping("/{eventId}")
    public EventDto getEvent(@PathVariable(name = "userId") Long initiatorId,
                             @PathVariable @Positive Long eventId) throws AccessToEventForbiddenException, EventNotFoundException {
        log.info("Get event with id = {} for user with id = {}", eventId, initiatorId);
        return eventService.getEvent(initiatorId, eventId);
    }

    /**
     * Обновить событие, добавленное текущим пользователем.
     *
     * @param initiatorId    идентификатор инициатора события.
     * @param eventId        идентификатор события.
     * @param updateEventDto трансферный объект, содержащий данные для обновления события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws AccessToEventForbiddenException доступ к событию с идентификатором {@code eventId} запрещён.
     * @throws CategoryNotFoundException       категория с идентификатором {@code updateEventDto.category} не найдена.
     * @throws EventEditingException           событие с идентификатором {@code eventId} запрещено редактировать.
     * @throws EventNotFoundException          событие с идентификатором {@code eventId} не найдено или ещё не опубликовано.
     * @throws InvalidEventDateException       дата и время события меньше текущих даты и времени менее, чем на 2 часа.
     */
    @PatchMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable(name = "userId") Long initiatorId,
                                @PathVariable @Positive Long eventId,
                                @RequestBody @Valid UpdateEventDto updateEventDto) throws AccessToEventForbiddenException, CategoryNotFoundException, EventEditingException, EventNotFoundException, InvalidEventDateException {
        log.info("Update event {} with id = {} by user with id = {}", updateEventDto, eventId, initiatorId);
        return eventService.updateEvent(initiatorId, eventId, updateEventDto);
    }
}
