package ru.practicum.shareit.item.mapper;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@AllArgsConstructor
public class ItemMapper {


    public static ItemDto itemDto(Item item) {

        if (item.getRequest() == null) {
            return new ItemDto(item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable()
            );
        } else {
            return new ItemDto(item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    item.getRequest().getId());
        }

    }

    public static Item toItem(User user, ItemDto itemDto) {

        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                null
        );
    }

    public static Item toItemWithRequest(User user, ItemDto itemDto, ItemRequest itemRequest) {

        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest
        );
    }

    public static ItemInfoDto itemInfoDto(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
        List<CommentInfoDto> listInfoDto = comments.stream().map(CommentMapper::toCommentInfoDto).toList();
        return new ItemInfoDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                listInfoDto,
                lastBooking,
                nextBooking
        );
    }

    public static ItemShortDto toItemShortDto(Item item) {
        return new ItemShortDto(
                item.getId(),
                item.getName(),
                item.getOwner().getId()
        );
    }
}
