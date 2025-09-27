package ru.practicum.analyzer.service;

import stats.message.analyzer.InteractionsCountRequestProto;
import stats.message.analyzer.RecommendedEventProto;
import stats.message.analyzer.SimilarEventsRequestProto;
import stats.message.analyzer.UserPredictionsRequestProto;

import java.util.Collection;

/**
 * Контракт рекомендательного сервиса.
 */
public interface RecommendationsService {
    /**
     * Получить поток рекомендованных мероприятий для указанного пользователя.
     *
     * @param requestProto входные данные.
     */
    Collection<RecommendedEventProto> getRecommendationsForUser(UserPredictionsRequestProto requestProto);

    /**
     * Получить поток мероприятий, с которыми не взаимодействовал данный пользователь, но которые максимально похожи на указанное мероприятие.
     *
     * @param requestProto входные данные.
     */
    Collection<RecommendedEventProto>  getSimilarEvents(SimilarEventsRequestProto requestProto);

    /**
     * Получить поток с суммой максимальных весов действий каждого пользователя с данными мероприятиями.
     *
     * @param requestProto входные данные.
     */
    Collection<RecommendedEventProto> getInteractionsCount(InteractionsCountRequestProto requestProto);
}
