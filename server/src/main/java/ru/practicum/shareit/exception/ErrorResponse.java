package ru.practicum.shareit.exception;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ErrorResponse {

    private String message;


    public ErrorResponse(String message) {
        this.message = message;
    }

    public Map<String, String> answer(String message) {
        return Map.of("error", message);
    }
}