package ru.practicum.analyzer.service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.UserAction;
import ru.practicum.analyzer.model.UserActionType;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

/**
 * Маппер для сущности действия пользователя.
 */
@Component
public class UserActionMapper {
    /**
     * Преобразовать объект действия пользователя в формате Avro в объект сущности действия пользователя.
     *
     * @param userActionAvro объект действия пользователя в формате Avro.
     * @return объект сущности действия пользователя
     */
    public UserAction mapToUserAction(UserActionAvro userActionAvro) {
        return UserAction.builder()
                .userId(userActionAvro.getUserId())
                .eventId(userActionAvro.getEventId())
                .actionType(switch (userActionAvro.getActionType()) {
                    case ActionTypeAvro.VIEW -> UserActionType.VIEW;
                    case ActionTypeAvro.REGISTER -> UserActionType.REGISTER;
                    case ActionTypeAvro.LIKE -> UserActionType.LIKE;
                })
                .timestamp(userActionAvro.getTimestamp())
                .build();
    }
}
