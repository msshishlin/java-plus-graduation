package ru.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.collector.kafka.producer.KafkaStatsProducer;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import stats.message.collector.ActionTypeProto;
import stats.message.collector.UserActionProto;

import java.time.Instant;

/**
 * Сервис для работы с информацией о действиях пользователей.
 */
@Component
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    /**
     * Издатель данных Kafka.
     */
    private final KafkaStatsProducer kafkaStatsProducer;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(UserActionProto userActionProto) {
        kafkaStatsProducer.sendUserAction(mapToUserActionAvro(userActionProto));
    }

    /**
     * Конвертировать данные о действии пользователя из формата Protobuf в формат Avro.
     *
     * @param userActionProto данные о действии пользователя в формате Protobuf.
     * @return данные о действии пользователя в формате Avro.
     */
    private UserActionAvro mapToUserActionAvro(UserActionProto userActionProto) {
        return UserActionAvro.newBuilder()
                .setUserId(userActionProto.getUserId())
                .setEventId(userActionProto.getEventId())
                .setActionType(mapToActionTypeAvro(userActionProto.getActionType()))
                .setTimestamp(Instant.ofEpochSecond(userActionProto.getTimestamp().getSeconds(), userActionProto.getTimestamp().getNanos()))
                .build();
    }

    /**
     * Конвертировать данные о типе действия пользователя из формата Protobuf в формат Avro.
     *
     * @param actionTypeProto тип действия пользователя в формате Protobuf.
     * @return тип действия пользователя в формате Avro.
     */
    private ActionTypeAvro mapToActionTypeAvro(ActionTypeProto actionTypeProto) {
        return switch (actionTypeProto) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            case UNRECOGNIZED -> throw new RuntimeException("Can't map action type");
        };
    }
}
