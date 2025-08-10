package ru.practicum.eventservice.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import ewm.client.StatsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.eventservice.model.Event;
import ru.practicum.eventservice.model.EventSearch;
import ru.practicum.eventservice.model.Location;
import ru.practicum.eventservice.model.QEvent;
import ru.practicum.eventservice.repository.EventRepository;
import ru.practicum.eventservice.service.mapper.EventMapper;
import ru.practicum.interactionapi.dto.categoryservice.CategoryDto;
import ru.practicum.interactionapi.dto.eventservice.*;
import ru.practicum.interactionapi.dto.userservice.UserDto;
import ru.practicum.interactionapi.exception.categoryservice.CategoryNotFoundException;
import ru.practicum.interactionapi.exception.eventservice.AccessToEventForbiddenException;
import ru.practicum.interactionapi.exception.eventservice.EventEditingException;
import ru.practicum.interactionapi.exception.eventservice.EventNotFoundException;
import ru.practicum.interactionapi.exception.eventservice.InvalidEventDateException;
import ru.practicum.interactionapi.exception.userservice.UserNotFoundException;
import ru.practicum.interactionapi.openfeign.CategoryServiceClient;
import ru.practicum.interactionapi.openfeign.UserServiceClient;
import ru.practicum.interactionapi.pageable.PageOffset;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Сервис для работы с событиями.
 */
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    /**
     * Хранилище данных о событиях.
     */
    private final EventRepository eventRepository;

    /**
     * Маппер для сущности события.
     */
    private final EventMapper eventMapper;

    /**
     * Клиент для сервиса управления категориями событий.
     */
    private final CategoryServiceClient categoryServiceClient;

    /**
     * Клиент для сервера статистики.
     */
    private final StatsClient statsClient;

    /**
     * Клиент для сервиса управления пользователями.
     */
    private final UserServiceClient userServiceClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public EventDto createEvent(Long initiatorId, CreateEventDto createEventDto) throws InvalidEventDateException {
        if (createEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new InvalidEventDateException("Дата и время события должна быть больше текущих даты и времени не менее, чем на 2 часа");
        }

        if (!userServiceClient.isUserExists(initiatorId)) {
            throw new UserNotFoundException(initiatorId);
        }
        if (!categoryServiceClient.isCategoryExists(createEventDto.getCategory())) {
            throw new CategoryNotFoundException(createEventDto.getCategory());
        }

        UserDto initiator = userServiceClient.getUser(initiatorId);
        CategoryDto category = categoryServiceClient.getCategory(createEventDto.getCategory());

        Event event = eventMapper.mapToEvent(createEventDto, initiator.getId(), category.getId());
        eventRepository.save(event);

        return eventMapper.mapToEventDto(event, initiator, category, 0L);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<EventDto> getEvents(Long initiatorId, int from, int size) throws UserNotFoundException {
        if (!userServiceClient.isUserExists(initiatorId)) {
            throw new UserNotFoundException(initiatorId);
        }

        UserDto initiator = userServiceClient.getUser(initiatorId);

        Predicate predicate = QEvent.event.initiatorId.eq(initiator.getId());
        PageOffset pageOffset = PageOffset.of(from, size, Sort.by("id").ascending());

        return eventMapper.mapToEventDtoCollection(eventRepository.findAll(predicate, pageOffset).getContent(), initiator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<EventShortDto> getEvents(Collection<Long> eventIds) {
        return eventMapper.mapToEventShortDtoCollection(eventRepository.findAllById(eventIds));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<EventDto> getEvents(EventSearch search) {
        return eventMapper.mapToEventDtoCollection(getEventsByEventSearch(search));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<EventShortDto> getPublishedEvents(EventSearch search) {
        return eventMapper.mapToEventShortDtoCollection(getEventsByEventSearch(search));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventDto getEvent(Long initiatorId, Long eventId) throws AccessToEventForbiddenException, EventNotFoundException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

        if (!Objects.equals(event.getInitiatorId(), initiatorId)) {
            throw new AccessToEventForbiddenException(eventId);
        }

        return eventMapper.mapToEventDto(event, userServiceClient.getUser(initiatorId), categoryServiceClient.getCategory(event.getCategoryId()), getEventStats(event.getId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventDto getPublishedEventById(Long eventId) throws EventNotFoundException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

        if (!Objects.equals(event.getState(), EventState.PUBLISHED)) {
            throw new EventNotFoundException(eventId);
        }

        return eventMapper.mapToEventDto(event, userServiceClient.getUser(event.getInitiatorId()), categoryServiceClient.getCategory(event.getCategoryId()), getEventStats(event.getId()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventDto updateEvent(Long eventId, UpdateEventDto updateEventDto) throws CategoryNotFoundException, EventEditingException, EventNotFoundException, InvalidEventDateException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new EventEditingException("Нельзя редактировать событие, до наступления которого осталось меньше часа");
        }

        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }

        if (updateEventDto.getAnnotation() != null) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }

        if (updateEventDto.getDescription() != null) {
            event.setDescription(updateEventDto.getDescription());
        }

        if (updateEventDto.getEventDate() != null) {
            if (updateEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new InvalidEventDateException("Дата и время события должна быть больше текущих даты и времени не менее, чем на 2 часа");
            }

            event.setEventDate(updateEventDto.getEventDate());
        }

        if (updateEventDto.getCategory() != null) {
            if (!categoryServiceClient.isCategoryExists(updateEventDto.getCategory())) {
                throw new CategoryNotFoundException(updateEventDto.getCategory());
            }

            event.setCategoryId(updateEventDto.getCategory());
        }

        if (updateEventDto.getLocation() != null) {
            event.setLocation(Location.builder().lat(updateEventDto.getLocation().getLat()).lon(updateEventDto.getLocation().getLon()).build());
        }

        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }

        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }

        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }

        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction() == EventStateAction.PUBLISH_EVENT) {
                if (event.getState() != EventState.PENDING) {
                    throw new EventEditingException("Cannot publish the event because it's not in the right state: PENDING");
                }
                event.setState(EventState.PUBLISHED);
            } else if (updateEventDto.getStateAction() == EventStateAction.REJECT_EVENT) {
                if (event.getState() == EventState.PUBLISHED) {
                    throw new EventEditingException("Cannot reject the event because it's in the state: PUBLISHED");
                }
                event.setState(EventState.REJECTED);
            }
        }

        return eventMapper.mapToEventDto(eventRepository.save(event), userServiceClient.getUser(event.getInitiatorId()), categoryServiceClient.getCategory(event.getCategoryId()), getEventStats(eventId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventDto updateEvent(Long initiatorId, Long eventId, UpdateEventDto updateEventDto) throws AccessToEventForbiddenException, CategoryNotFoundException, EventEditingException, EventNotFoundException, InvalidEventDateException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));

        if (!Objects.equals(event.getInitiatorId(), initiatorId)) {
            throw new AccessToEventForbiddenException(eventId);
        }

        if (Objects.equals(event.getState(), EventState.PUBLISHED)) {
            throw new EventEditingException(eventId);
        }

        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }

        if (updateEventDto.getAnnotation() != null) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }

        if (updateEventDto.getDescription() != null) {
            event.setDescription(updateEventDto.getDescription());
        }

        if (updateEventDto.getEventDate() != null) {
            if (updateEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new InvalidEventDateException("Дата и время события должна быть больше текущих даты и времени не менее, чем на 2 часа");
            }

            event.setEventDate(updateEventDto.getEventDate());
        }

        if (updateEventDto.getCategory() != null) {
            if (!categoryServiceClient.isCategoryExists(updateEventDto.getCategory())) {
                throw new CategoryNotFoundException(updateEventDto.getCategory());
            }

            event.setCategoryId(updateEventDto.getCategory());
        }

        if (updateEventDto.getLocation() != null) {
            event.setLocation(Location.builder().lat(updateEventDto.getLocation().getLat()).lon(updateEventDto.getLocation().getLon()).build());
        }

        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }

        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }

        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }

        if (updateEventDto.getStateAction() != null) {
            if (event.getState() == EventState.PUBLISHED) {
                throw new EventEditingException("Only pending or canceled events can be changed");
            }

            if (updateEventDto.getStateAction() == EventStateAction.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            } else if (updateEventDto.getStateAction() == EventStateAction.CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
            }
        }

        return eventMapper.mapToEventDto(eventRepository.save(event), userServiceClient.getUser(initiatorId), categoryServiceClient.getCategory(event.getCategoryId()), getEventStats(eventId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEventExists(Long eventId) {
        return eventRepository.existsById(eventId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEventsWithCategoryExists(Long categoryId) {
        return eventRepository.existsByCategoryId(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEventPublished(Long eventId) throws EventNotFoundException {
        return eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId)).getState().equals(EventState.PUBLISHED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void confirmParticipation(Long eventId) throws EventNotFoundException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);

        eventRepository.save(event);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void rejectParticipation(Long eventId) throws EventNotFoundException {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        event.setConfirmedRequests(event.getConfirmedRequests() - 1);

        eventRepository.save(event);
    }

    // region Facilities

    /**
     * Получить коллекцию событий с определенными параметрами.
     *
     * @param search объект, содержащий параметры поиска событий.
     * @return коллекция трансферных объектов, содержащий краткую информацию по событиям.
     */
    private Collection<Event> getEventsByEventSearch(EventSearch search) {
        QEvent event = QEvent.event;

        BooleanBuilder predicate = new BooleanBuilder();

        if (search.getText() != null && !search.getText().isBlank()) {
            predicate.and(event.annotation.contains(search.getText()).or(event.description.contains(search.getText())));
        }

        if (search.getUsers() != null && !search.getUsers().isEmpty()) {
            predicate.and(event.initiatorId.in(search.getUsers()));
        }

        if (search.getStates() != null && !search.getStates().isEmpty()) {
            predicate.and(event.state.in(search.getStates()));
        }

        if (search.getCategories() != null && !search.getCategories().isEmpty()) {
            predicate.and(event.categoryId.in(search.getCategories()));
        }

        if (search.getPaid() != null) {
            predicate.and(event.paid.eq(search.getPaid()));
        }

        if (search.getRangeStart() != null) {
            predicate.and(event.eventDate.after(search.getRangeStart()));
        }

        if (search.getRangeEnd() != null) {
            predicate.and(event.eventDate.before(search.getRangeEnd()));
        }

        if (search.isOnlyAvailable()) {
            predicate.and(event.participantLimit.eq(0).or(event.confirmedRequests.lt(event.participantLimit)));
        }

        Pageable pageable = PageOffset.of(search.getFrom(), search.getSize());
        if (search.getSort() != null) {
            switch (search.getSort()) {
                case EventSort.EVENT_DATE ->
                        pageable = PageOffset.of(search.getFrom(), search.getSize(), Sort.Direction.ASC, "eventDate");
                case EventSort.VIEWS ->
                        pageable = PageOffset.of(search.getFrom(), search.getSize(), Sort.Direction.ASC, "views");
            }
        }

        List<Event> result = new ArrayList<>();
        eventRepository.findAll(predicate, pageable).forEach(result::add);
        return result;
    }

    /**
     * Получить статистику просмотра события.
     *
     * @param eventId идентификатор события.
     * @return статистика просмотра события.
     */
    private Long getEventStats(long eventId) {
        LocalDateTime start = LocalDateTime.of(2020, 5, 5, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2035, 5, 5, 0, 0, 0);

        try {
            return Objects.requireNonNull(statsClient.getStats(start, end, List.of("/events/" + eventId), true).getBody()).getFirst().getHits();
        } catch (Throwable ex) {
            return 0L;
        }
    }

    // endregion
}
