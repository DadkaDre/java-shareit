package ru.practicum.shareit.booking.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    @Autowired
    private final BookingService service;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingInfoDto create(@RequestHeader(Constants.HEADER) Long userId,
                                 @RequestBody BookingDto bookingDto) {
        log.info("Переданы данные от юзера {} на бронирование {}", userId, bookingDto);
        return service.create(userId, bookingDto);
    }

    @PatchMapping("/{booking-Id}")
    public BookingInfoDto approved(@RequestHeader(Constants.HEADER) Long userId,
                                   @PathVariable("booking-Id") Long bookingId,
                                   @RequestParam Boolean approved) {
        return service.approved(userId, bookingId, approved);
    }

    @GetMapping("/{booking-Id}")
    public BookingInfoDto getById(@RequestHeader(Constants.HEADER) Long userId,
                                  @PathVariable("booking-Id") Long bookingId) {
        return service.getById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingInfoDto> getAllByBooker(@RequestHeader(Constants.HEADER) Long userId,
                                               @RequestParam(defaultValue = "ALL") String state) {
        return service.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getAllByOwner(@RequestHeader(Constants.HEADER) @NotNull Long ownerId,
                                              @RequestParam(defaultValue = "ALL") String state) {
        return service.getAllByOwner(ownerId, state);
    }
}
