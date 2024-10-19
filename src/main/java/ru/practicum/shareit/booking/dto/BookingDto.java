package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.group.Marker;

import java.time.LocalDateTime;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {

    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    LocalDateTime start;
    @NotBlank(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    LocalDateTime end;
    @NotNull(groups = {Marker.OnCreate.class, Marker.OnUpdate.class})
    Long itemId;

    @AssertTrue
    public Boolean isBeforeEnd(BookingDto bookingDto) {

        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        return start.isBefore(end);
    }
}
