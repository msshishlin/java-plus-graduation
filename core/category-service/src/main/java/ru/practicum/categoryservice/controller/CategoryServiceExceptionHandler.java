package ru.practicum.categoryservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.interactionapi.exception.categoryservice.CategoryNotFoundException;
import ru.practicum.interactionapi.exception.categoryservice.CategoryWithSameNameAlreadyExistsException;

/**
 * Обработчик исключений, возникающих в сервисе.
 */
@RestControllerAdvice
public class CategoryServiceExceptionHandler {
    /**
     * Обработать исключение, выбрасываемое сервисом, если категория не была найдена.
     *
     * @param categoryNotFoundException исключение, выбрасываемое сервисом, если категория не была найдена.
     * @return результат обработки исключения.
     */
    @ExceptionHandler
    public ResponseEntity<Exception> handleCategoryNotFoundException(final CategoryNotFoundException categoryNotFoundException) {
        return new ResponseEntity<>(categoryNotFoundException, HttpStatus.NOT_FOUND);
    }

    /**
     * Обработать исключение, выбрасываемое сервисом, если категория с таким названием уже существует в БД.
     *
     * @param categoryWithSameNameAlreadyExistsException исключение, выбрасываемое сервисом, если категория с таким названием уже существует в БД.
     * @return результат обработки исключения.
     */
    @ExceptionHandler
    public ResponseEntity<Exception> handleCategoryWithSameNameAlreadyExistsException(final CategoryWithSameNameAlreadyExistsException categoryWithSameNameAlreadyExistsException) {
        return new ResponseEntity<>(categoryWithSameNameAlreadyExistsException, HttpStatus.CONFLICT);
    }
}
