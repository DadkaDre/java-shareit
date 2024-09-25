package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    User create(User user);

    User update(User user);

    User getById(Long id);

    Collection<User> getAll();

    void delete(Long id);
}