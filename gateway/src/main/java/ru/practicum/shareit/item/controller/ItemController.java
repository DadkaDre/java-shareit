package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.group.Marker;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {


    private final ItemClient itemClient;


    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(Constants.HEADER) @NotNull Long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        return itemClient.create(userId, itemDto);
    }

    @GetMapping("/{item-id}")
    public ResponseEntity<Object> getById(@RequestHeader(Constants.HEADER) @NotNull Long userId,
                                          @PathVariable("item-id") Long itemId) {
        return itemClient.getById(userId, itemId);
    }


    @PatchMapping("/{item-id}")

    public ResponseEntity<Object> update(@RequestHeader(Constants.HEADER) @NotNull Long userId,
                                         @PathVariable("item-id") Long itemId,
                                         @RequestBody ItemDto itemDto) {

        return itemClient.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@PathVariable @NotNull Long itemId) {

        return itemClient.delete(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader(Constants.HEADER) @NotNull Long userId) {

        return itemClient.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(Constants.HEADER) @NotNull Long userId,
                                         @RequestParam @NotBlank String text) {
        return itemClient.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComments(@RequestHeader(Constants.HEADER) Long userId,
                                              @Validated({Marker.OnCreate.class}) @RequestBody CommentDto commentDto,
                                              @PathVariable @NotNull Long itemId) {
        return itemClient.addComments(userId, itemId, commentDto);
    }
}
