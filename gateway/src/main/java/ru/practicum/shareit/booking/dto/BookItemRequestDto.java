package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.group.Marker;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookItemRequestDto {

    @NotNull(groups = Marker.OnCreate.class)
    long itemId;

    @FutureOrPresent(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnCreate.class)
    LocalDateTime start;

    @Future(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnCreate.class)
    LocalDateTime end;
}