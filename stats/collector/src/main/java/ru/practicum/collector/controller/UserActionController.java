package ru.practicum.collector.controller;

import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.practicum.collector.service.UserActionService;
import stats.message.collector.UserActionProto;
import stats.service.collector.UserActionControllerGrpc;

/**
 * GRPC-сервис для работы с информацией о действиях пользователей.
 */
@GrpcService
@RequiredArgsConstructor
public class UserActionController extends UserActionControllerGrpc.UserActionControllerImplBase {
    /**
     * Сервис для работы с информацией о действиях пользователей.
     */
    private final UserActionService userActionService;

    /**
     * Обработать действие пользователя.
     *
     * @param request          запрос, содержащий данные о действии пользователя.
     * @param responseObserver специальный объект для формирования ответа.
     */
    @Override
    public void collectUserAction(UserActionProto request, StreamObserver<Empty> responseObserver) {
        try {
            userActionService.handle(request);

            responseObserver.onNext(Empty.getDefaultInstance());
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
