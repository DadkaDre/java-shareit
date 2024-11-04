package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {


    private final ItemService itemService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(Constants.HEADER) Long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("Получили данные для создания предмета {} у пользователя по id {}", itemDto, userId);
        return itemService.create(userId, itemDto);
    }

    @GetMapping("/{item-id}")
    public ItemInfoDto getById(@RequestHeader(Constants.HEADER) Long userId,
                               @PathVariable("item-id") Long itemId) {
        log.info("Получен запрос на вывод предмета по id {}", itemId);
        return itemService.getById(itemId, userId);
    }


    @PatchMapping("/{item-id}")
    public ItemDto update(@RequestHeader(Constants.HEADER) Long userId, @PathVariable("item-id") Long itemId, @RequestBody ItemDto itemDto) {
        log.info("Переданы данные на редактирование предмета по id: {}, пользователя по id: {}, данные {}", userId, itemId, itemDto);
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable @NotNull Long itemId) {

        itemService.delete(itemId);
    }


    @GetMapping
    public List<ItemInfoDto> getOwnerItems(@RequestHeader(Constants.HEADER) Long userId) {
        log.info("Получаем список предметов по юзеру с id {}", userId);
        return itemService.getOwnerItems(userId);
    }


    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Получили данные для поиска {}", text);
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentInfoDto addComments(@RequestHeader(Constants.HEADER) Long userId,
                                      @Valid @RequestBody CommentDto commentDto,
                                      @PathVariable Long itemId) {
        return itemService.addComments(userId, commentDto, itemId);
    }
}
