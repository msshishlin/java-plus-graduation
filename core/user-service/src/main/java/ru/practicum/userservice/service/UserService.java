package ru.practicum.userservice.service;

import ru.practicum.interactionapi.dto.userservice.CreateUserDto;
import ru.practicum.interactionapi.dto.userservice.UserDto;
import ru.practicum.interactionapi.exception.userservice.UserNotFoundException;
import ru.practicum.interactionapi.exception.userservice.UserWithSameEmailAlreadyExistsException;

import java.util.Collection;

/**
 * Контракт сервиса для работы с пользователями.
 */
public interface UserService {
    /**
     * Добавить нового пользователя.
     *
     * @param createUserDto трансферный объект, содержащий данные для добавления нового пользователя.
     * @return новый пользователь.
     * @throws UserWithSameEmailAlreadyExistsException Адрес электронной почты {@code createUserDto.email} уже занят другим пользователем.
     */
    UserDto createUser(CreateUserDto createUserDto) throws UserWithSameEmailAlreadyExistsException;

    /**
     * Получить коллекцию пользователей.
     *
     * @param userIds коллекция идентификаторов пользователей, которых надо получить.
     * @param from    количество пользователей, которое необходимо пропустить.
     * @param size    количество пользователей, которое необходимо получить.
     * @return коллекция пользователей.
     */
    Collection<UserDto> getUsers(Collection<Long> userIds, int from, int size);

    /**
     * Проверить существует ли пользователь.
     *
     * @param userId идентификатор пользователя.
     * @return признак существует ли пользователь.
     */
    boolean isUserExists(Long userId);

    /**
     * Найти пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return пользователь.
     * @throws UserNotFoundException пользователь с идентификатором {@code userId} не найден.
     */
    UserDto findUserById(Long userId) throws UserNotFoundException;

    /**
     * Удалить пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @throws UserNotFoundException пользователь с идентификатором {@code userId} не найден.
     */
    void deleteUserById(Long userId) throws UserNotFoundException;
}
