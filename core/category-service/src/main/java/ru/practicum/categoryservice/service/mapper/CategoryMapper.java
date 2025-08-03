package ru.practicum.categoryservice.service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.categoryservice.model.Category;
import ru.practicum.interactionapi.dto.categoryservice.CategoryDto;
import ru.practicum.interactionapi.dto.categoryservice.CreateCategoryDto;

import java.util.Collection;

/**
 * Маппер для сущности категории.
 */
@Component
public class CategoryMapper {
    /**
     * Преобразовать трансферный объект, содержащий данные для добавления новой категории, в объект категории.
     *
     * @param createCategoryDto трансферный объект, содержащий данные для добавления новой категории.
     * @return объект категории.
     */
    public Category mapToCategory(CreateCategoryDto createCategoryDto) {
        return Category.builder()
                .name(createCategoryDto.getName() != null ? createCategoryDto.getName().trim() : null)
                .build();
    }

    /**
     * Преобразовать объект категории в трансферный объект, содержащий данные о категории.
     *
     * @param category объект категории.
     * @return трансферный объект, содержащий данные о категории.
     */
    public CategoryDto mapToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    /**
     * Преобразовать коллекцию объектов категорий в коллекцию трансферных объектов, содержащих информацию о категориях.
     *
     * @param categories коллекцию объектов категорий.
     * @return коллекция трансферных объектов, содержащих информацию о категориях.
     */
    public Collection<CategoryDto> mapToCategoryDtoCollection(Collection<Category> categories) {
        return categories.stream().map(this::mapToCategoryDto).toList();
    }
}
