package ewm.client;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import stats.message.analyzer.InteractionsCountRequestProto;
import stats.message.analyzer.RecommendedEventProto;
import stats.message.analyzer.SimilarEventsRequestProto;
import stats.message.analyzer.UserPredictionsRequestProto;
import stats.service.dashboard.RecommendationsControllerGrpc;

import java.util.ArrayList;
import java.util.List;

/**
 * GRPC-клиент для сервиса Analyzer.
 */
@Component
public class AnalyzerGrpcClient {
    /**
     * GRPC-клиент для сервиса Analyzer.
     */
    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub client;

    /**
     * Получить поток рекомендованных мероприятий для указанного пользователя.
     *
     * @param userId     идентификатор пользователя, для которого вычисляются рекомендации.
     * @param maxResults ограничение количества мероприятий в результате выполнения запроса.
     */
    public List<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        List<RecommendedEventProto> recommendations = new ArrayList<>();

        UserPredictionsRequestProto requestProto = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();
        client.getRecommendationsForUser(requestProto).forEachRemaining(recommendations::add);

        return recommendations;
    }

    /**
     * Получить поток мероприятий, с которыми не взаимодействовал данный пользователь, но которые максимально похожи на указанное мероприятие.
     *
     * @param eventId    идентификатор мероприятия, для которого нужно найти похожие мероприятия.
     * @param userId     идентификатор пользователя, для которого из выдачи нужно исключить мероприятия, с которыми он уже взаимодействовал.
     * @param maxResults ограничение количества мероприятий в результате выполнения запроса.
     */
    public List<RecommendedEventProto> getSimilarEvents(long eventId, long userId, int maxResults) {
        List<RecommendedEventProto> similarEvents = new ArrayList<>();

        SimilarEventsRequestProto requestProto = SimilarEventsRequestProto.newBuilder()
                .setEventId(eventId)
                .setUserId(userId)
                .setMaxResults(maxResults)
                .build();
        client.getSimilarEvents(requestProto).forEachRemaining(similarEvents::add);

        return similarEvents;
    }

    /**
     * Получить поток с суммой максимальных весов действий каждого пользователя с данными мероприятиями.
     *
     * @param eventIds идентификаторы мероприятий, для которых нужно вернуть сумму всех взаимодействий.
     */
    public List<RecommendedEventProto> getInteractionsCount(List<Long> eventIds) {
        List<RecommendedEventProto> interactionsCount = new ArrayList<>();

        InteractionsCountRequestProto requestProto = InteractionsCountRequestProto.newBuilder()
                .addAllEventId(eventIds)
                .build();
        client.getInteractionsCount(requestProto).forEachRemaining(interactionsCount::add);

        return interactionsCount;
    }
}
