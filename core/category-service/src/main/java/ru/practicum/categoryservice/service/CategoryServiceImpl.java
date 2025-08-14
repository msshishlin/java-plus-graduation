package ru.practicum.categoryservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.categoryservice.model.Category;
import ru.practicum.categoryservice.repository.CategoryRepository;
import ru.practicum.categoryservice.service.mapper.CategoryMapper;
import ru.practicum.interactionapi.dto.categoryservice.CategoryDto;
import ru.practicum.interactionapi.dto.categoryservice.CreateCategoryDto;
import ru.practicum.interactionapi.dto.categoryservice.UpdateCategoryDto;
import ru.practicum.interactionapi.exception.categoryservice.CategoryNotFoundException;
import ru.practicum.interactionapi.exception.categoryservice.CategoryWithSameNameAlreadyExistsException;
import ru.practicum.interactionapi.exception.categoryservice.DeleteCategoryException;
import ru.practicum.interactionapi.openfeign.EventServiceClient;
import ru.practicum.interactionapi.pageable.PageOffset;

import java.util.Collection;

/**
 * Сервис для работы с категориями.
 */
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    /**
     * Хранилище данных о категориях.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Маппер для сущности категории.
     */
    private final CategoryMapper categoryMapper;

    /**
     * Клиент сервиса для работы с событиями.
     */
    private final EventServiceClient eventServiceClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryDto createCategory(CreateCategoryDto createCategoryDto) throws CategoryWithSameNameAlreadyExistsException {
        if (categoryRepository.existsByName(createCategoryDto.getName())) {
            throw new CategoryWithSameNameAlreadyExistsException(createCategoryDto.getName());
        }

        return categoryMapper.mapToCategoryDto(categoryRepository.save(categoryMapper.mapToCategory(createCategoryDto)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<CategoryDto> getCategories(int from, int size) {
        return categoryMapper.mapToCategoryDtoCollection(categoryRepository.findAll(PageOffset.of(from, size, Sort.by("id").ascending())).getContent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<CategoryDto> getCategories(Collection<Long> categoriesIds) {
        return categoryMapper.mapToCategoryDtoCollection(categoryRepository.findAllById(categoriesIds));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryDto getCategory(long categoryId) throws CategoryNotFoundException {
        return categoryMapper.mapToCategoryDto(categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryDto updateCategory(long categoryId, UpdateCategoryDto updateCategoryDto) throws CategoryNotFoundException, CategoryWithSameNameAlreadyExistsException {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
        if (category.getName().equalsIgnoreCase(updateCategoryDto.getName())) {
            return categoryMapper.mapToCategoryDto(category);
        }

        if (categoryRepository.existsByName(updateCategoryDto.getName())) {
            throw new CategoryWithSameNameAlreadyExistsException(updateCategoryDto.getName());
        }

        category.setName(updateCategoryDto.getName());
        return categoryMapper.mapToCategoryDto(categoryRepository.save(category));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCategory(long categoryId) throws CategoryNotFoundException {
        if (eventServiceClient.isEventsWithCategoryExists(categoryId)) {
            throw new DeleteCategoryException("Невозможно удалить категорию с привязанными событиями");
        }

        categoryRepository.delete(categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCategoryExists(long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
}
