package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final BookingDto dto;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingInfoDto create(Long userId, BookingDto bookingDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Нет такого пользователя"));

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Нет такого предмета"));
        if (!dto.isBeforeEnd(bookingDto)) {
            throw new BadRequestException("Вещь забронирована");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь забронирована");
        }
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);
        bookingRepository.save(booking);
        return BookingMapper.toBookingInfoDto(booking, user, item);

    }

    @Override
    @Transactional
    public BookingInfoDto approved(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Нет такого бронирования"));
        if (!(userId.equals(booking.getItem().getOwner().getId()))) {
            throw new BadRequestException("Только собственник может подтвердить бронирование");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        Booking approvedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingInfoDto(approvedBooking, booking.getBooker(), booking.getItem());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingInfoDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Нет такого бронирования"));
        if (!(userId.equals(booking.getBooker().getId())) && !(userId.equals(booking.getItem().getOwner().getId()))) {
            throw new BadRequestException("Просматривать info может владелец вещи или создатель брони");
        }
        return BookingMapper.toBookingInfoDto(booking, booking.getBooker(), booking.getItem());

    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingInfoDto> getAllByBooker(Long userId, String state) {
        State bookingState = from(state);
        userService.getById(userId);

        List<Booking> bookings;

        bookings = switch (bookingState) {
            case ALL -> bookingRepository.getAllByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.getAllByBookerIdAndCurrentTime(userId);
            case PAST -> bookingRepository.getAllByBookerIdAndPastTime(userId);
            case FUTURE -> bookingRepository.getAllByBookerIdAndFutureTime(userId);
            case WAITING -> bookingRepository.getAllByBookerIdAndStatusIs(userId, Status.WAITING);
            case REJECTED -> bookingRepository.getAllByBookerIdAndStatusIs(userId, Status.REJECTED);
            default -> throw new BadRequestException("Недопустимое значение state");
        };

        return bookings.stream().map(i -> BookingMapper.toBookingInfoDto(i, i.getBooker(), i.getItem())).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingInfoDto> getAllByOwner(Long ownerId, String state) {
        State bookingState = from(state);
        userService.getById(ownerId);

        List<Booking> bookings;

        bookings = switch (bookingState) {
            case ALL -> bookingRepository.findAllByItemOwnerIdOrderByStartDesc(ownerId);
            case CURRENT ->
                    bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, LocalDateTime.now(), LocalDateTime.now());
            case PAST ->
                    bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, LocalDateTime.now());
            case FUTURE ->
                    bookingRepository.findAllByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, LocalDateTime.now());
            case WAITING -> bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartDesc(ownerId, Status.WAITING);
            case REJECTED ->
                    bookingRepository.findAllByItemOwnerIdAndStatusIsOrderByStartDesc(ownerId, Status.REJECTED);
            default -> throw new BadRequestException("Недопустимое значение state");
        };

        return bookings.stream().map(i -> BookingMapper.toBookingInfoDto(i, i.getBooker(), i.getItem())).toList();
    }

    private State from(String state) {
        state = state.toUpperCase();
        for (State value : State.values()) {
            if (value.name().equals(state)) {
                return value;
            } else {
                throw new BadRequestException("Значение не соответствует допустимому");
            }
        }
        return null;
    }
}
