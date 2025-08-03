package ru.practicum.categoryservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.categoryservice.service.CategoryService;
import ru.practicum.interactionapi.dto.categoryservice.CategoryDto;
import ru.practicum.interactionapi.dto.categoryservice.CreateCategoryDto;
import ru.practicum.interactionapi.dto.categoryservice.UpdateCategoryDto;
import ru.practicum.interactionapi.exception.categoryservice.CategoryNotFoundException;
import ru.practicum.interactionapi.openfeign.CategoryServiceClient;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@Slf4j
public class CategoryServiceController implements CategoryServiceClient {
    /**
     * Сервис для работы с категориями.
     */
    private final CategoryService categoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryDto createCategory(CreateCategoryDto createCategoryDto) {
        log.info("Create category - {}", createCategoryDto);
        return categoryService.createCategory(createCategoryDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<CategoryDto> getCategories(int from, int size) {
        log.info("Get {} categories starts with {}", size, from);
        return categoryService.getCategories(from, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryDto getCategory(Long categoryId) throws CategoryNotFoundException {
        log.info("Get category with id={}", categoryId);
        return categoryService.getCategory(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CategoryDto updateCategory(Long categoryId, UpdateCategoryDto updateCategoryDto) throws CategoryNotFoundException {
        log.info("Update category with id={} - {}", categoryId, updateCategoryDto);
        return categoryService.updateCategory(categoryId, updateCategoryDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCategory(Long categoryId) throws CategoryNotFoundException {
        log.info("Delete category with id={}", categoryId);
        categoryService.deleteCategory(categoryId);
    }
}
