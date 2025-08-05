package ru.practicum.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.userservice.model.User;

/**
 * Контракт хранилища данных о пользователях.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Проверить существует ли в БД пользователь с таким адресом электронной почты.
     *
     * @param email адрес электронной почты.
     * @return пользователя.
     */
    boolean existsByEmail(String email);
}
