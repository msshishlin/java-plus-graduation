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
import ru.practicum.interactionapi.pageable.PageOffset;

import java.util.Collection;

/**
 * Сервис для работы с категориями.
 */
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    /**
     * Хранилище данных для категорий.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Маппер для сущности категории.
     */
    private final CategoryMapper categoryMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryDto createCategory(CreateCategoryDto createCategoryDto) throws CategoryWithSameNameAlreadyExistsException {
        if (categoryRepository.findByName(createCategoryDto.getName()).isPresent()) {
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
    public CategoryDto getCategory(long categoryId) throws CategoryNotFoundException {
        return categoryMapper.mapToCategoryDto(getCategoryById(categoryId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryDto updateCategory(long categoryId, UpdateCategoryDto updateCategoryDto) throws CategoryNotFoundException, CategoryWithSameNameAlreadyExistsException {
        Category category = getCategoryById(categoryId);
        if (category.getName().equalsIgnoreCase(updateCategoryDto.getName())) {
            return categoryMapper.mapToCategoryDto(category);
        }

        if (categoryRepository.findByName(updateCategoryDto.getName()).isPresent()) {
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
        categoryRepository.delete(getCategoryById(categoryId));
    }

    // region Facilities

    /**
     * Получить категорию по её идентификатору.
     *
     * @param categoryId идентификатор категории.
     * @return трансферный объект, содержащий данные о категории.
     */
    private Category getCategoryById(long categoryId) throws CategoryNotFoundException {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    // endregion
}
