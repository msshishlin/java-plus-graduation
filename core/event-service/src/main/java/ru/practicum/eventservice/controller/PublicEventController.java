package ru.practicum.eventservice.controller;

import ewm.client.AnalyzerGrpcClient;
import ewm.client.CollectorGrpcClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.eventservice.model.EventSearch;
import ru.practicum.eventservice.service.EventService;
import ru.practicum.interactionapi.dto.eventservice.EventDto;
import ru.practicum.interactionapi.dto.eventservice.EventShortDto;
import ru.practicum.interactionapi.dto.eventservice.EventSort;
import ru.practicum.interactionapi.exception.eventservice.EventNotFoundException;
import stats.message.analyzer.RecommendedEventProto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

/**
 * Контроллер для работы с событиями (публичное API).
 */
@RequestMapping("/events")
@RequiredArgsConstructor
@RestController
@Slf4j
public class PublicEventController {
    /**
     * GRPC-клиент для сервиса Analyzer.
     */
    private final AnalyzerGrpcClient analyzerGrpcClient;

    /**
     * GRPC-клиент для сервиса Collector.
     */
    private final CollectorGrpcClient collectorGrpcClient;

    /**
     * Сервис для работы с событиями.
     */
    private final EventService eventService;

    /**
     * Получить коллекцию событий.
     *
     * @param text          текст для поиска в содержимом аннотации и подробном описании события.
     * @param categories    коллекция идентификаторов категорий, в которых будет вестись поиск.
     * @param paid          поиск только платных/бесплатных событий.
     * @param rangeStart    дата и время, не раньше которых должно произойти событие.
     * @param rangeEnd      дата и время, не позже которых должно произойти событие.
     * @param onlyAvailable только события, у которых не исчерпан лимит запросов на участие.
     * @param sort          способ сортировки событий.
     * @param from          количество событий, которое нужно пропустить.
     * @param size          количество событий, которое нужно извлечь.
     * @param request       HTTP-запрос.
     * @return коллекция событий.
     */
    @GetMapping
    public Collection<EventShortDto> getEvents(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) Collection<@Positive Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false) String rangeStart,
                                               @RequestParam(required = false) String rangeEnd,
                                               @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                               @RequestParam(required = false) EventSort sort,
                                               @RequestParam(defaultValue = "0") int from,
                                               @RequestParam(defaultValue = "10") int size,
                                               HttpServletRequest request) {
        EventSearch eventSearch = EventSearch.builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart != null ? LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .rangeEnd(rangeEnd != null ? LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : null)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        log.info("Get events with params {}", eventSearch);
        return eventService.getPublishedEvents(eventSearch);
    }

    /**
     * Получить информацию об опубликованном событии.
     *
     * @param eventId идентификатор события.
     * @param userId  идентификатор пользователя.
     * @return трансферный объект, содержащий данные о событии.
     * @throws EventNotFoundException событие с идентификатором {@code eventId} не найдено или ещё не опубликовано.
     */
    @GetMapping("/{eventId}")
    public EventDto getPublishedEventById(@PathVariable @Positive Long eventId, @RequestHeader("X-EWM-USER-ID") long userId) throws EventNotFoundException {
        log.info("Get published event with id = {}", eventId);

        try {
            return eventService.getPublishedEventById(eventId);
        } finally {
            try {
                collectorGrpcClient.collectEventView(userId, eventId);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        }
    }

    /**
     * Лайк мероприятия.
     *
     * @param eventId идентификатор события.
     * @param userId  идентификатор пользователя.
     */
    @PutMapping("/{eventId}/like")
    public void likeEvent(@PathVariable @Positive Long eventId, @RequestHeader("X-EWM-USER-ID") long userId) {
        collectorGrpcClient.collectEventLike(userId, eventId);
    }

    /**
     * Получить рекомендации мероприятий для пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return рекомендации мероприятий.
     */
    @GetMapping("/recommendations")
    public Collection<RecommendedEventProto> getRecommendations(@RequestHeader("X-EWM-USER-ID") long userId) {
        return analyzerGrpcClient.getRecommendationsForUser(userId, 20);
    }
}
