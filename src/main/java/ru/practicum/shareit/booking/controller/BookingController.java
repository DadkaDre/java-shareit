package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    @Autowired
    private final BookingService service;

    static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingInfoDto create(@NotNull @RequestHeader(HEADER) Long userId,
                                 @Valid @RequestBody BookingDto bookingDto) {

        return service.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingInfoDto approved(@RequestHeader(HEADER) @NotNull Long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved) {
        return service.approved(userId, bookingId, approved);
    }

    @GetMapping("/{booking-Id}")
    public BookingInfoDto getById(@RequestHeader(HEADER) @NotNull Long userId,
                                  @PathVariable("booking-Id") Long bookingId) {
        return service.getById(userId, bookingId);
    }

    @GetMapping()
    public List<BookingInfoDto> getAllByBooker(@RequestHeader(HEADER) @NotNull Long userId,
                                               @RequestParam(defaultValue = "ALL") @NotBlank String state) {
        return service.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingInfoDto> getAllByOwner(@RequestHeader(HEADER) @NotNull Long ownerId,
                                              @RequestParam(defaultValue = "ALL") @NotBlank String state) {
        return service.getAllByBooker(ownerId, state);
    }
}
