package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {

    @Future(message = "Старт не может быть в прошлом времени")
    LocalDateTime start;
    @Future(message = "Время окончания не может быть в прошлом")
    LocalDateTime end;

    Long itemId;

    @AssertTrue
    public Boolean isBeforeEnd(BookingDto bookingDto) {

        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        return start.isBefore(end);
    }
}
