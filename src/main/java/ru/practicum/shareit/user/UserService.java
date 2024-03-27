package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto findById(Long userId);

    Collection<UserDto> findAll();

    UserDto updateById(UserDto userDto);

    void delete(Long userId);
}
