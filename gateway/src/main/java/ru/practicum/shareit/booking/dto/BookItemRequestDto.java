package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.group.Marker;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

    @NotNull(groups = Marker.OnCreate.class)
    private long itemId;

    @FutureOrPresent(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnCreate.class)
    private LocalDateTime start;

    @Future(groups = Marker.OnCreate.class)
    @NotNull(groups = Marker.OnCreate.class)
    private LocalDateTime end;
}