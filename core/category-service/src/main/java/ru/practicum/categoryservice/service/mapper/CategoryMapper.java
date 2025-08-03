package ru.practicum.categoryservice.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.categoryservice.model.Category;
import ru.practicum.interactionapi.dto.categoryservice.CategoryDto;
import ru.practicum.interactionapi.dto.categoryservice.CreateCategoryDto;

import java.util.Collection;

/**
 * Маппер для сущности категории.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    /**
     * Преобразовать трансферный объект, содержащий данные для добавления новой категории, в объект категории.
     *
     * @param createCategoryDto трансферный объект, содержащий данные для добавления новой категории.
     * @return объект категории.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", expression = "java(createCategoryDto.getName() != null ? createCategoryDto.getName().trim() : null)")
    Category mapToCategory(CreateCategoryDto createCategoryDto);

    /**
     * Преобразовать объект категории в трансферный объект, содержащий данные о категории.
     *
     * @param category объект категории.
     * @return трансферный объект, содержащий данные о категории.
     */
    CategoryDto mapToCategoryDto(Category category);

    /**
     * Преобразовать коллекцию объектов категорий в коллекцию трансферных объектов, содержащих информацию о категориях.
     *
     * @param categories коллекцию объектов категорий.
     * @return коллекция трансферных объектов, содержащих информацию о категориях.
     */
    Collection<CategoryDto> mapToCategoryDtoCollection(Collection<Category> categories);
}
