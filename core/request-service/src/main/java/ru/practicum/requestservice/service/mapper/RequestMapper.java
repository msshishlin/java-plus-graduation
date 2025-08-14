package ru.practicum.requestservice.service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.dto.requestservice.RequestDto;
import ru.practicum.requestservice.model.Request;

import java.util.Collection;

/**
 * Маппер для сущности заявки на участие в событии.
 */
@Component
public class RequestMapper {
    /**
     * Преобразовать объект заявки на участие в событии в трансферный объект, содержащий данные о заявке на участие в событие.
     *
     * @param request объект заявки на участие в событии.
     * @return трансферный объект, содержащий данные о заявке на участие в событие.
     */
    public RequestDto mapToRequestDto(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEventId())
                .requester(request.getRequesterId())
                .status(request.getStatus())
                .build();
    }

    /**
     * Преобразовать коллекцию объектов заявок на участие в событиях в коллекцию трансферных объектов, содержащих данные о заявках на участие в событиях.
     *
     * @param requests коллекция объектов заявок на участие в событиях.
     * @return коллекция трансферных объектов, содержащих данные о заявках на участие в событиях.
     */
    public Collection<RequestDto> mapToRequestDtoCollection(Collection<Request> requests) {
        return requests.stream().map(this::mapToRequestDto).toList();
    }
}
