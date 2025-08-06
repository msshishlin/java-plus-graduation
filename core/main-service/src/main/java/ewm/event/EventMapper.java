package ewm.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.dto.categoryservice.CategoryDto;
import ru.practicum.interactionapi.dto.userservice.UserShortDto;
import ru.practicum.interactionapi.openfeign.CategoryServiceClient;
import ru.practicum.interactionapi.openfeign.UserServiceClient;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Маппер для моделей, содержащих информацию о событии.
 */
@Component
@RequiredArgsConstructor
public class EventMapper {
    /**
     * Клиент для сервиса управления категориями событий.
     */
    private final CategoryServiceClient categoryServiceClient;

    /**
     * Клиент для сервиса управления пользователями.
     */
    private final UserServiceClient userServiceClient;

    /**
     * Преобразовать трансферный объект, содержащий данные для добавления нового события, в объект события.
     *
     * @param createEventDto трансферный объект, содержащий данные для добавления нового события.
     * @param initiator      инициатор события.
     * @param categoryDto    категория события.
     * @return объект события.
     */
    public Event mapToEvent(CreateEventDto createEventDto, UserShortDto initiator, CategoryDto categoryDto) {
        return Event.builder()
                .createdOn(LocalDateTime.now())
                .initiatorId(initiator.getId())
                .title(createEventDto.getTitle())
                .annotation(createEventDto.getAnnotation())
                .description(createEventDto.getDescription())
                .eventDate(createEventDto.getEventDate())
                .categoryId(categoryDto.getId())
                .location(createEventDto.getLocation())
                .paid(createEventDto.isPaid())
                .participantLimit(createEventDto.getParticipantLimit())
                .requestModeration(createEventDto.isRequestModeration())
                .state(EventState.PENDING)
                .build();
    }

    /**
     * Преобразовать объект события в трансферный объект, содержащий данные о событии.
     *
     * @param event       объект события.
     * @param initiator   инициатор события.
     * @param categoryDto категория события.
     * @param views       статистика просмотров события.
     * @return трансферный объект, содержащий данные о событии.
     */
    public EventDto mapToEventDto(Event event, UserShortDto initiator, CategoryDto categoryDto, int views) {
        return EventDto.builder()
                .id(event.getId())
                .createdOn(event.getCreatedOn())
                .initiator(initiator)
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .category(categoryDto)
                .location(event.getLocation())
                .publishedOn(event.getPublishedOn())
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .requestModeration(event.isRequestModeration())
                .confirmedRequests(event.getConfirmedRequests())
                .state(event.getState())
                .views(views)
                .build();
    }

    /**
     * Преобразовать объект события в трансферный объект, содержащий краткую информацию о событии.
     *
     * @param event       объект события.
     * @param initiator   инициатор события.
     * @param categoryDto категория события.
     * @param views       статистика просмотров события.
     * @return трансферный объект, содержащий краткую информацию о событии.
     */
    public EventShortDto mapToEventShortDto(Event event, UserShortDto initiator, CategoryDto categoryDto, int views) {
        return EventShortDto.builder()
                .id(event.getId())
                .initiator(initiator)
                .title(event.getTitle())
                .annotation(event.getAnnotation())
                .eventDate(event.getEventDate())
                .category(categoryDto)
                .paid(event.isPaid())
                .confirmedRequests(event.getConfirmedRequests())
                .views(views)
                .build();
    }

    /**
     * Преобразовать коллекцию объектов событий в коллекцию трансферных объектов, содержащих информацию о событиях.
     *
     * @param events        коллекция объектов события.
     * @param getEventViews функция получения статистики просмотров события.
     * @return коллекция трансферных объектов, содержащих информацию о событиях.
     */
    public Collection<EventDto> mapToEventDtoCollection(Collection<Event> events, Function<Long, Integer> getEventViews) {
        Map<Long, UserShortDto> users = new HashMap<>();
        Map<Long, CategoryDto> categories = new HashMap<>();

        return events.stream().map(event -> {
            UserShortDto userShortDto = users.get(event.getInitiatorId());
            if (userShortDto == null) {
                userShortDto = userServiceClient.findUserById(event.getInitiatorId());
                users.put(event.getInitiatorId(), userShortDto);
            }

            CategoryDto categoryDto = categories.get(event.getCategoryId());
            if (categoryDto == null) {
                categoryDto = categoryServiceClient.findCategoryById(event.getCategoryId());
                categories.put(event.getCategoryId(), categoryDto);
            }

            return mapToEventDto(event, userShortDto, categoryDto, getEventViews.apply(event.getId()));
        }).toList();
    }

    /**
     * Преобразовать коллекцию объектов событий в коллекцию трансферных объектов, содержащих краткую информацию о событиях.
     *
     * @param events        коллекция объектов события.
     * @param getEventViews функция получения статистики просмотров события.
     * @return коллекция трансферных объектов, содержащих краткую информацию о событиях.
     */
    public Collection<EventShortDto> mapToEventShortDtoCollection(Collection<Event> events, Function<Long, Integer> getEventViews) {
        Map<Long, UserShortDto> users = new HashMap<>();
        Map<Long, CategoryDto> categories = new HashMap<>();

        return events.stream().map(event -> {
            UserShortDto userShortDto = users.get(event.getInitiatorId());
            if (userShortDto == null) {
                userShortDto = userServiceClient.findUserById(event.getInitiatorId());
                users.put(event.getInitiatorId(), userShortDto);
            }

            CategoryDto categoryDto = categories.get(event.getCategoryId());
            if (categoryDto == null) {
                categoryDto = categoryServiceClient.findCategoryById(event.getCategoryId());
                categories.put(event.getCategoryId(), categoryDto);
            }

            return mapToEventShortDto(event, userShortDto, categoryDto, getEventViews.apply(event.getId()));
        }).toList();
    }
}
