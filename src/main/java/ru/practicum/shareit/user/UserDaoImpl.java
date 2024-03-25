package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserDaoImpl implements UserDao {
    private final Map<Long, User> userMap = new HashMap<>();
    private Long idNext = 1L;

    private Long getIdNext() {
        return idNext++;
    }

    @Override
    public User create(User user) {
        user.setId(getIdNext());
        userMap.put(user.getId(), user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        log.info("Возвращены все пользователи");
        return userMap.values();
    }

    @Override
    public User findById(Long userId) {
        return userMap.get(userId);
    }

    @Override
    public User update(User user, Long userId) {
        user.setId(userId);
        userMap.put(userId, user);
        log.info("Пользователь с id: {} был обновлён", userId);
        return user;
    }

    @Override
    public void delete(Long id) {
        log.info("Пользователь с id: {} удалён", id);
        userMap.remove(id);
    }

    @Override
    public void existsUserById(Long userId) {
        if (!userMap.containsKey(userId)) {
            log.warn("Пользователь с id: {} не найден", userId);
            throw new UserNotFoundException("id: " + userId);
        }
    }

    @Override
    public void emailIsNotUnique(User user, Long userId) {
        List<User> userWithSameEmail = findAll()
                .stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .filter(u -> !Objects.equals(u.getId(), userId))
                .collect(Collectors.toList());
        if (!userWithSameEmail.isEmpty()) {
            throw new EmailExistException("Пользователь с таким E-mail= " + user.getEmail() + " уже существует!");
        }
    }
}