package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ItemServiceSBImplTest {

    ItemService itemService;

    UserService userService;

    BookingService bookingService;

    ItemRepository itemRepository;

    UserRepository userRepository;

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

        final UserDto userDto3 = new UserDto();
        userDto3.setName("Andrew");
        userDto3.setEmail("andrew@mail.ru");
        userService.create(userDto3);

        final ItemDto itemDto1 = new ItemDto();
        itemDto1.setName("Bass-Guitar");
        itemDto1.setDescription("Cool");
        itemDto1.setAvailable(true);
        itemService.create(1L, itemDto1);

        final ItemDto itemDto2 = new ItemDto();
        itemDto2.setName("Guitar");
        itemDto2.setDescription("Electro");
        itemDto2.setAvailable(true);
        itemService.create(1L, itemDto2);

        final ItemDto itemDto3 = new ItemDto();
        itemDto3.setName("Spoons");
        itemDto3.setDescription("Silver");
        itemDto3.setAvailable(true);
        itemService.create(2L, itemDto3);

        final BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().minusDays(6));
        bookingDto.setEnd(LocalDateTime.now().minusDays(2));
        bookingService.create(2L, bookingDto);
        bookingService.approved(1L, 1L, true);
    }


    @Test
    @DirtiesContext
    @DisplayName("ItemService_update")
    void testUpdate() {

        final ItemDto itemDto = new ItemDto();
        itemDto.setName("TestName");
        itemDto.setDescription("TestDesc");
        itemDto.setAvailable(true);
        itemService.create(1L, itemDto);

        itemDto.setName("TestName1");
        itemDto.setDescription("TestDesc1");
        itemService.update(1L, 4L, itemDto);

        assertEquals("TestName1", itemDto.getName());
        assertEquals("TestDesc1", itemDto.getDescription());
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemService_getItemsByOwnerId")
    void testGetItemsByOwnerId() {

        assertEquals(2, itemService.getOwnerItems(1L).size());
        assertEquals(1, itemService.getOwnerItems(2L).size());
    }

    @Test
    @DirtiesContext
    @DisplayName("ItemService_addComments")
    void testAddComments() {

        final CommentDto commentDto1 = new CommentDto();
        commentDto1.setText("Text-test");

        final CommentInfoDto commentDto2 = itemService.addComments(2L, commentDto1, 1L);

        assertEquals("Text-test", commentDto2.getText());
        assertEquals("Elena", commentDto2.getAuthorName());
    }
}