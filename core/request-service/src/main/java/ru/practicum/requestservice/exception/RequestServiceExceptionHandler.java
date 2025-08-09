package ru.practicum.requestservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.interactionapi.exception.eventservice.AccessToEventForbiddenException;
import ru.practicum.interactionapi.exception.eventservice.EventNotFoundException;
import ru.practicum.interactionapi.exception.requestservice.CreateRequestException;
import ru.practicum.interactionapi.exception.requestservice.UpdateRequestStatusException;
import ru.practicum.interactionapi.exception.userservice.UserNotFoundException;

/**
 * Обработчик исключений, возникающих в сервисе.
 */
@RestControllerAdvice
public class RequestServiceExceptionHandler {
    /**
     * Обработать исключение, выбрасываемое сервисом, если доступ к событию запрещен.
     *
     * @param accessToEventForbiddenException исключение, выбрасываемое сервисом, если доступ к событию запрещен.
     * @return результат обработки исключения.
     */
    @ExceptionHandler
    public ResponseEntity<Exception> handleAccessToEventForbiddenException(final AccessToEventForbiddenException accessToEventForbiddenException) {
        return new ResponseEntity<>(accessToEventForbiddenException, HttpStatus.FORBIDDEN);
    }

    /**
     * Обработать исключение, выбрасываемое сервисом, если невозможно создать заявку на участие в событии.
     *
     * @param createRequestException исключение, выбрасываемое сервисом, если невозможно создать заявку на участие в событии.
     * @return результат обработки исключения.
     */
    @ExceptionHandler
    public ResponseEntity<Exception> handleCreateRequestException(final CreateRequestException createRequestException) {
        return new ResponseEntity<>(createRequestException, HttpStatus.CONFLICT);
    }

    /**
     * Обработать исключение, выбрасываемое сервисом, если событие не было найдено.
     *
     * @param eventNotFoundException исключение, выбрасываемое сервисом, если событие не было найдено.
     * @return результат обработки исключения.
     */
    @ExceptionHandler
    public ResponseEntity<Exception> handleEventNotFoundException(final EventNotFoundException eventNotFoundException) {
        return new ResponseEntity<>(eventNotFoundException, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработать исключение, выбрасываемое сервисом, если в пути запроса пропущен обязательный параметр.
     *
     * @param missingPathVariableException исключение, выбрасываемое сервисом, если в пути запроса пропущен обязательный параметр.
     * @return результат обработки исключения.
     */
    @ExceptionHandler
    public ResponseEntity<Exception> handleMissingPathVariableException(final MissingPathVariableException missingPathVariableException) {
        return new ResponseEntity<>(missingPathVariableException, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обработать исключение, выбрасываемое сервисом, если невозможно обновить статус заявки на участие в событии.
     *
     * @param updateRequestStatusException исключение, выбрасываемое сервисом, если невозможно обновить статус заявки на участие в событии.
     * @return результат обработки исключения.
     */
    @ExceptionHandler
    public ResponseEntity<Exception> handleUpdateRequestStatusException(final UpdateRequestStatusException updateRequestStatusException) {
        return new ResponseEntity<>(updateRequestStatusException, HttpStatus.CONFLICT);
    }

    /**
     * Обработать исключение, выбрасываемое сервисом, если пользователь не был найден.
     *
     * @param userNotFoundException исключение, выбрасываемое сервисом, если пользователь не был найден.
     * @return результат обработки исключения.
     */
    @ExceptionHandler
    public ResponseEntity<Exception> handleUserNotFoundException(final UserNotFoundException userNotFoundException) {
        return new ResponseEntity<>(userNotFoundException, HttpStatus.BAD_REQUEST);
    }
}
