package ru.practicum.categoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.categoryservice.model.Category;

/**
 * Контракт хранилища данных для категорий.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
