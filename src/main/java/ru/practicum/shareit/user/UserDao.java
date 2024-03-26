package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserDao {
    User create(User user);

    Collection<User> findAll();

    User findById(Long id);

    User update(User user);

    void delete(Long id);

    boolean existsUserById(Long userId);

}
