package ewm.client;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import stats.message.collector.ActionTypeProto;
import stats.message.collector.UserActionProto;
import stats.service.collector.UserActionControllerGrpc;

import java.time.Instant;

/**
 * GRPC-клиент для сервиса Collector.
 */
@Component
public class CollectorGrpcClient {
    /**
     * GRPC-клиент для сервиса Collector.
     */
    @GrpcClient("collector")
    private UserActionControllerGrpc.UserActionControllerBlockingStub client;

    /**
     * Обработать действие "Просмотр страницы мероприятия".
     *
     * @param userId  идентификатор пользователя.
     * @param eventId идентификатор мероприятия.
     */
    public void collectEventView(long userId, long eventId) {
        collectUserAction(userId, eventId, ActionTypeProto.ACTION_VIEW);
    }

    /**
     * Обработать действие "Заявка на участие в мероприятии".
     *
     * @param userId  идентификатор пользователя.
     * @param eventId идентификатор мероприятия.
     */
    public void collectEventRegister(long userId, long eventId) {
        collectUserAction(userId, eventId, ActionTypeProto.ACTION_REGISTER);
    }

    /**
     * Обработать действие "Положительная оценка/лайк мероприятию".
     *
     * @param userId  идентификатор пользователя.
     * @param eventId идентификатор мероприятия.
     */
    public void collectEventLike(long userId, long eventId) {
        collectUserAction(userId, eventId, ActionTypeProto.ACTION_LIKE);
    }

    /**
     * Обработать действие пользователя.
     *
     * @param userId          идентификатор пользователя.
     * @param eventId         идентификатор события.
     * @param actionTypeProto тип действия пользователя.
     */
    private void collectUserAction(long userId, long eventId, ActionTypeProto actionTypeProto) {
        UserActionProto requestProto = UserActionProto.newBuilder()
                .setUserId(userId)
                .setEventId(eventId)
                .setActionType(actionTypeProto)
                .setTimestamp(com.google.protobuf.Timestamp.newBuilder()
                        .setSeconds(Instant.now().getEpochSecond())
                        .setNanos(Instant.now().getNano())
                        .build())
                .build();

        client.collectUserAction(requestProto);
    }
}
