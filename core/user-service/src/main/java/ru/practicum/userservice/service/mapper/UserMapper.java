package ru.practicum.userservice.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.interactionapi.dto.userservice.CreateUserDto;
import ru.practicum.interactionapi.dto.userservice.UserDto;
import ru.practicum.interactionapi.dto.userservice.UserShortDto;
import ru.practicum.userservice.model.User;

import java.util.Collection;

/**
 * Маппер для сущности пользователя.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {
    /**
     * Преобразовать трансферный объект, содержащий данные для добавления нового пользователя, в объект пользователя.
     *
     * @param createUserDto трансферный объект, содержащий данные для добавления нового пользователя.
     * @return объект пользователя.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", expression = "java(createUserDto.getName() != null ? createUserDto.getName().trim() : null)")
    @Mapping(target = "email", expression = "java(createUserDto.getEmail() != null ? createUserDto.getEmail().trim() : null)")
    User mapToUser(CreateUserDto createUserDto);

    /**
     * Преобразовать объект пользователя в трансферный объект, содержащий информацию о пользователе.
     *
     * @param user объект пользователя.
     * @return трансферный объект, содержащий информацию о пользователе.
     */
    UserDto mapToUserDto(User user);

    /**
     * Преобразовать объект пользователя в трансферный объект, содержащий краткую информацию о пользователе.
     *
     * @param user объект пользователя.
     * @return трансферный объект, содержащий краткую информацию о пользователе.
     */
    UserShortDto mapToUserShortDto(User user);

    /**
     * Преобразовать коллекцию объектов пользователей в коллекцию трансферных объектов, содержащих информацию о пользователях.
     *
     * @param users коллекция объектов пользователей.
     * @return коллекция трансферных объектов, содержащих информацию о пользователях.
     */
    Collection<UserDto> mapToUserDtoCollection(Collection<User> users);
}
