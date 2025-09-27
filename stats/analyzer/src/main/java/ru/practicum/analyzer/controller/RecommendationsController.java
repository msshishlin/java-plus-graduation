package ru.practicum.analyzer.controller;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.analyzer.service.RecommendationsService;
import stats.message.analyzer.InteractionsCountRequestProto;
import stats.message.analyzer.RecommendedEventProto;
import stats.message.analyzer.SimilarEventsRequestProto;
import stats.message.analyzer.UserPredictionsRequestProto;
import stats.service.dashboard.RecommendationsControllerGrpc;

/**
 * GRPC-сервис для предоставления рекомендаций мероприятий пользователям.
 */
@GrpcService
@RequiredArgsConstructor
public class RecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {
    /**
     * Рекомендательный сервис.
     */
    private final RecommendationsService recommendationsService;

    /**
     * Получить поток рекомендованных мероприятий для указанного пользователя.
     *
     * @param requestProto     входные данные.
     * @param responseObserver специальный объект для формирования ответа.
     */
    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto requestProto, StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            recommendationsService.getRecommendationsForUser(requestProto).forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception exception) {
            responseObserver.onError(
                    new StatusRuntimeException(
                            Status.INTERNAL.withDescription(exception.getLocalizedMessage())
                                    .withCause(exception)
                    )
            );
        }
    }

    /**
     * Получить поток мероприятий, с которыми не взаимодействовал данный пользователь, но которые максимально похожи на указанное мероприятие.
     *
     * @param requestProto     входные данные.
     * @param responseObserver специальный объект для формирования ответа.
     */
    @Override
    public void getSimilarEvents(SimilarEventsRequestProto requestProto, StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            recommendationsService.getSimilarEvents(requestProto).forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception exception) {
            responseObserver.onError(
                    new StatusRuntimeException(
                            Status.INTERNAL.withDescription(exception.getLocalizedMessage())
                                    .withCause(exception)
                    )
            );
        }
    }

    /**
     * Получить поток с суммой максимальных весов действий каждого пользователя с данными мероприятиями.
     *
     * @param requestProto     входные данные.
     * @param responseObserver специальный объект для формирования ответа.
     */
    @Override
    public void getInteractionsCount(InteractionsCountRequestProto requestProto, StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            recommendationsService.getInteractionsCount(requestProto).forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception exception) {
            responseObserver.onError(
                    new StatusRuntimeException(
                            Status.INTERNAL.withDescription(exception.getLocalizedMessage())
                                    .withCause(exception)
                    )
            );
        }
    }
}
