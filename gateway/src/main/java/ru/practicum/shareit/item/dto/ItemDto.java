package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.group.Marker;


@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    Long id;

    @NotBlank(message = "Наименование вещи отсутствует.")
    String name;

    @NotBlank(message = "Наименование вещи отсутствует.", groups = Marker.OnCreate.class)
    String description;

    @NotNull(groups = Marker.OnCreate.class)
    Boolean available;

    Long requestId;
}