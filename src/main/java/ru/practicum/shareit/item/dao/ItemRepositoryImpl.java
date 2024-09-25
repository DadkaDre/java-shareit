package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {

    Map<Long, Map<Long, Item>> ownerItems = new HashMap<>();
    Map<Long, Item> items = new HashMap<>();
    Long seq = 1L;

    @Override
    public Item create(Item item) {
        item.setId(generatedId());
        items.put(item.getId(), item);

        ownerItems.compute(item.getOwner().getId(), (ownerId, ownerItemsMap) -> {
            if (ownerItemsMap == null) {
                ownerItemsMap = new HashMap<>();
            }
            ownerItemsMap.put(item.getId(), item);
            return ownerItemsMap;
        });

        return items.get(item.getId());

    }

    @Override
    public Item getById(Long itemId) {
        return items.get(itemId);

    }

    @Override
    public Item update(Item item) {
        Item oldItem = items.get(item.getId());
        if (item.getName() != null) {
            oldItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            oldItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            oldItem.setAvailable(item.getAvailable());
        }
        items.put(oldItem.getId(), oldItem);
        Map<Long, Item> itemsMap = ownerItems.get(oldItem.getOwner().getId());
        itemsMap.put(oldItem.getId(), oldItem);
        ownerItems.put(oldItem.getOwner().getId(), itemsMap);
        return oldItem;
    }

    @Override
    public List<Item> getOwnerItems(Long userId) {
        return ownerItems.get(userId).values().stream()
                .toList();
    }

    @Override
    public List<Item> search(String query) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains((query.toLowerCase())))
                .toList();
    }


    private Long generatedId() {
        return seq++;
    }
}

