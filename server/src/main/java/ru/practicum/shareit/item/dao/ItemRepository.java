package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerId(Long ownerId);


    @Query("""
            SELECT i
            FROM Item i
            WHERE i.available = true
                AND (LOWER(i.name) LIKE LOWER(CONCAT('%', ?1, '%'))
                OR LOWER(i.description) LIKE LOWER(CONCAT('%', ?1, '%')))
            """)
    List<Item> search(String query);

    @Query("""
            Select i
            From Item i
            Where i.available = true
            """)
    List<Item> findAllByAvailable();

    List<Item> findAllByRequest(ItemRequest itemRequest);

}
