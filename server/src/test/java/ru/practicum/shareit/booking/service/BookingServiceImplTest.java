package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingServiceImplTest {

    @Mock
    UserService userService;

    BookingService bookingService;

    BookingMapper bookingMapper;

    @Mock
    BookingRepository bookingRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    User user1;

    Item item1;

    Item item2;

    BookingDto bookingDto;

    Booking booking;

    Booking booking1;

    @BeforeEach
    public void setUp() {

        bookingMapper = new BookingMapper();

        UserMapper userMapper = new UserMapper();

        ItemMapper itemMapper = new ItemMapper();

        bookingService = new BookingServiceImpl(userService, bookingRepository, itemRepository, userRepository);

        UserService userService;
        BookingRepository bookingRepository;
        BookingDto dto;
        ItemRepository itemRepository;
        UserRepository userRepository;


        user1 = new User();
        user1.setName("Andrew");
        user1.setEmail("Andrew@yandex.ru");
        user1.setId(1L);

        item1 = new Item();
        item1.setName("Guitar");
        item1.setDescription("Electro");
        item1.setOwner(user1);
        item1.setAvailable(true);

        item2 = new Item();
        item2.setName("Bass-Guitar");
        item2.setDescription("Cool");
        item2.setOwner(user1);
        item2.setAvailable(false);

        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(5));

        booking = new Booking();
        booking.setItem(item1);
        booking.setBooker(user1);
        booking.setId(1L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(5));

        booking1 = new Booking();
        booking1.setItem(item1);
        booking1.setBooker(user1);
        booking1.setId(2L);
        booking1.setStatus(Status.WAITING);
        booking1.setStart(LocalDateTime.now().minusDays(10));
        booking1.setEnd(LocalDateTime.now().minusDays(5));
    }

    @Test
    @DisplayName("BookingService_createNotUser")
    void testCreateNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.create(1L, bookingDto)
        );
    }

    @Test
    @DisplayName("BookingService_createNotItem")
    void testCreateNotItem() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        assertThrows(
                NotFoundException.class,
                () -> bookingService.create(1L, bookingDto)
        );
    }

    @Test
    @DisplayName("BookingService_createNotAvailable")
    void testCreateNotAvailable() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item2));

        assertThrows(
                BadRequestException.class,
                () -> bookingService.create(1L, bookingDto)
        );
    }

    @Test
    @DisplayName("BookingService_createNotGoodTime")
    void testCreateNotGoodTime() {

        final BookingDto bookingDto;
        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now().plusDays(7));
        bookingDto.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        assertThrows(
                ValidationException.class,
                () -> bookingService.create(1L, bookingDto)
        );
    }

    @Test
    @DisplayName("BookingService_createEqualsTime")
    void testCreateEqualsTime() {

        final LocalDateTime time = LocalDateTime.now().plusDays(5);

        final BookingDto bookingDto;
        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(time);
        bookingDto.setEnd(time);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        assertThrows(
                ValidationException.class,
                () -> bookingService.create(1L, bookingDto)
        );
    }

    @Test
    @DisplayName("BookingService_approvedNotBooking")
    void testApprovedNotBooking() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.approved(1L, 1L, true)
        );
    }

    @Test
    @DisplayName("BookingService_approvedUserNotOwner")
    void testApprovedUserNotOwner() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(
                BadRequestException.class,
                () -> bookingService.approved(2L, 1L, true)
        );
    }

    @Test
    @DisplayName("BookingService_approvedStatusNotWaiting")
    void testApprovedStatusNotWaiting() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2L);
        booking2.setStatus(Status.APPROVED);
        booking2.setStart(LocalDateTime.now().plusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(bookingRepository.findById(2L)).thenReturn(Optional.of(booking2));

        assertThrows(
                ValidationException.class,
                () -> bookingService.approved(1L, 2L, true)
        );
    }

    @Test
    @DisplayName("BookingService_approvedTrue")
    void testApprovedTrue() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        final BookingInfoDto bookingResponce = bookingService.approved(1L, 1L, true);

        assertEquals(Status.APPROVED, bookingResponce.getStatus());
        assertEquals("Andrew", bookingResponce.getBooker().getName());
        assertEquals("Guitar", bookingResponce.getItem().getName());
    }

    @Test
    @DisplayName("BookingService_approvedFalse")
    void testApprovedFalse() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking)).thenReturn(booking);

        final BookingInfoDto bookingResponce = bookingService.approved(1L, 1L, false);

        assertEquals(Status.REJECTED, bookingResponce.getStatus());
        assertEquals("Andrew", bookingResponce.getBooker().getName());
        assertEquals("Guitar", bookingResponce.getItem().getName());
    }

    @Test
    @DisplayName("BookingService_findByIdNotBooking")
    void testFindByIdNotBooking() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getById(1L, 1L)
        );
    }

    @Test
    @DisplayName("BookingService_findByIdUserNotOwner")
    void testFindByIdUserNotOwner() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(
                BadRequestException.class,
                () -> bookingService.getById(2L, 1L)
        );
    }

    @Test
    @DisplayName("BookingService_findById")
    void testFindById() {

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        final BookingInfoDto bookingResponce = bookingService.getById(1L, 1L);

        assertEquals("Andrew", bookingResponce.getBooker().getName());
        assertEquals("Guitar", bookingResponce.getItem().getName());
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdNotUser")
    void testFindAllByUserIdNotUser() {


        assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllByBooker(1L, "all")
        );
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdAll")
    void testFindAllByUserIdAll() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.getAllByBookerIdOrderByStartDesc(1L)).thenReturn(List.of(booking, booking1));

        final List<BookingInfoDto> bookingResponces = bookingService.getAllByBooker(1L, "all");

        assertEquals(2, bookingResponces.size());
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdCurrent")
    void testFindAllByUserIdCurrent() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2L);
        booking2.setStatus(Status.APPROVED);
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.getAllByBookerIdAndCurrentTime(1L)).thenReturn(List.of(booking2));

        final List<BookingInfoDto> bookingResponces = bookingService.getAllByBooker(1L, "CURRENT");

        assertEquals(1, bookingResponces.size());
        assertTrue(bookingResponces.getFirst().getStart().isBefore(LocalDateTime.now()));
        assertTrue(bookingResponces.getFirst().getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdPast")
    void testFindAllByUserIdPast() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.getAllByBookerIdAndPastTime(anyLong()))
                .thenReturn(List.of(booking1));

        final List<BookingInfoDto> bookingResponces = bookingService.getAllByBooker(1L, "PAST");

        assertEquals(1, bookingResponces.size());
        assertTrue(bookingResponces.getFirst().getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdFuture")
    void testFindAllByUserIdFuture() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.getAllByBookerIdAndFutureTime(anyLong()))
                .thenReturn(List.of(booking));

        final List<BookingInfoDto> bookingResponces = bookingService.getAllByBooker(1L, "FUTURE");

        assertEquals(1, bookingResponces.size());
        assertTrue(bookingResponces.getFirst().getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdWaiting")
    void testFindAllByUserIdWaiting() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.getAllByBookerIdAndStatusIs(1L, Status.WAITING))
                .thenReturn(List.of(booking));

        final List<BookingInfoDto> bookingResponces = bookingService.getAllByBooker(1L, "WAITING");

        assertEquals(1, bookingResponces.size());
        assertEquals(Status.WAITING, bookingResponces.getFirst().getStatus());
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdRejected")
    void testFindAllByUserIdRejected() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2L);
        booking2.setStatus(Status.REJECTED);
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(bookingRepository.getAllByBookerIdAndStatusIs(1L, Status.REJECTED))
                .thenReturn(List.of(booking2));

        final List<BookingInfoDto> bookingResponces = bookingService.getAllByBooker(1L, "REJECTED");

        assertEquals(1, bookingResponces.size());
        assertEquals(Status.REJECTED, bookingResponces.getFirst().getStatus());
    }

    @Test
    @DisplayName("BookingService_findAllByUserIdValid")
    void testFindAllByUserIdValid() {


        assertThrows(
                BadRequestException.class,
                () -> bookingService.getAllByBooker(1L, "bla")
        );
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdNotUser")
    void testFindAllByOwnerIdNotUser() {

        assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllByOwner(1L, "all")
        );
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdNotItem")
    void testFindAllByOwnerIdNotItem() {


        assertThrows(
                NotFoundException.class,
                () -> bookingService.getAllByOwner(1L, "all")
        );
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdAll")
    void testFindAllByOwnerIdAll() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdOrderByStartDesc(1L)).thenReturn(List.of(booking, booking1));

        final List<BookingInfoDto> bookingResponces = bookingService.getAllByOwner(1L, "all");

        assertEquals(2, bookingResponces.size());
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdCurrent")
    void testFindAllByOwnerIdCurrent() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2L);
        booking2.setStatus(Status.APPROVED);
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(eq(1L),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(booking2));

        final List<BookingInfoDto> bookingResponces = bookingService.getAllByOwner(1L, "CURRENT");

        assertEquals(1, bookingResponces.size());
        assertTrue(bookingResponces.getFirst().getStart().isBefore(LocalDateTime.now()));
        assertTrue(bookingResponces.getFirst().getEnd().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdPast")
    void testFindAllByOwnerIdPast() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking1));

        final List<BookingInfoDto> bookingResponces = bookingService.getAllByOwner(1L, "PAST");

        assertEquals(1, bookingResponces.size());
        assertTrue(bookingResponces.getFirst().getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdFuture")
    void testFindAllByOwnerIdFuture() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));

        final List<BookingInfoDto> bookingResponces = bookingService.getAllByOwner(1L, "FUTURE");

        assertEquals(1, bookingResponces.size());
        assertTrue(bookingResponces.getFirst().getStart().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdWaiting")
    void testFindAllByOwnerIdWaiting() {

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartDesc(1L, Status.WAITING))
                .thenReturn(List.of(booking));

        final List<BookingInfoDto> bookingResponces = bookingService.getAllByOwner(1L, "WAITING");

        assertEquals(1, bookingResponces.size());
        assertEquals(Status.WAITING, bookingResponces.getFirst().getStatus());
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdRejected")
    void testFindAllByOwnerIdRejected() {

        final Booking booking2 = new Booking();
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setId(2L);
        booking2.setStatus(Status.REJECTED);
        booking2.setStart(LocalDateTime.now().minusDays(2));
        booking2.setEnd(LocalDateTime.now().plusDays(5));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1, item2));
        when(bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartDesc(1L, Status.REJECTED))
                .thenReturn(List.of(booking2));

        final List<BookingInfoDto> bookingResponces = bookingService.getAllByOwner(1L, "REJECTED");

        assertEquals(1, bookingResponces.size());
        assertEquals(Status.REJECTED, bookingResponces.getFirst().getStatus());
    }

    @Test
    @DisplayName("BookingService_findAllByOwnerIdValid")
    void testFindAllByOwnerIdValid() {


        assertThrows(
                BadRequestException.class,
                () -> bookingService.getAllByOwner(1L, "bla")
        );
    }
}