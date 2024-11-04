package ru.practicum.shareit.booking.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.group.Marker;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@NotNull @RequestHeader(Constants.HEADER) long userId,
                                         @Validated(Marker.OnCreate.class) @RequestBody BookItemRequestDto requestDto) {

        return bookingClient.create(userId, requestDto);
    }

    @PatchMapping("/{booking-Id}")
    public ResponseEntity<Object> approved(@RequestHeader(Constants.HEADER) @NotNull Long userId,
                                           @PathVariable("booking-Id") Long bookingId,
                                           @RequestParam Boolean approved) {
        return bookingClient.approved(userId, bookingId, approved);
    }

    @GetMapping("/{booking-Id}")
    public ResponseEntity<Object> getById(@RequestHeader(Constants.HEADER) @NotNull Long userId,
                                          @PathVariable("booking-Id") Long bookingId) {
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByBooker(@RequestHeader(Constants.HEADER) @NotNull Long userId,
                                                 @RequestParam(defaultValue = "ALL") @NotBlank String state) {

        return bookingClient.getAllByBooker(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestHeader(Constants.HEADER) @NotNull Long ownerId,
                                                @RequestParam(defaultValue = "ALL") @NotBlank String state) {
        return bookingClient.getAllByOwner(ownerId, state);
    }
}
