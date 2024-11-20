package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;


public interface UserService {

    UserDto create(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    UserDto getById(Long id);

    Collection<UserDto> getAll();

    void delete(Long id);

    User getOwner(Long id);

}