package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestShortInfoDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ItemRequestServiceSBImplTest {

    ItemRequestService itemRequestService;

    ItemService itemService;

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

        final ItemRequestDto itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("Description3");
        itemRequestService.create(1L, itemRequestDto1);

        final ItemRequestDto itemRequestDto2 = new ItemRequestDto();
        itemRequestDto2.setDescription("Description4");
        itemRequestService.create(1L, itemRequestDto2);

        final ItemRequestDto itemRequestDto3 = new ItemRequestDto();
        itemRequestDto3.setDescription("Description5");
        itemRequestService.create(2L, itemRequestDto3);

        final ItemDto itemDto = new ItemDto();
        itemDto.setName("Description5");
        itemDto.setDescription("Cool");
        itemDto.setAvailable(true);
        itemDto.setRequestId(3L);
        itemService.create(1L, itemDto);
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRequestService_saveItemRequest")
    void testSaveItemRequest() {

        final ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description6");

        final ItemRequestInfoDto i = itemRequestService.create(1L, itemRequestDto);

        assertEquals("Andrew", i.getRequestor().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRequestService_getAllByUser")
    void testGetAllByUser() {

        final List<ItemRequestShortInfoDto> i = itemRequestService.getAllByUser(1L);

        assertEquals(2, i.size());
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemRequestService_getAll")
    void testGetAll() {

        final ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Description6");
        itemRequestService.create(2L, itemRequestDto);

        final List<ItemRequestShortInfoDto> i = itemRequestService.getAll(1L);

        assertEquals(2, i.size());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("ItemRequestService_getById")
    void testGetById() {

        final ItemRequestShortInfoDto i = itemRequestService.getById(3L);

        final List<ItemShortDto> items = i.getItems();

        assertEquals("Description5", i.getDescription());
        assertEquals(1, items.size());
        assertEquals("Description5", items.getFirst().getName());
    }
}