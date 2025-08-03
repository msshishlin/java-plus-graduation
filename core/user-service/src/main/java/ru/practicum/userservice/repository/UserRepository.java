package ru.practicum.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.userservice.model.User;

import java.util.Optional;

/**
 * Контракт хранилища данных о пользователях.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Найти пользователя по его адресу электронной почты.
     *
     * @param email адрес электронной почты.
     * @return пользователя.
     */
    Optional<User> findByEmail(String email);
}
