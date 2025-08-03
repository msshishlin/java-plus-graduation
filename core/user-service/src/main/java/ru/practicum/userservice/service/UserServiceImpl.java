package ru.practicum.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.interactionapi.dto.userservice.CreateUserDto;
import ru.practicum.interactionapi.dto.userservice.UserDto;
import ru.practicum.interactionapi.dto.userservice.UserShortDto;
import ru.practicum.interactionapi.exception.userservice.UserNotFoundException;
import ru.practicum.interactionapi.exception.userservice.UserWithSameEmailAlreadyExistsException;
import ru.practicum.interactionapi.pageable.PageOffset;
import ru.practicum.userservice.repository.UserRepository;
import ru.practicum.userservice.service.mapper.UserMapper;

import java.util.Collection;

/**
 * Сервис управления пользователями.
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    /**
     * Хранилище данных о пользователях.
     */
    private final UserRepository userRepository;

    /**
     * Маппер для сущности пользователя.
     */
    private final UserMapper userMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDto createUser(CreateUserDto createUserDto) throws UserWithSameEmailAlreadyExistsException {
        if (userRepository.findByEmail(createUserDto.getEmail()).isPresent()) {
            throw new UserWithSameEmailAlreadyExistsException(createUserDto.getEmail());
        }

        return userMapper.mapToUserDto(userRepository.save(userMapper.mapToUser(createUserDto)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserShortDto getUser(Long userId) throws UserNotFoundException {
        return userMapper.mapToUserShortDto(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<UserDto> getUsers(Collection<Long> userIds, int from, int size) {
        if (userIds != null && !userIds.isEmpty()) {
            return userMapper.mapToUserDtoCollection(userRepository.findAllById(userIds));
        }

        return userMapper.mapToUserDtoCollection(userRepository.findAll(PageOffset.of(from, size)).getContent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteUser(Long userId) throws UserNotFoundException {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        userRepository.deleteById(userId);
    }
}
