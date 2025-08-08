package ru.practicum.categoryservice.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categoryservice.service.CategoryService;
import ru.practicum.interactionapi.dto.categoryservice.CategoryDto;
import ru.practicum.interactionapi.exception.categoryservice.CategoryNotFoundException;

import java.util.Collection;

/**
 * Контроллер для работы с категориями (публичное API).
 */
@RequestMapping("/categories")
@RequiredArgsConstructor
@RestController
@Slf4j
public class PublicCategoryController {
    /**
     * Сервис для работы с категориями.
     */
    private final CategoryService categoryService;

    /**
     * Получить коллекцию категорий.
     *
     * @param from количество категорий, которое необходимо пропустить.
     * @param size количество категорий, которое необходимо получить.
     * @return коллекция категорий.
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<CategoryDto> getCategories(@RequestParam(name = "ids", required = false) Collection<Long> categoriesIds,
                                                 @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        log.info("Get {} categories starts with {}", size, from);
        return categoryService.getCategories(categoriesIds, from, size);
    }

    /**
     * Найти категорию по её идентификатору.
     *
     * @param categoryId идентификатор категории.
     * @return трансферный объект, содержащий данные о категории.
     * @throws CategoryNotFoundException категория с идентификатором {@code categoryId} не найдена.
     */
    @GetMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto findCategoryById(@PathVariable Long categoryId) throws CategoryNotFoundException {
        log.info("Find category with id={}", categoryId);
        return categoryService.findCategoryById(categoryId);
    }
}
