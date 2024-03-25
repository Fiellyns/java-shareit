package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    User create(User user);

    Collection<User> findAll();

    User findById(Long id);

    User update(User user, Long userId);

    void delete(Long id);

    void existsUserById(Long userId);

    void emailIsNotUnique(User user, Long userId);
}
