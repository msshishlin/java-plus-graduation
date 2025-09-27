package ru.practicum.aggregator.handler;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Instant;
import java.util.*;

/**
 * Обработчик действий пользователей над мероприятиями.
 */
@Component
public class UserActionHandler {

    /**
     * Вес действия "Просмотр страницы мероприятия".
     */
    private static final double VIEW_WEIGHT = 0.4;

    /**
     * Вес действия "Заявка на участие в мероприятии".
     */
    private static final double REGISTER_WEIGHT = 0.8;

    /**
     * Вес действия "Положительная оценка/лайк мероприятия".
     */
    private static final double LIKE_WEIGHT = 1.0;

    /**
     * Максимальные веса действий пользователей для каждого из мероприятий.
     * Ключ - идентификатор мероприятия.
     * Значение - отображение, в котором ключ - идентификатор пользователя, а значение - максимальный вес из всех действий пользователя с мероприятием.
     */
    private final Map<Long, Map<Long, Double>> usersActionMaxWeightsForEachEvent = new HashMap<>();

    /**
     * Сумма весов действий пользователя для каждого из мероприятий.
     * Ключ - идентификатор мероприятия.
     * Значение - сумма весов действий пользователей с этим мероприятием.
     */
    private final Map<Long, Double> sumOfUsersActionWeightsForEachEvent = new HashMap<>();

    /**
     * Сумма минимальных весов для каждой пары мероприятий.
     * Ключ - идентификатор первого мероприятия.
     * Значение - отображение, в котором ключ - идентификатор второго события, а значение - сумма минимальных весов этих событий.
     */
    private final Map<Long, Map<Long, Double>> sumOfMinWeightsForEachEventPair = new HashMap<>();

    /**
     * Обработать действие пользователя над мероприятием.
     *
     * @param userActionAvro действие пользователя над мероприятием.
     * @return сходство текущего мероприятия с другими мероприятиями.
     */
    public Optional<List<EventSimilarityAvro>> handle(UserActionAvro userActionAvro) {
        long eventId = userActionAvro.getEventId();
        long userId = userActionAvro.getUserId();
        double userActionWeight = getUserActionWeight(userActionAvro.getActionType());
        Instant timestamp = userActionAvro.getTimestamp();

        // Если действий пользователей с данным мероприятием раньше не было.
        if (!usersActionMaxWeightsForEachEvent.containsKey(eventId)) {
            usersActionMaxWeightsForEachEvent.put(eventId, new HashMap<>());
        }

        // Если это первое действие пользователя с данным мероприятием.
        if (!usersActionMaxWeightsForEachEvent.get(eventId).containsKey(userId)) {
            usersActionMaxWeightsForEachEvent.get(eventId).put(userId, userActionWeight);
            sumOfUsersActionWeightsForEachEvent.put(eventId, sumOfUsersActionWeightsForEachEvent.getOrDefault(eventId, 0.0) + userActionWeight);

            return Optional.of(calculateEventsSimilarity(eventId, userId, 0.0, timestamp));
        }

        // Если вес предыдущего действия пользователя меньше веса текущего действия пользователя.
        double previousUserActionWeight = usersActionMaxWeightsForEachEvent.get(eventId).get(userId);
        if (previousUserActionWeight < userActionWeight) {
            usersActionMaxWeightsForEachEvent.get(eventId).put(userId, Math.max(previousUserActionWeight, userActionWeight));
            sumOfUsersActionWeightsForEachEvent.put(eventId, sumOfUsersActionWeightsForEachEvent.get(eventId) - previousUserActionWeight + userActionWeight);

            return Optional.of(calculateEventsSimilarity(eventId, userId, previousUserActionWeight, timestamp));
        }

        return Optional.empty();
    }

