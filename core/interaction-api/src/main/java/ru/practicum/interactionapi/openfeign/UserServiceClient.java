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
     * @param userIds коллекция идентификаторов пользователей, которых надо получить.
     * @return коллекция пользователей.
     */
    @GetMapping("/admin/users")
    Collection<UserDto> getUsers(@RequestParam(name = "ids") Collection<Long> userIds);

    /**
     * Получить пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя.
     * @return пользователь.
     */
    @GetMapping("/admin/users/{userId}")
    UserDto findUserById(@PathVariable Long userId);
}
