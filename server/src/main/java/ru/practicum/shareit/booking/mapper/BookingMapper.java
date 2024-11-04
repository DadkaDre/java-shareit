package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto, User user, Item item) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user
        );
    }

    public static BookingInfoDto toBookingInfoDto(Booking booking, User user, Item item) {
        UserDto userdto = UserMapper.toUserDto(user);
        return new BookingInfoDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable()),
                userdto,
                booking.getStatus()
        );
    }
}
