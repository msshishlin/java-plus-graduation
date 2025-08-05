package ru.practicum.categoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.categoryservice.model.Category;

import java.util.Optional;

/**
 * Контракт хранилища данных для категорий.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Найти категорию по её названию.
     *
     * @param categoryName название категории.
     * @return категория.
     */
    Optional<Category> findByName(String categoryName);
}
