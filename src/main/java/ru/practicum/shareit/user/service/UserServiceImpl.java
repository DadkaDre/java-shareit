package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {

        log.info("Проверка прошла успешно");
        User user = UserMapper.toUser(userDto);
        log.info("Переводим dto в сущность");
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Нет пользователя с таким id"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        user.setId(id);
        return UserMapper.toUserDto(userRepository.save(user));

    }

    @Override
    public UserDto getById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Нет пользователя с таким id"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public Collection<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Нет пользователя с таким id"));
        userRepository.deleteById(user.getId());
    }

    public User getOwner(Long id) {

        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Нет пользователя с таким id"));
    }

}