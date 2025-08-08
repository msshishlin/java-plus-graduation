package ru.practicum.eventservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventservice.model.EventSearch;
import ru.practicum.eventservice.service.EventService;
import ru.practicum.interactionapi.dto.eventservice.EventDto;
import ru.practicum.interactionapi.dto.eventservice.EventState;
import ru.practicum.interactionapi.dto.eventservice.UpdateEventDto;
import ru.practicum.interactionapi.exception.eventservice.EventEditingException;
import ru.practicum.interactionapi.exception.eventservice.EventNotFoundException;
import ru.practicum.interactionapi.exception.eventservice.InvalidEventDateException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * Контроллер для работы с событиями (API администратора).
 */
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@RestController
@Slf4j
public class AdminEventController {
    /**
     * Сервис для работы с событиями.
     */
    private final EventService eventService;

    /**
     * Получить коллекцию событий.
     *
     * @param users      коллекция идентификаторов пользователей, чьи события нужно найти
     * @param states     коллекция состояний в которых находятся искомые события.
     * @param categories коллекция идентификаторов категорий, в которых будет вестись поиск.
     * @param rangeStart дата и время, не раньше которых должно произойти событие.
     * @param rangeEnd   дата и время, не позже которых должно произойти событие.
     * @param from       количество событий, которое нужно пропустить.
     * @param size       количество событий, которое нужно извлечь.
     * @return коллекция событий.
     */
    @GetMapping
    public Collection<EventDto> getEvents(@RequestParam(required = false) Collection<Long> users,
                                          @RequestParam(required = false) Collection<EventState> states,
                                          @RequestParam(required = false) Collection<Long> categories,
                                          @RequestParam(required = false) String rangeStart,
                                          @RequestParam(required = false) String rangeEnd,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        EventSearch eventSearch = EventSearch.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart != null ? LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .rangeEnd(rangeEnd != null ? LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .from(from)
                .size(size)
                .build();

        log.info("Get events with params {}", eventSearch);
        return eventService.getEvents(eventSearch);
    }

    /**
     * Обновить событие.
     *
     * @param eventId        идентификатор события.
     * @param updateEventDto трансферный объект, содержащий данные для обновления события.
     * @return трансферный объект, содержащий данные о событии.
     * @throws EventEditingException     событие с идентификатором {@code eventId} запрещено редактировать.
     * @throws EventNotFoundException    событие с идентификатором {@code eventId} не найдено.
     * @throws InvalidEventDateException дата и время события меньше текущих даты и времени менее, чем на 2 часа.
     */
    @PatchMapping("/{eventId}")
    public EventDto updateEvent(@PathVariable @Positive Long eventId,
                                @RequestBody @Valid UpdateEventDto updateEventDto) throws EventEditingException, EventNotFoundException, InvalidEventDateException {
        log.info("Update event {} with id={}", updateEventDto, eventId);
        return eventService.updateEventByAdmin(eventId, updateEventDto);
    }

    /**
     * Подтвердить участие в событии.
     *
     * @param eventId идентификатор события.
     * @throws EventNotFoundException событие с идентификатором {@code eventId} не найдено.
     */
    @PatchMapping("/{eventId}/participation/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirmParticipation(@PathVariable @Positive Long eventId) throws EventNotFoundException {
        log.info("Confirm participation for event with id={}", eventId);
        eventService.confirmParticipation(eventId);
    }
}
