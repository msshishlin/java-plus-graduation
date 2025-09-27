package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.model.UserAction;
import ru.practicum.analyzer.model.UserActionType;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.analyzer.repository.UserActionRepository;
import stats.message.analyzer.InteractionsCountRequestProto;
import stats.message.analyzer.RecommendedEventProto;
import stats.message.analyzer.SimilarEventsRequestProto;
import stats.message.analyzer.UserPredictionsRequestProto;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Рекомендательный сервис.
 */
@RequiredArgsConstructor
@Service
public class RecommendationsServiceImpl implements RecommendationsService {
    /**
     * Хранилище данных о сходствах мероприятий.
     */
    private final EventSimilarityRepository eventSimilarityRepository;

    /**
     * Хранилище данных о действиях пользователей над мероприятиями.
     */
    private final UserActionRepository userActionRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto requestProto) {
        // Получаем все действия пользователя.
        Collection<UserAction> userActions = userActionRepository.findByUserId(requestProto.getUserId());
        if (userActions.isEmpty()) {
            return Collections.emptyList();
        }

        // Получаем идентификаторы мероприятий, с которыми взаимодействовал пользователь.
        Set<Long> eventIds = userActions.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        // Формируем список сходств, в которых участвует событие, над которым пользователь выполнял какие-либо действия.
        List<EventSimilarity> eventSimilarities = new ArrayList<>();

        for (long eventId : eventIds) {
            eventSimilarities.addAll(eventSimilarityRepository.findByEventAOrEventB(eventId, eventId));
        }

        // Отфильтровываем сходства в которых есть события, над которыми пользователь не совершал никаких действий.
        // Отсортировываем их по убыванию рассчитанного коэффициента сходства.
        // Берем MaxResults сходств и превращаем их в рекомендованные события.
        return eventSimilarities.stream()
                .filter(eventSimilarity -> !eventIds.contains(eventSimilarity.getEventA()) || !eventIds.contains(eventSimilarity.getEventB()))
                .sorted(Comparator.comparing(EventSimilarity::getScore).reversed())
                .limit(requestProto.getMaxResults())
                .map(eventSimilarity -> RecommendedEventProto.newBuilder()
                        .setEventId(eventIds.contains(eventSimilarity.getEventA()) ? eventSimilarity.getEventB() : eventSimilarity.getEventA())
                        .setScore(eventSimilarity.getScore())
                        .build())
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<RecommendedEventProto> getSimilarEvents(SimilarEventsRequestProto requestProto) {
        // Получаем все действия пользователя.
        Collection<UserAction> userActions = userActionRepository.findByUserId(requestProto.getUserId());

        // Получаем идентификаторы мероприятий, с которыми взаимодействовал пользователь.
        Set<Long> eventIds = userActions.stream()
                .map(UserAction::getEventId)
                .collect(Collectors.toSet());

        // Получаем список сходств, в которых участвует событие из запроса.
        Collection<EventSimilarity> eventSimilarities = eventSimilarityRepository.findByEventAOrEventB(requestProto.getEventId(), requestProto.getEventId());

        // Отфильтровываем сходства в которых есть события, над которыми пользователь не совершал никаких действий.
        // Отсортировываем их по убыванию рассчитанного коэффициента сходства.
        // Берем MaxResults сходств и превращаем их в рекомендованные события.
        return eventSimilarities.stream()
                .filter(eventSimilarity -> !eventIds.contains(eventSimilarity.getEventA()) || !eventIds.contains(eventSimilarity.getEventB()))
                .sorted(Comparator.comparing(EventSimilarity::getScore).reversed())
                .limit(requestProto.getMaxResults())
                .map(eventSimilarity -> RecommendedEventProto.newBuilder()
                        .setEventId(eventIds.contains(eventSimilarity.getEventA()) ? eventSimilarity.getEventB() : eventSimilarity.getEventA())
                        .setScore(eventSimilarity.getScore())
                        .build())
                .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto requestProto) {
        return requestProto.getEventIdList().stream().map(eventId -> {
            Map<Long, Collection<Double>> userWeightsMap = new HashMap<>();

            Collection<UserAction> userActions = userActionRepository.findByEventId(eventId);
            for (UserAction userAction : userActions) {
                userWeightsMap.computeIfAbsent(userAction.getUserId(), (k) -> new ArrayList<>())
                        .add(switch (userAction.getActionType()) {
                            case UserActionType.VIEW -> 0.4;
                            case UserActionType.REGISTER -> 0.8;
                            case UserActionType.LIKE -> 1.0;
                        });
            }

            Map<Long, Double> userMaxWeights = userWeightsMap.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, (entry) -> entry.getValue().stream().max(Double::compareTo).get()));

            return RecommendedEventProto.newBuilder()
                    .setEventId(eventId)
                    .setScore(userMaxWeights.values().stream().mapToDouble(Double::doubleValue).sum())
                    .build();
        }).toList();
    }
}
