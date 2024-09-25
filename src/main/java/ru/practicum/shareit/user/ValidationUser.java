package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;



@Component
public class ValidationUser {

    private UserRepository userRepository;

    @SneakyThrows
    public void checkUserFields(UserDto userDto) {
        if (userDto == null) {
            throw new BadRequestException("Данные отсутствуют");
        }

        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new BadRequestException("Имя пользователя не задано");
        }

        if (userDto.getEmail() == null || userDto.getEmail().isBlank() || !(userDto.getEmail().contains("@"))) {
            throw new BadRequestException("почта не может быть пустой или не содержать символ @");
        }
    }

    @SneakyThrows
    public void checkUserId(Long userDtoId) {
        if (userDtoId == null) {
            throw new BadRequestException("id пользователя не указан");
        }
    }
}