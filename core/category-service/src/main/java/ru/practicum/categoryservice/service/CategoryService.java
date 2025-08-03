package ru.practicum.categoryservice.service;

import ru.practicum.interactionapi.dto.categoryservice.CategoryDto;
import ru.practicum.interactionapi.dto.categoryservice.CreateCategoryDto;
import ru.practicum.interactionapi.dto.categoryservice.UpdateCategoryDto;
import ru.practicum.interactionapi.exception.categoryservice.CategoryNotFoundException;

import java.util.Collection;

/**
 * Контракт сервиса для работы с категориями.
 */
public interface CategoryService {
    /**
     * Добавить новую категорию.
     *
     * @param createCategoryDto трансферный объект, содержащий данные для добавления новой категории.
     * @return трансферный объект, содержащий данные о категории.
     */
    CategoryDto createCategory(CreateCategoryDto createCategoryDto);

    /**
     * Получить коллекцию категорий.
     *
     * @param from количество категорий, которое необходимо пропустить.
     * @param size количество категорий, которое необходимо получить.
     * @return коллекция категорий.
     */
    Collection<CategoryDto> getCategories(int from, int size);

    /**
     * Получить категорию.
     *
     * @param categoryId идентификатор категории.
     * @return трансферный объект, содержащий данные о категории.
     * @throws CategoryNotFoundException категория с идентификатором {@code categoryId} не найдена.
     */
    CategoryDto getCategory(long categoryId) throws CategoryNotFoundException;

    /**
     * Обновить категорию.
     *
     * @param categoryId        идентификатор категории.
     * @param updateCategoryDto трансферный объект, содержащий данные для обновления категории.
     * @return трансферный объект, содержащий данные о категории.
     * @throws CategoryNotFoundException категория с идентификатором {@code categoryId} не найдена.
     */
    CategoryDto updateCategory(long categoryId, UpdateCategoryDto updateCategoryDto) throws CategoryNotFoundException;

    /**
     * Удалить категорию.
     *
     * @param categoryId идентификатор категории.
     * @throws CategoryNotFoundException категория с идентификатором {@code categoryId} не найдена.
     */
    void deleteCategory(long categoryId) throws CategoryNotFoundException;
}
