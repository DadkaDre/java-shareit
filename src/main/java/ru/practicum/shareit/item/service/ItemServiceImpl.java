package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForBiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {

        User user = UserMapper.toUser(userService.getById(userId));
        Item item = ItemMapper.toItem(user, itemDto);
        return ItemMapper.itemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {

        log.info("Проверяем наличие пользователя в системе по заданному id {}", userId);

        User user = userService.getOwner(userId);

        log.info("Проверяем в базе наличие предмета по id {}", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет такого предмета"));
        checkOwner(userId, itemId);

        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.itemDto(itemRepository.save(item));
    }

    @Override
    public ItemInfoDto getById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет предмета по id"));
        Long ownerId = item.getOwner().getId();
        List<Comment> comments = commentRepository.findAllByItemId(item.getId());
        List<Booking> bookings = bookingRepository.findAllByItemIdAndEndBefore(itemId, LocalDateTime.now());
        Booking lastBooking = getLastBooking(bookings);
        Booking nextBooking = getNextBooking(bookings);
        return ItemMapper.itemInfoDto(item, lastBooking, nextBooking, comments);
    }

    @Override
    public List<ItemInfoDto> getOwnerItems(Long userId) {

        User user = userService.getOwner(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Long> itemsId = items.stream().map(Item::getId).toList();
        List<Booking> bookings = bookingRepository.findAllByIdInAndEndBefore(itemsId, LocalDateTime.now());
        List<Comment> comments = commentRepository.findAllByItemIdIn(itemsId);
        Map<Long, List<Booking>> bookingsMapByItemsId = new HashMap<>();
        Map<Long, List<Comment>> commentsMapByItemsID = new HashMap<>();
        for (Booking booking : bookings) {
            bookingsMapByItemsId.computeIfAbsent(booking.getItem().getId(), k -> new ArrayList<>()).add(booking);
        }
        for (Comment comment : comments) {
            commentsMapByItemsID.computeIfAbsent(comment.getItem().getId(), c -> new ArrayList<>()).add(comment);
        }
        List<ItemInfoDto> itemInfoDto = new ArrayList<>();

        for (Item item : items) {
            List<Comment> listComments = commentsMapByItemsID.getOrDefault(item.getId(), new ArrayList<>());
            List<Booking> itemBookings = bookingsMapByItemsId.getOrDefault(item.getId(), new ArrayList<>());
            Booking lastBooking = getLastBooking(itemBookings);
            Booking nextBooking = getNextBooking(itemBookings);

            ItemInfoDto infoDto = ItemMapper.itemInfoDto(item, lastBooking, nextBooking, listComments);
            itemInfoDto.add(infoDto);
        }
        return itemInfoDto;


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

    @Override
    public CommentInfoDto addComments(Long userId, CommentDto commentDto, Long itemId) {
        User user = UserMapper.toUser(userService.getById(userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет такого предмета в базе"));
        List<Booking> bookings = bookingRepository.getALLByItemIdAndBookerIdAndStatusIsOrderByEndDesc(itemId, userId, Status.APPROVED);
        if (bookings == null) {
            throw new BadRequestException("Пользователь не бронировал вещь");
        }


        if (bookings.getFirst().getStart().isBefore(LocalDateTime.now())) {
            Comment comment = CommentMapper.toComment(commentDto, user, item);
            return CommentMapper.toCommentInfoDto(commentRepository.save(comment));
        } else {
            throw new BadRequestException("Нет подходящей брони по параметрам");
        }
    }

    private Booking getLastBooking(List<Booking> bookings) {
        if (bookings.isEmpty() || bookings.size() == 1) {
            return null;
        }
        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getStart() != null)
                .max(Comparator.comparing(Booking::getStart));
        return lastBooking.orElse(null);
    }

    private Booking getNextBooking(List<Booking> bookings) {
        if (bookings.isEmpty() || bookings.size() == 1) {
            return null;
        }
        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getEnd() != null)
                .max(Comparator.comparing(Booking::getEnd));
        return lastBooking.orElse(null);
    }

    private void checkOwner(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Нет такого предмета по id"));
        if (!(userId.equals(item.getOwner().getId()))) {
            throw new ForBiddenException("Только собственники предметов имеют доступ");
        }
    }
}
