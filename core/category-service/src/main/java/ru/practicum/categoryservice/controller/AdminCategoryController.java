package ru.practicum.categoryservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.categoryservice.service.CategoryService;
import ru.practicum.interactionapi.dto.categoryservice.CategoryDto;
import ru.practicum.interactionapi.dto.categoryservice.CreateCategoryDto;
import ru.practicum.interactionapi.dto.categoryservice.UpdateCategoryDto;
import ru.practicum.interactionapi.exception.categoryservice.CategoryNotFoundException;
import ru.practicum.interactionapi.exception.categoryservice.CategoryWithSameNameAlreadyExistsException;

/**
 * Контроллер для работы с категориями (API администратора).
 */
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@RestController
@Slf4j
public class AdminCategoryController {
    /**
     * Сервис для работы с категориями.
     */
    private final CategoryService categoryService;

    /**
     * Добавить новую категорию.
     *
     * @param createCategoryDto трансферный объект, содержащий данные для добавления новой категории.
     * @return трансферный объект, содержащий данные о категории.
     * @throws CategoryWithSameNameAlreadyExistsException категория с названием {@code createCategoryDto.name} уже существует.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CreateCategoryDto createCategoryDto) throws CategoryWithSameNameAlreadyExistsException {
        log.info("Create category - {}", createCategoryDto);
        return categoryService.createCategory(createCategoryDto);
    }

    /**
     * Обновить категорию.
     *
     * @param categoryId        идентификатор категории.
     * @param updateCategoryDto трансферный объект, содержащий данные для обновления категории.
     * @return трансферный объект, содержащий данные о категории.
     * @throws CategoryNotFoundException                  категория с идентификатором {@code categoryId} не найдена.
     * @throws CategoryWithSameNameAlreadyExistsException категория с названием {@code updateCategoryDto.name} уже существует.
     */
    @PatchMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable @Positive Long categoryId,
                                      @RequestBody @Valid UpdateCategoryDto updateCategoryDto) throws CategoryNotFoundException, CategoryWithSameNameAlreadyExistsException {
        log.info("Update category with id={} - {}", categoryId, updateCategoryDto);
        return categoryService.updateCategory(categoryId, updateCategoryDto);
    }

    /**
     * Удалить категорию по её идентификатору.
     *
     * @param categoryId идентификатор категории.
     * @throws CategoryNotFoundException категория с идентификатором {@code categoryId} не найдена.
     */
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable @Positive Long categoryId) throws CategoryNotFoundException {
        log.info("Delete category with id={}", categoryId);
        categoryService.deleteCategoryById(categoryId);
    }
}
