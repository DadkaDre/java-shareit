package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;

import java.util.List;

public interface BookingService {

    BookingInfoDto create(Long userId, BookingDto bookingDto);

    BookingInfoDto approved(Long userId, Long bookingId, boolean approved);

    BookingInfoDto getById(Long userId, Long bookingId);

    List<BookingInfoDto> getAllByBooker(Long userId, String state);

    List<BookingInfoDto> getAllByOwner(Long ownerId, String state);

}
