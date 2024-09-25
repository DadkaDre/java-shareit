package ru.practicum.shareit.exception;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {
    @NotBlank
    private String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}