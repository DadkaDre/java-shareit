package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestShortInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestInfoDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = UserMapper.toUser(userService.getById(userId));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);

        return ItemRequestMapper.toItemRequestInfoDto(repository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestShortInfoDto> getAllByUser(Long userId) {

        List<ItemRequestShortInfoDto> list = new ArrayList<>();
        List<ItemRequest> requestsList = repository.findAllByRequestorId(userId);
        List<Item> itemslist = (itemRepository.findAll());
        for (ItemRequest request : requestsList) {
            List<ItemShortDto> sortList = itemslist.stream()
                    .filter(item -> item.getRequest() != null)
                    .filter(item -> item.getRequest().equals(request))
                    .map(ItemMapper::toItemShortDto)
                    .toList();
            ItemRequestInfoDto itemRequestInfoDto = ItemRequestMapper.toItemRequestInfoDto(request);
            ItemRequestShortInfoDto itemRequestShortInfoDto = ItemRequestMapper.toItemRequestShortInfoDto(itemRequestInfoDto);
            itemRequestShortInfoDto.setItems(sortList);
            list.add(itemRequestShortInfoDto);
        }
        return list.stream().sorted(Comparator.comparing(ItemRequestShortInfoDto::getCreated).reversed()).toList();

    }

    @Override
    public List<ItemRequestShortInfoDto> getAll(Long userId) {

        List<ItemRequestShortInfoDto> list = new ArrayList<>();
        List<ItemRequest> listRequestsFromDao = repository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        for (ItemRequest i : listRequestsFromDao) {
            ItemRequestShortInfoDto itemRequestShortInfoDto = ItemRequestMapper.toItemRequestShortInfoDto(ItemRequestMapper.toItemRequestInfoDto(i));
            list.add(itemRequestShortInfoDto);
        }
        return list.stream().sorted(Comparator.comparing(ItemRequestShortInfoDto::getCreated).reversed()).toList();
    }

    @Override
    public ItemRequestShortInfoDto getById(Long requestId) {
        log.info("Запрос на получение запроса с id = {} и все ответы на него", requestId);
        ItemRequest itemRequest = repository.findById(requestId).orElseThrow(() -> new NotFoundException("Запроса с id = {} нет." + requestId));
        ItemRequestShortInfoDto itemRequestShortInfoDto = ItemRequestMapper.toItemRequestShortInfoDto(ItemRequestMapper.toItemRequestInfoDto(itemRequest));
        List<ItemShortDto> items = itemRepository.findAllByRequest(itemRequest)
                .stream().map(ItemMapper::toItemShortDto).toList();
        itemRequestShortInfoDto.setItems(items);

        log.info("Получен запрос с id = {} и все ответы на него", requestId);
        return itemRequestShortInfoDto;

    }
}
