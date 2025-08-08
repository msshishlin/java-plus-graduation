package ru.practicum.userservice.service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.dto.userservice.CreateUserDto;
import ru.practicum.interactionapi.dto.userservice.UserDto;
import ru.practicum.userservice.model.User;

import java.util.Collection;

/**
 * Маппер для сущности пользователя.
 */
@Component
public class UserMapper {
    /**
     * Преобразовать трансферный объект, содержащий данные для добавления нового пользователя, в объект пользователя.
     *
     * @param createUserDto трансферный объект, содержащий данные для добавления нового пользователя.
     * @return объект пользователя.
     */
    public User mapToUser(CreateUserDto createUserDto) {
        return User.builder()
                .name(createUserDto.getName() != null ? createUserDto.getName().trim() : null)
                .email(createUserDto.getEmail() != null ? createUserDto.getEmail().trim() : null)
                .build();
    }

    /**
     * Преобразовать объект пользователя в трансферный объект, содержащий информацию о пользователе.
     *
     * @param user объект пользователя.
     * @return трансферный объект, содержащий информацию о пользователе.
     */
    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    /**
     * Преобразовать коллекцию объектов пользователей в коллекцию трансферных объектов, содержащих информацию о пользователях.
     *
     * @param users коллекция объектов пользователей.
     * @return коллекция трансферных объектов, содержащих информацию о пользователях.
     */
    public Collection<UserDto> mapToUserDtoCollection(Collection<User> users) {
        return users.stream().map(this::mapToUserDto).toList();
    }
}
