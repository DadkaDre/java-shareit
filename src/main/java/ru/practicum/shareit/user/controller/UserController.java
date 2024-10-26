package ru.practicum.shareit.user.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.group.Marker;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;



@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    @Autowired
    private final UserService userService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        log.info("Переданы данны на создание пользователя {}", userDto);
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    @Validated({Marker.OnUpdate.class})
    public UserDto update(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        log.info("Переданы данные на редактирование: {}; пользователя c id {}", userDto, id);
        return userService.update(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        log.info("Передан запрос на поиск пользователя по id {}", id);
        return userService.getById(id);
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("Получили запрос на список всех пользователей в системе");
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Получили запрос на удаление пользователя по id {}", id);
        userService.delete(id);
    }
}