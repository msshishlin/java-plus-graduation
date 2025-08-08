package ru.practicum.userservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.dto.userservice.CreateUserDto;
import ru.practicum.interactionapi.dto.userservice.UserDto;
import ru.practicum.interactionapi.exception.userservice.UserNotFoundException;
import ru.practicum.interactionapi.exception.userservice.UserWithSameEmailAlreadyExistsException;
import ru.practicum.userservice.service.UserService;

import java.util.Collection;

/**
 * Контроллер для работы с пользователями (API администратора).
 */
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@RestController
@Slf4j
public class AdminUserController {
    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Добавить нового пользователя.
     *
     * @param createUserDto трансферный объект, содержащий данные для добавления нового пользователя.
     * @return новый пользователь.
     * @throws UserWithSameEmailAlreadyExistsException Адрес электронной почты {@code createUserDto.email} уже занят другим пользователем.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid CreateUserDto createUserDto) throws UserWithSameEmailAlreadyExistsException {
        log.info("Create user - {}", createUserDto);
        return userService.createUser(createUserDto);
    }

    /**
     * Получить коллекцию пользователей.
     *
     * @param userIds коллекция идентификаторов пользователей, которых надо получить.
     * @param from    количество пользователей, которое необходимо пропустить.
     * @param size    количество пользователей, которое необходимо получить.
     * @return коллекция пользователей.
     */
    @GetMapping
    public Collection<UserDto> getUsers(@RequestParam(name = "ids", required = false) Collection<Long> userIds,
                                        @RequestParam(defaultValue = "0") @Min(0) int from,
                                        @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Get users - userIds: {}, from: {}, size: {}", userIds, from, size);
        return userService.getUsers(userIds, from, size);
    }

    /**
     * Получить пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return пользователь.
     * @throws UserNotFoundException пользователь с идентификатором {@code userId} не найден.
     */
    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable @Positive Long userId) throws UserNotFoundException {
        log.info("Find user with id={}", userId);
        return userService.findUserById(userId);
    }

    /**
     * Удалить пользователя.
     *
     * @param userId идентификатор пользователя.
     * @throws UserNotFoundException пользователь с идентификатором {@code userId} не найден.
     */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable @Positive Long userId) throws UserNotFoundException {
        log.info("Delete user with id={}", userId);
        userService.deleteUserById(userId);
    }
}
