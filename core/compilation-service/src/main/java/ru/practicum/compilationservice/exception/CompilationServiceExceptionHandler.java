package ru.practicum.compilationservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.interactionapi.exception.compilationservice.CompilationNotFoundException;

/**
 * Обработчик исключений, возникающих в сервисе.
 */
@RestControllerAdvice
public class CompilationServiceExceptionHandler {
    /**
     * Обработать исключение, выбрасываемое сервисом, если подборка событий не была найдена.
     *
     * @param compilationNotFoundException исключение, выбрасываемое сервисом, если подборка событий не была найдена.
     * @return результат обработки исключения.
     */
    @ExceptionHandler
    public ResponseEntity<Exception> handleCompilationNotFoundException(final CompilationNotFoundException compilationNotFoundException) {
        return new ResponseEntity<>(compilationNotFoundException, HttpStatus.NOT_FOUND);
    }
}
