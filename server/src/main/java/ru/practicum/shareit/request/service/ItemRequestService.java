package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestShortInfoDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestInfoDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestShortInfoDto> getAllByUser(Long userId);

    List<ItemRequestShortInfoDto> getAll(Long userId);

    ItemRequestShortInfoDto getById(Long requestId);
}
