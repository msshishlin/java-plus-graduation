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
     * @param categoriesIds идентификаторы категорий.
     * @return коллекция категорий.
     */
    @GetMapping("/interaction/categories")
    Collection<CategoryDto> getCategories(@RequestParam(name = "ids") Collection<Long> categoriesIds);

    /**
     * Получить категорию.
     *
     * @param categoryId идентификатор категории.
     * @return трансферный объект, содержащий данные о категории.
     */
    @GetMapping("/interaction/categories/{categoryId}")
    CategoryDto getCategory(@PathVariable Long categoryId);

    /**
     * Проверить существует ли категория.
     *
     * @param categoryId идентификатор категории.
     * @return признак существует ли категория.
     */
    @GetMapping("/interaction/categories/check/existence/by/id/{categoryId}")
    boolean isCategoryExists(@PathVariable Long categoryId);
}
