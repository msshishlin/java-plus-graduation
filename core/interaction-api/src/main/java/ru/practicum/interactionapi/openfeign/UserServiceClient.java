package ru.practicum.interactionapi.openfeign;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.dto.userservice.CreateUserDto;
import ru.practicum.interactionapi.dto.userservice.UserDto;
import ru.practicum.interactionapi.dto.userservice.UserShortDto;
import ru.practicum.interactionapi.exception.userservice.UserNotFoundException;
import ru.practicum.interactionapi.exception.userservice.UserWithSameEmailAlreadyExistsException;

import java.util.Collection;
import java.util.List;

/**
 * Контракт клиента для сервиса управления пользователями.
 */
@FeignClient(name = "user-service")
public interface UserServiceClient {
    /**
     * Добавить нового пользователя.
     *
     * @param createUserDto трансферный объект, содержащий данные для добавления нового пользователя.
     * @return новый пользователь.
     * @throws UserWithSameEmailAlreadyExistsException Адрес электронной почты {@code createUserDto.email} уже занят другим пользователем.
     */
    @PostMapping("/admin/users")
    @ResponseStatus(HttpStatus.CREATED)
    UserDto createUser(@RequestBody @Valid CreateUserDto createUserDto) throws UserWithSameEmailAlreadyExistsException;

    /**
     * Получить пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return пользователь.
     * @throws UserNotFoundException пользователь с идентификатором {@code userId} не найден.
     */
    @GetMapping("/admin/users/{userId}")
    UserShortDto getUser(@PathVariable Long userId) throws UserNotFoundException;

    /**
     * Получить коллекцию пользователей.
     *
     * @param userIds коллекция идентификаторов пользователей, которых надо получить.
     * @param from    количество пользователей, которое необходимо пропустить.
     * @param size    количество пользователей, которое необходимо получить.
     * @return коллекция пользователей.
     */
    @GetMapping("/admin/users")
    Collection<UserDto> getUsers(@RequestParam(name = "ids", required = false) List<Long> userIds,
                                 @RequestParam(defaultValue = "0") @Min(0) int from,
                                 @RequestParam(defaultValue = "10") @Min(1) int size);

    /**
     * Удалить пользователя.
     *
     * @param userId идентификатор пользователя.
     * @throws UserNotFoundException пользователь с идентификатором {@code userId} не найден.
     */
    @DeleteMapping("/admin/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteUser(@PathVariable Long userId) throws UserNotFoundException;
}
