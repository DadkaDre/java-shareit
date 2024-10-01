package ru.practicum.shareit.user.dao;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {


    Map<Long, User> users = new HashMap<>();
    private Long seq = 1L;

    @Override
    public User create(User user) {
        user.setId(generatedId());
        checkEmail(user);
        users.put(user.getId(), user);

        return users.get(user.getId());
    }

    @Override
    public User update(User user) {
        User oldUser = users.get(user.getId());
        if (!(user.getName() == null)) {
            oldUser.setName(user.getName());
        }
        if (!(user.getEmail() == null)) {
            checkEmail(user);
            oldUser.setEmail(user.getEmail());
        }
        return oldUser;
    }


    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values().stream().toList();
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    private Long generatedId() {
        return seq++;
    }

    private void checkEmail(User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email зарегистрирован в системе");
        }
    }
}
