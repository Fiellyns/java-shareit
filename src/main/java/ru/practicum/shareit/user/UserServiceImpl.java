package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserDao userDao, UserMapper userMapper) {
        this.userDao = userDao;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto create(UserDto userDto) {
        return userMapper.toDto(userDao.create(userMapper.toModel(userDto)));
    }

    @Override
    public UserDto findById(Long userId) {
        existsUserById(userId);
        return userMapper.toDto(userDao.findById(userId));
    }

    @Override
    public Collection<UserDto> findAll() {
        return userDao.findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateById(UserDto userDto) {
        User userFromMap = userMapper.toModel(findById(userDto.getId()));
        User userFromDto = userMapper.toModel(userDto);

        userFromMap.setName(Objects.requireNonNullElse(userFromDto.getName(), userFromMap.getName()));
        userFromMap.setEmail(Objects.requireNonNullElse(userFromDto.getEmail(), userFromMap.getEmail()));

        return userMapper.toDto(userDao.update(userFromMap));
    }

    @Override
    public void delete(Long userId) {
        existsUserById(userId);
        userDao.delete(userId);
    }

    @Override
    public void existsUserById(Long userId) {
        if (!userDao.existsUserById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
    }
}
