package ru.practicum.interactionapi.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interactionapi.dto.categoryservice.CategoryDto;

import java.util.Collection;

/**
 * Контракт клиента сервиса для работы с категориями событий.
 */
@FeignClient(name = "category-service")
public interface CategoryServiceClient {
    /**
     * Получить коллекцию категорий.
     *
     * @return коллекция категорий.
     */
    @GetMapping("/categories")
    Collection<CategoryDto> getCategories(@RequestParam(name = "ids") Collection<Long> categoriesIds);

    /**
     * Найти категорию по её идентификатору.
     *
     * @param categoryId идентификатор категории.
     * @return трансферный объект, содержащий данные о категории.
     */
    @GetMapping("/categories/{categoryId}")
    CategoryDto findCategoryById(@PathVariable Long categoryId);
}
