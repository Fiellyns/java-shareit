package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailExistException;

import java.util.*;

@Slf4j
@Component
public class UserDaoImpl implements UserDao {
    private final Map<Long, User> userMap = new HashMap<>();

    private final Set<String> usersEmailSet = new HashSet<>();

    private Long idNext = 1L;

    private Long getIdNext() {
        return idNext++;
    }

    @Override
    public User create(User user) {
        if (!usersEmailSet.add(user.getEmail())) {
            throw new EmailExistException("Пользователь с таким E-mail= " + user.getEmail() + " уже существует!");
        }
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
    public User update(User user) {
        User updatingUser = userMap.get(user.getId());
        if (!user.getEmail().equalsIgnoreCase(updatingUser.getEmail())) {
            if (!usersEmailSet.add(user.getEmail())) {
                throw new EmailExistException("Пользователь с таким E-mail= " + user.getEmail() + " уже существует!");
            }
            usersEmailSet.remove(updatingUser.getEmail());
        }
        userMap.put(user.getId(), user);
        log.info("Пользователь с id: {} был обновлён", user.getId());
        return user;
    }

    @Override
    public void delete(Long id) {
        usersEmailSet.remove(userMap.get(id).getEmail());
        userMap.remove(id);
        log.info("Пользователь с id: {} удалён", id);
    }

    @Override
    public boolean existsUserById(Long userId) {
        return userMap.containsKey(userId);
    }

}