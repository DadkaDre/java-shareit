package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class BookingServiceSBImplTest {

    BookingService bookingService;

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

        final UserDto userDto3 = new UserDto();
        userDto3.setName("Ekaterina");
        userDto3.setEmail("ekaterina@mail.ru");
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

        final BookingDto bookingRequest = new BookingDto();
        bookingRequest.setItemId(2L);
        bookingRequest.setStart(LocalDateTime.now().plusDays(1));
        bookingRequest.setEnd(LocalDateTime.now().plusDays(3));
        bookingService.create(2L, bookingRequest);

        final BookingDto bookingRequest2 = new BookingDto();
        bookingRequest2.setItemId(3L);
        bookingRequest2.setStart(LocalDateTime.now().plusDays(10));
        bookingRequest2.setEnd(LocalDateTime.now().plusDays(13));
        bookingService.create(2L, bookingRequest2);
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingService_saveRequest")
    void testSaveRequest() {

        final BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(3));


        final BookingInfoDto bookingInfoDto = bookingService.create(1L, bookingDto);

        assertEquals("Bass-Guitar", bookingInfoDto.getItem().getName());
        assertEquals("Andrew", bookingInfoDto.getBooker().getName());
        assertEquals(Status.WAITING, bookingInfoDto.getStatus());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingService_approved")
    void testApproved() {

        final BookingInfoDto bookingInfoDto = bookingService.approved(1L, 1L, true);

        assertEquals(Status.APPROVED, bookingInfoDto.getStatus());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingService_findById")
    void testFindById() {

        final BookingInfoDto bookingInfoDto = bookingService.getById(1L, 1L);

        final BookingInfoDto bookingInfoDto2 = bookingService.getById(2L, 1L);

        assertEquals(bookingInfoDto, bookingInfoDto2);
        assertEquals("Guitar", bookingInfoDto.getItem().getName());
        assertEquals("Guitar", bookingInfoDto2.getItem().getName());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingService_findAllByUserId")
    void testFindAllByUserId() {

        final List<BookingInfoDto> bookingInfoDto = bookingService.getAllByBooker(2L, "all");
        assertEquals(2, bookingInfoDto.size());
    }

    @Test
    @DirtiesContext
    @DisplayName("BookingService_findAllByOwnerId")
    void testFindAllByOwnerId() {

        final List<BookingInfoDto> bookingInfoDto = bookingService.getAllByOwner(1L, "all");

        assertEquals(1, bookingInfoDto.size());
    }
}