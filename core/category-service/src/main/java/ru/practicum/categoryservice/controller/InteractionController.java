package ru.practicum.categoryservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categoryservice.service.CategoryService;
import ru.practicum.interactionapi.dto.categoryservice.CategoryDto;
import ru.practicum.interactionapi.exception.categoryservice.CategoryNotFoundException;

import java.util.Collection;

/**
 * Контроллер, содержащий конечные точки доступа для межсервисного взаимодействия.
 */
@RequestMapping("/interaction/categories")
@RequiredArgsConstructor
@RestController
@Slf4j
public class InteractionController {
    /**
     * Сервис для работы с категориями.
     */
    private final CategoryService categoryService;

    /**
     * Получить коллекцию категорий по их идентификаторам.
     *
     * @param categoriesIds идентификаторы категорий.
     * @return коллекция категорий.
     */
    @GetMapping
    public Collection<CategoryDto> getCategories(@RequestParam(name = "ids") Collection<Long> categoriesIds) {
        log.info("Get categories with ids = {}", categoriesIds);
        return categoryService.getCategories(categoriesIds);
    }

    /**
     * Получить категорию по её идентификатору.
     *
     * @param categoryId идентификатор категории.
     * @return трансферный объект, содержащий данные о категории.
     * @throws CategoryNotFoundException категория с идентификатором {@code categoryId} не найдена.
     */
    @GetMapping("/{categoryId}")
    public CategoryDto getCategory(@PathVariable Long categoryId) throws CategoryNotFoundException {
        log.info("Get category with id = {}", categoryId);
        return categoryService.getCategory(categoryId);
    }

    /**
     * Проверить существует ли категория.
     *
     * @param categoryId идентификатор категории.
     * @return признак существует ли категория.
     */
    @GetMapping("/check/existence/by/id/{categoryId}")
    public boolean isCategoryExists(@PathVariable Long categoryId) {
        log.info("Check category with id = {} existence", categoryId);
        return categoryService.isCategoryExists(categoryId);
    }
}
