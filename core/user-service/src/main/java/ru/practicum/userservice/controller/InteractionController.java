package ru.practicum.userservice.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.dto.userservice.UserDto;
import ru.practicum.interactionapi.exception.userservice.UserNotFoundException;
import ru.practicum.userservice.service.UserService;

import java.util.Collection;

/**
 * Контроллер, содержащий конечные точки доступа для межсервисного взаимодействия.
 */
@RequestMapping("/interaction/users")
@RequiredArgsConstructor
@RestController
@Slf4j
public class InteractionController {
    /**
     * Сервис для работы с пользователями.
     */
    private final UserService userService;

    /**
     * Получить коллекцию пользователей.
     *
     * @param userIds идентификаторы пользователей.
     * @return коллекция пользователей.
     */
    @GetMapping
    public Collection<UserDto> getUsers(@RequestParam(name = "ids") Collection<Long> userIds) {
        log.info("Get users with ids = {}", userIds);
        return userService.getUsers(userIds);
    }

    /**
     * Получить пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return пользователь.
     * @throws UserNotFoundException пользователь с идентификатором {@code userId} не найден.
     */
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable @Positive Long userId) throws UserNotFoundException {
        log.info("Get user with id = {}", userId);
        return userService.getUser(userId);
    }

    /**
     * Проверить существует ли пользователь.
     *
     * @param userId идентификатор пользователя.
     * @return признак существует ли пользователь.
     */
    @GetMapping("/check/existence/by/id/{userId}")
    public boolean isUserExists(@PathVariable Long userId) {
        log.info("Check user with id = {} existence", userId);
        return userService.isUserExists(userId);
    }
}
