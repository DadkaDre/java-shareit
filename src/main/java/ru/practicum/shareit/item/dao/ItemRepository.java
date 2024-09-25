package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Item item);

    Item getById(Long Item);

    Item update(Item item);

    List<Item> getOwnerItems(Long userId);

    List<Item> search(String quarry);
}