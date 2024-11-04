package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class UserServiceImplSBTest {

    UserService userService;

    @BeforeEach
    public void setUp() {

        final UserDto userDto1 = new UserDto();
        userDto1.setName("Andrew");
        userDto1.setEmail("andrew@List.ru");
        userService.create(userDto1);

        final UserDto userDto2 = new UserDto();
        userDto2.setName("Elena");
        userDto2.setEmail("elena@List.ru");
        userService.create(userDto2);

        final UserDto userDto = new UserDto();
        userDto.setName("Andrew");
        userDto.setEmail("andrew@mail.ru");
        userService.create(userDto);
    }

    @Test
    @DirtiesContext
    @DisplayName("UserService_create")
    void testCreate() {

        assertEquals(3, userService.getAll().size());
    }

    @Test
    @DirtiesContext
    @DisplayName("UserService_update")
    void testUpdate() {

        final UserDto userDto1 = new UserDto();
        userDto1.setName("NeAndrew");
        userDto1.setEmail("neMidnight@mail.ru");
        userService.update(3L, userDto1);

        assertEquals("NeAndrew", userService.getById(3L).getName());
        assertEquals("neMidnight@mail.ru", userService.getById(3L).getEmail());

        final UserDto userDto2 = new UserDto();
        userDto2.setName("Ania");
        userService.update(3L, userDto2);

        assertEquals("Ania", userService.getById(3L).getName());

        final UserDto userDto3 = new UserDto();
        userDto3.setEmail("night@mail.ru");
        userService.update(3L, userDto3);

        assertEquals("night@mail.ru", userService.getById(3L).getEmail());
    }

    @Test
    @DirtiesContext
    @DisplayName("UserService_getById")
    void testGetById() {

        assertEquals("Andrew", userService.getById(1L).getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("UserService_delete")
    void testDelete() {

        userService.delete(3L);

        assertEquals(2, userService.getAll().size());
    }
}