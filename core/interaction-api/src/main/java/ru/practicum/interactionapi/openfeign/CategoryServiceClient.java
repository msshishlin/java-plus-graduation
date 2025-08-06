package ru.practicum.interactionapi.openfeign;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.dto.categoryservice.CategoryDto;
import ru.practicum.interactionapi.dto.categoryservice.CreateCategoryDto;
import ru.practicum.interactionapi.dto.categoryservice.UpdateCategoryDto;
import ru.practicum.interactionapi.exception.categoryservice.CategoryNotFoundException;
import ru.practicum.interactionapi.exception.categoryservice.CategoryWithSameNameAlreadyExistsException;

import java.util.Collection;

/**
 * Контракт клиента для сервиса управления категориями событий.
 */
@FeignClient(name = "category-service")
public interface CategoryServiceClient {
    /**
     * Добавить новую категорию.
     *
     * @param createCategoryDto трансферный объект, содержащий данные для добавления новой категории.
     * @return трансферный объект, содержащий данные о категории.
     * @throws CategoryWithSameNameAlreadyExistsException категория с названием {@code createCategoryDto.name} уже существует.
     */
    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto createCategory(@RequestBody @Valid CreateCategoryDto createCategoryDto) throws CategoryWithSameNameAlreadyExistsException;

    /**
     * Получить коллекцию категорий.
     *
     * @param from количество категорий, которое необходимо пропустить.
     * @param size количество категорий, которое необходимо получить.
     * @return коллекция категорий.
     */
    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    Collection<CategoryDto> getCategories(@RequestParam(value = "from", defaultValue = "0") @PositiveOrZero int from,
                                          @RequestParam(value = "size", defaultValue = "10") @Positive int size);

    /**
     * Найти категорию по её идентификатору.
     *
     * @param categoryId идентификатор категории.
     * @return трансферный объект, содержащий данные о категории.
     * @throws CategoryNotFoundException категория с идентификатором {@code categoryId} не найдена.
     */
    @GetMapping("/categories/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    CategoryDto findCategoryById(@PathVariable Long categoryId) throws CategoryNotFoundException;

    /**
     * Обновить категорию.
     *
     * @param categoryId        идентификатор категории.
     * @param updateCategoryDto трансферный объект, содержащий данные для обновления категории.
     * @return трансферный объект, содержащий данные о категории.
     * @throws CategoryNotFoundException категория с идентификатором {@code categoryId} не найдена.
     * @throws CategoryWithSameNameAlreadyExistsException категория с названием {@code updateCategoryDto.name} уже существует.
     */
    @PatchMapping("/admin/categories/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    CategoryDto updateCategory(@PathVariable @Positive Long categoryId,
                               @RequestBody @Valid UpdateCategoryDto updateCategoryDto) throws CategoryNotFoundException, CategoryWithSameNameAlreadyExistsException;

    /**
     * Удалить категорию по её идентификатору.
     *
     * @param categoryId идентификатор категории.
     * @throws CategoryNotFoundException категория с идентификатором {@code categoryId} не найдена.
     */
    @DeleteMapping("/admin/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCategoryById(@PathVariable @Positive Long categoryId) throws CategoryNotFoundException;
}
