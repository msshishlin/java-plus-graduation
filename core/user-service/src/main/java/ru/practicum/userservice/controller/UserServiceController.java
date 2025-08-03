package ru.practicum.userservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interactionapi.dto.userservice.CreateUserDto;
import ru.practicum.interactionapi.dto.userservice.UserDto;
import ru.practicum.interactionapi.dto.userservice.UserShortDto;
import ru.practicum.interactionapi.exception.userservice.UserNotFoundException;
import ru.practicum.interactionapi.openfeign.UserServiceClient;
import ru.practicum.userservice.service.UserService;

import java.util.Collection;
import java.util.List;

/**
 * Контроллер для работы с пользователями.
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class UserServiceController implements UserServiceClient {
    /**
     * Сервис для сущности "Пользователь".
     */
    private final UserService userService;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        log.info("Create user - {}", createUserDto);
        return userService.createUser(createUserDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserShortDto getUser(Long userId) throws UserNotFoundException {
        log.info("Get user with id={}", userId);
        return userService.getUser(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<UserDto> getUsers(List<Long> userIds, int from, int size) {
        log.info("Get users - userIds: {}, from: {}, size: {}", userIds, from, size);
        return userService.getUsers(userIds, from, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(Long userId) throws UserNotFoundException {
        log.info("Delete user with id={}", userId);
        userService.deleteUser(userId);
    }
}
