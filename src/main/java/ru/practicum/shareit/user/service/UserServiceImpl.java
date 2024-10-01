package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.ValidationUser;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.Collection;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ValidationUser validation;

    @Override
    public UserDto create(UserDto userDto) {
        log.info("Проверяем данные на корректность {}", userDto);
        validation.checkUserFields(userDto);
        log.info("Проверка прошла успешно");
        User user = UserMapper.toUser(userDto);
        log.info("Переводим dto в сущность");
        return UserMapper.toUserDto(userRepository.create(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        log.info("Проверяем данные на корректность id {}, данные {}", id, userDto);
        validation.checkUserId(id);
        checkUserById(id);
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        return UserMapper.toUserDto(userRepository.update(user));
    }

    @Override
    public UserDto getById(Long id) {
        log.info("Проверяем корректность ввода id пользователя {}", id);
        validation.checkUserId(id);
        log.info("Проверяем наличие пользователя по id {} в базе данных", id);
        checkUserById(id);
        User user = userRepository.getById(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public Collection<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public void delete(Long id) {
        log.info("Проверяем корректность ввода id пользователя {}", id);
        validation.checkUserId(id);
        log.info("Проверяем наличие пользователя по id {} в базе данных", id);
        checkUserById(id);
        userRepository.delete(id);
    }

    public User getOwner(Long id) {
        checkUserById(id);
        return userRepository.getById(id);
    }

    private void checkUserById(Long id) {
        if (userRepository.getById(id) == null) {
            throw new NotFoundException("Пользователь по id не найден");
        }
    }
}