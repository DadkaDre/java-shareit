package ru.practicum.shareit.booking.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    BookingRepository repository;

    @BeforeEach
    public void ready() {
        User user = new User(1l, "Andrew", "Andrew@mail.ru");
        User user2 = new User(2l, "Alex", "Alex@mail.ru");
        User user3 = new User(3l, "Roman", "Roman@mail.ru");

        Item item = new Item(1L, "Гитара", "Электро-Гитара", true, user, null);
        Item item2 = new Item(2L, "Бас-Гитара", "Мощь", true, user2, null);
        Item item3 = new Item(3L, "Барабаны", "Огонь", true, user3, null);

        BookingDto bookingDto = new BookingDto(LocalDateTime.now(), LocalDateTime.now().plusDays(1), 1L);


    }

    @Test
    void getAllByBookerIdAndCurrentTimeEmptyList() {

        List<Booking> bookings = repository.getAllByBookerIdAndCurrentTime(1L);
        assertTrue(bookings.isEmpty());
    }
}


