package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestShortInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {

        return new ItemRequest(
                null,
                itemRequestDto.getDescription(),
                user,
                LocalDateTime.now()
        );
    }

    public static ItemRequestInfoDto toItemRequestInfoDto(ItemRequest itemRequest) {
        return new ItemRequestInfoDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                UserMapper.toUserDto(itemRequest.getRequestor()),
                itemRequest.getCreated(),
                Collections.emptyList()

        );
    }

    public static ItemRequestShortInfoDto toItemRequestShortInfoDto(ItemRequestInfoDto itemRequestInfoDto) {
        return new ItemRequestShortInfoDto(
                itemRequestInfoDto.getId(),
                itemRequestInfoDto.getDescription(),
                itemRequestInfoDto.getCreated(),
                itemRequestInfoDto.getItems()
        );
    }
}
