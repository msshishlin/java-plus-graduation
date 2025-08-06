package ru.practicum.categoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.categoryservice.model.Category;

/**
 * Контракт хранилища данных о категориях.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * Проверить существует ли в БД категория с таким названием.
     *
     * @param categoryName название категории.
     * @return категория.
     */
    boolean existsByName(String categoryName);
}
