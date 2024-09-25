package ru.practicum.shareit.item.service;

import org.apache.coyote.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto) throws BadRequestException;

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDto getById(Long itemId);

    List<ItemDto> getOwnerItems(Long userId);

    List<ItemDto> search(String query);

}
