package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ForBiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ValidationItem;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.ValidationUser;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ValidationUser validationUser;
    @Autowired
    private ValidationItem validationItem;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        log.info("Проверяем наличие id пользователя");
        validationUser.checkUserId(userId);
        log.info("Проверяем наличие пользователя в системе по заданному id {}", userId);
        userService.getById(userId);
        log.info("Проверяем корректность поле переданных в запросе {}", itemDto);
        validationItem.checkItemFields(itemDto);
        UserDto userDto = userService.getById(userId);
        User user = UserMapper.toUser(userDto);
        Item item = ItemMapper.toItem(user, itemDto);
        return ItemMapper.itemDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Проверяем id пользователя");
        validationUser.checkUserId(userId);
        log.info("Проверяем наличие пользователя в системе по заданному id {}", userId);
        userService.getById(userId);
        log.info("Проверяем id предмета {}", itemId);
        validationItem.checkItemId(itemId);
        log.info("Проверяем в базе наличие предмета по id {}", itemId);
        checkItemById(itemId);
        checkOwner(userId, itemId);
        User owner = userService.getOwner(userId);
        Item item = ItemMapper.toItem(owner, itemDto);
        item.setId(itemId);
        return ItemMapper.itemDto(itemRepository.update(item));

    }

    @Override
    public ItemDto getById(Long itemId) {
        validationItem.checkItemId(itemId);
        checkItemById(itemId);
        return ItemMapper.itemDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ItemDto> getOwnerItems(Long userId) {
        validationUser.checkUserId(userId);
        userService.getOwner(userId);
        return itemRepository.getOwnerItems(userId).stream().map(ItemMapper::itemDto).toList();
    }

    @Override
    public List<ItemDto> search(String query) {
        if (query.isEmpty() || query.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.search(query).stream()
                .map(ItemMapper::itemDto)
                .toList();
    }

    private void checkItemById(Long itemId) {
        if (itemRepository.getById(itemId) == null) {
            throw new NotFoundException("Предмета по id " + itemId + " не существует");
        }
    }

    private void checkOwner(Long userId, Long itemId) {
        if (!userId.equals(itemRepository.getById(itemId).getOwner().getId())) {
            throw new ForBiddenException("Только собственники предметов имеют доступ");
        }
    }
}
