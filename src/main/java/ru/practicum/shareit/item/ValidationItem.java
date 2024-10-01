package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;

@Component
public class ValidationItem {

    @SneakyThrows
    public void checkItemFields(ItemDto item) {

        if (item.getAvailable() == null) {
            throw new BadRequestException("Отсутствует поле avaliable");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            throw new BadRequestException("Отсутствует поле name");
        }
        if (item.getDescription() == null) {
            throw new BadRequestException("Отсутствует поле description");
        }

    }

    @SneakyThrows
    public void checkItemId(Long itemId) {
        if (itemId == null) {
            throw new BadRequestException("id предмета не указан");
        }
    }
}