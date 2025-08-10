package ru.practicum.interactionapi.openfeign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interactionapi.dto.userservice.UserDto;

import java.util.Collection;

/**
 * Контракт клиента сервиса для работы с пользователями.
 */
@FeignClient(name = "user-service")
public interface UserServiceClient {
    /**
     * Получить коллекцию пользователей.
     *
     * @param userIds идентификаторы пользователей.
     * @return коллекция пользователей.
     */
    @GetMapping("/interaction/users")
    Collection<UserDto> getUsers(@RequestParam(name = "ids") Collection<Long> userIds);

    /**
     * Получить пользователя.
     *
     * @param userId идентификатор пользователя.
     * @return пользователь.
     */
    @GetMapping("/interaction/users/{userId}")
    UserDto getUser(@PathVariable Long userId);

    /**
     * Проверить существует ли пользователь.
     *
     * @param userId идентификатор пользователя.
     * @return признак существует ли пользователь.
     */
    @GetMapping("/interaction/users/check/existence/by/id/{userId}")
    boolean isUserExists(@PathVariable Long userId);
}