    /**
     * Рассчитать сходство мероприятия с другими мероприятиями.
     *
     * @param eventId                  идентификатор мероприятия.
     * @param userId                   идентификатор пользователя.
     * @param previousUserActionWeight вес предыдущего действия пользователя.
     * @param timestamp                временная метка.
     * @return сходство мероприятия с другими мероприятиями.
     */
    private List<EventSimilarityAvro> calculateEventsSimilarity(long eventId, long userId, double previousUserActionWeight, Instant timestamp) {
        List<EventSimilarityAvro> eventSimilarityList = new ArrayList<>();

        for (long otherEventId : sumOfUsersActionWeightsForEachEvent.keySet()) {
            if (otherEventId == eventId) {
                continue;
            }

            // Если пользователь не взаимодействовал с другим мероприятием, нет смысла проводить расчет для данной пары.
            if (usersActionMaxWeightsForEachEvent.get(otherEventId).get(userId) == null) {
                continue;
            }

            // Упорядочивание идентификаторы мероприятий по возрастанию.
            long firstEventId = Math.min(eventId, otherEventId);
            long secondEventId = Math.max(eventId, otherEventId);

            // Если сумма минимальных весов для данной пары отсутствует.
            if (!this.isSumOfMinWeightsForEventPairExists(firstEventId, secondEventId)) {
                // Вычисляем сумму минимальных весов для пары мероприятий.
                double sumOfMinWeightsForEventPair = calculateMinActionWeightOfEachUserForEventPair(firstEventId, secondEventId)
                        .values()
                        .stream()
                        .mapToDouble(Double::doubleValue)
                        .sum();

                sumOfMinWeightsForEachEventPair
                        .computeIfAbsent(firstEventId, map -> new HashMap<>())
                        .put(secondEventId, sumOfMinWeightsForEventPair);
            } else {
                // Получаем вес текущего действия пользователя для текущего мероприятия и для другого мероприятия.
                double currentUserActionWeight;
                double userActionWeightForOtherEvent;

                if (firstEventId == eventId) {
                    currentUserActionWeight = usersActionMaxWeightsForEachEvent.get(firstEventId).get(userId);
                    userActionWeightForOtherEvent = usersActionMaxWeightsForEachEvent.get(secondEventId).get(userId);
                } else {
                    currentUserActionWeight = usersActionMaxWeightsForEachEvent.get(secondEventId).get(userId);
                    userActionWeightForOtherEvent = usersActionMaxWeightsForEachEvent.get(firstEventId).get(userId);
                }

                // Рассчитываем предыдущий и текущий минимальные веса действий текущего пользователя для пары мероприятий.
                double previousMinWeightForEventPair = Math.min(userActionWeightForOtherEvent, previousUserActionWeight);
                double currentMinWeightForEventPair = Math.min(userActionWeightForOtherEvent, currentUserActionWeight);

                // Рассчитываем новую сумму минимальных весов действий пользователей для пары событий.
                double oldSumOfMinWeightsForEventPair = sumOfMinWeightsForEachEventPair.get(firstEventId).get(secondEventId);
                double newSumOfMinWeightsForEventPair = oldSumOfMinWeightsForEventPair - previousMinWeightForEventPair + currentMinWeightForEventPair;

                sumOfMinWeightsForEachEventPair.get(firstEventId).put(secondEventId, newSumOfMinWeightsForEventPair);
            }

            double similarity = sumOfMinWeightsForEachEventPair.get(firstEventId).get(secondEventId) /
                    (Math.sqrt(sumOfUsersActionWeightsForEachEvent.get(firstEventId)) * Math.sqrt(sumOfUsersActionWeightsForEachEvent.get(secondEventId)));

            EventSimilarityAvro eventSimilarity = EventSimilarityAvro.newBuilder()
                    .setEventA(firstEventId)
                    .setEventB(secondEventId)
                    .setScore(similarity)
                    .setTimestamp(timestamp)
                    .build();

            eventSimilarityList.add(eventSimilarity);
        }

        return eventSimilarityList;
    }

    /**
     * Проверить существует ли сумма минимальных весов для пары мероприятий.
     *
     * @param firstEventId  идентификатор первого мероприятия.
     * @param secondEventId идентификатор второго мероприятия.
     * @return признак существует ли сумма минимальных весов для пары мероприятий.
     */
    private boolean isSumOfMinWeightsForEventPairExists(long firstEventId, long secondEventId) {
        if (!sumOfMinWeightsForEachEventPair.containsKey(firstEventId)) {
            return false;
        }

        return sumOfMinWeightsForEachEventPair.get(firstEventId).containsKey(secondEventId);
    }

    /**
     * Посчитать для каждого пользователя минимальный вес действий для пары мероприятий.
     *
     * @param firstEventId  идентификатор первого мероприятия.
     * @param secondEventId идентификатор второго мероприятия.
     * @return сумма минимальных весов для пары мероприятий.
     */
    private Map<Long, Double> calculateMinActionWeightOfEachUserForEventPair(long firstEventId, long secondEventId) {
        // Получаем веса действий пользователей для каждого из мероприятий.
        Map<Long, Double> firstEventUsersActionWeights = usersActionMaxWeightsForEachEvent.get(firstEventId);
        Map<Long, Double> secondEventUsersActionWeights = usersActionMaxWeightsForEachEvent.get(secondEventId);

        // Собираем идентификаторы всех пользователей, выполнявших действия над первым или вторым мероприятиями, или над обоими сразу.
        Set<Long> userIds = new HashSet<>();
        userIds.addAll(firstEventUsersActionWeights.keySet());
        userIds.addAll(secondEventUsersActionWeights.keySet());

        Map<Long, Double> minActionWeightOfEachUserForEventPair = new HashMap<>();

        for (long userId : userIds) {
            double minUserActionWeightForFirstEvent = firstEventUsersActionWeights.getOrDefault(userId, 0.0);
            double minUserActionWeightForSecondEvent = secondEventUsersActionWeights.getOrDefault(userId, 0.0);

            minActionWeightOfEachUserForEventPair.put(userId, Math.min(minUserActionWeightForFirstEvent, minUserActionWeightForSecondEvent));
        }

        return minActionWeightOfEachUserForEventPair;
    }

    /**
     * Получить вес действия пользователя.
     *
     * @param userActionType тип действия пользователя.
     * @return вес действия пользователя.
     */
    private double getUserActionWeight(ActionTypeAvro userActionType) {
        return switch (userActionType) {
            case VIEW -> VIEW_WEIGHT;
            case REGISTER -> REGISTER_WEIGHT;
            case LIKE -> LIKE_WEIGHT;
        };
    }
}
