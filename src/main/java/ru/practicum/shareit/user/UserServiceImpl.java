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

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto create(UserDto userDto) {
        return userMapper.toDto(userRepository.save(userMapper.toModel(userDto)));
    }

    @Override
    public UserDto findById(Long userId) {
        return userMapper.toDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден")));
    }

    @Override
    public Collection<UserDto> findAll() {
        return userRepository.findAll()
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

        return userMapper.toDto(userRepository.save(userFromMap));
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

}
