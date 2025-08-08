package ru.practicum.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.interactionapi.exception.userservice.UserNotFoundException;
import ru.practicum.interactionapi.exception.userservice.UserWithSameEmailAlreadyExistsException;

/**
 * Обработчик исключений, возникающих в сервисе.
 */
@RestControllerAdvice
public class UserServiceExceptionHandler {
    /**
     * Обработать исключение, выбрасываемое сервисом, если пользователь не был найден.
     *
     * @param userNotFoundException исключение, выбрасываемое сервисом, если пользователь не был найден.
     * @return результат обработки исключения.
     */
    @ExceptionHandler
    public ResponseEntity<Exception> handleUserNotFoundException(final UserNotFoundException userNotFoundException) {
        return new ResponseEntity<>(userNotFoundException, HttpStatus.NOT_FOUND);
    }

    /**
     * Обработать исключение, выбрасываемое сервисом, если производится попытка создания нового пользователя с адресом электронной почты, который уже занят другим пользователем.
     *
     * @param userWithSameEmailAlreadyExistsException исключение, выбрасываемое сервисом, если производится попытка создания нового пользователя с адресом электронной почты, который уже занят другим пользователем.
     * @return результат обработки исключения.
     */
    @ExceptionHandler
    public ResponseEntity<Exception> handleUserWithSameEmailAlreadyExistsException(final UserWithSameEmailAlreadyExistsException userWithSameEmailAlreadyExistsException) {
        return new ResponseEntity<>(userWithSameEmailAlreadyExistsException, HttpStatus.CONFLICT);
    }
}
