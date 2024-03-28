package ru.practicum.shareit.user;

import java.util.Collection;
import java.util.Optional;

public interface UserDao {
    User create(User user);

    Collection<User> findAll();

    Optional<User> findById(Long id);

    User update(User user);

    void delete(Long id);
}
