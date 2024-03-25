package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        User user = userMapper.toUser(userDto);
        userDao.emailIsNotUnique(user, user.getId());
        return userMapper.toUserDto(userDao.create(user));
    }

    @Override
    public UserDto findById(Long userId) {
        userDao.existsUserById(userId);
        return userMapper.toUserDto(userDao.findById(userId));
    }

    @Override
    public Collection<UserDto> findAll() {
        return userDao.findAll()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateById(UserDto userDto, Long userId) {
        User userFromMap = userMapper.toUser(findById(userId));
        User userFromDto = userMapper.toUser(userDto);

        userDao.emailIsNotUnique(userFromDto, userId);

        userFromMap.setName(Objects.requireNonNullElse(userFromDto.getName(), userFromMap.getName()));
        userFromMap.setEmail(Objects.requireNonNullElse(userFromDto.getEmail(), userFromMap.getEmail()));

        return userMapper.toUserDto(userDao.update(userFromMap, userId));
    }

    @Override
    public void delete(Long userId) {
        userDao.existsUserById(userId);
        userDao.delete(userId);
    }
}
