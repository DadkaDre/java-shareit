package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ForBiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    UserService userService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    CommentRepository commentRepository;

    @Mock
    BookingRepository bookingRepository;

    ItemService itemService;

    User user1;
    User user2;
    UserDto userDto;
    ItemRequest itemRequest1;
    ItemDto itemDto1;
    Item item1;

    Item item2;

    CommentDto commentDto1;

    Comment comment;
    Booking booking;
    Booking booking2;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, commentRepository, itemRequestRepository);

        user1 = new User();
        user1.setId(1L);
        user1.setName("Andrew");
        user1.setEmail("Andrew@yandex.ru");

        user2 = new User();
        user2.setId(2L);
        user2.setName("John");
        user2.setEmail("john@example.com");

        userDto = new UserDto();
        userDto.setName("Andrew");
        userDto.setEmail("Andrew@yandex.ru");

        itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Cool Guitar");
        itemRequest1.setRequestor(user1);
        itemRequest1.setCreated(LocalDateTime.now());

        itemDto1 = new ItemDto();
        itemDto1.setName("GuitarDto");
        itemDto1.setDescription("Fire");
        itemDto1.setAvailable(false);

        item1 = new Item();
        item1.setName("Bass-Guitar");
        item1.setDescription("Cool");
        item1.setOwner(user1);
        item1.setAvailable(true);
        item1.setId(1L);

        item2 = new Item();
        item2.setName("Bass-Guitar");
        item2.setDescription("Cool");
        item2.setOwner(user1);
        item2.setAvailable(true);
        item2.setRequest(itemRequest1);

        commentDto1 = new CommentDto();
        commentDto1.setText("Description6");
        commentDto1.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setItem(item1);
        comment.setText("Description6");
        comment.setAuthor(user1);
        comment.setCreated(LocalDateTime.now());

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item1);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));

        booking2 = new Booking();
        booking2.setId(1L);
        booking2.setItem(item1);
        booking2.setBooker(user1);
        booking2.setStart(LocalDateTime.now().minusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(1));
        booking2.setStatus(Status.APPROVED);

    }

    @Test
    @DisplayName("Создание элемента с действующим запросом (владелец)")
    void testCreateWithValidRequest() {
        itemDto1.setRequestId(1L);

        when(userService.getById(1L)).thenReturn(userDto);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest1));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto createdItemDto = itemService.create(1L, itemDto1);

        assertEquals("GuitarDto", createdItemDto.getName());
        assertEquals(1L, createdItemDto.getRequestId());
    }

    @Test
    @DisplayName("Создание элемента с отсутствующим запросом")
    void testCreateWithNonExistentRequest() {
        itemDto1.setRequestId(1L);

        when(userService.getById(1L)).thenReturn(userDto);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.empty()); // Запрос не найден

        assertThrows(NotFoundException.class, () -> itemService.create(1L, itemDto1));
    }

    @Test
    @DisplayName("Создание элемента без запроса")
    void testCreateWithoutRequest() {
        when(userService.getById(1L)).thenReturn(userDto);
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ItemDto createdItemDto = itemService.create(1L, itemDto1);

        assertEquals("GuitarDto", createdItemDto.getName());
        assertNull(createdItemDto.getRequestId());
    }

    @Test
    @DisplayName("Обновление несуществующего элемента")
    void testUpdateNonExistentItem() {
        when(userService.getOwner(1L)).thenReturn(user1);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty()); // Элемент не найден

        assertThrows(NotFoundException.class, () -> itemService.update(1L, 1L, itemDto1));
    }

    @Test
    @DisplayName("Успешное обновление элемента")
    void testUpdateItemSuccessfully() {
        when(userService.getOwner(1L)).thenReturn(user1);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0)); // Возвращаем сам объект

        ItemDto updatedItemDto = itemService.update(1L, 1L, itemDto1);

        assertEquals("GuitarDto", updatedItemDto.getName());
        assertEquals("Fire", updatedItemDto.getDescription());
        assertEquals(false, updatedItemDto.getAvailable());
    }

    @Test
    @DisplayName("Обновление элемента без прав владельца")
    void testUpdateItemWithoutOwnership() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("John");

        when(userService.getOwner(2L)).thenReturn(anotherUser);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        assertThrows(ForBiddenException.class, () -> itemService.update(2L, 1L, itemDto1));
    }

    @Test
    @DisplayName("Успешное получение элемента по id")
    void testGetByIdSuccessfully() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(commentRepository.findAllByItemId(1L)).thenReturn(List.of(comment));
        when(bookingRepository.findAllByItemId(1L)).thenReturn(List.of(booking));

        ItemInfoDto itemInfoDto = itemService.getById(1L, 1L);

        assertEquals("Bass-Guitar", itemInfoDto.getName());
        assertEquals("Cool", itemInfoDto.getDescription());
        assertEquals(1, itemInfoDto.getComments().size());
        assertEquals("Description6", itemInfoDto.getComments().get(0).getText());
    }

    @Test
    @DisplayName("Получение несуществующего элемента")
    void testGetByIdNonExistentItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(1L, 1L));
    }

    @Test
    @DisplayName("Получение элемента без комментариев и бронирований")
    void testGetByIdWithoutCommentsAndBookings() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(commentRepository.findAllByItemId(1L)).thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemId(1L)).thenReturn(Collections.emptyList());

        ItemInfoDto itemInfoDto = itemService.getById(1L, 1L);

        assertEquals("Bass-Guitar", itemInfoDto.getName());
        assertEquals("Cool", itemInfoDto.getDescription());
        assertEquals(0, itemInfoDto.getComments().size());
    }

    @Test
    @DisplayName("ItemService_findByIdNotItem")
    void testFindByIdNotItem() {

        assertThrows(
                NotFoundException.class,
                () -> itemService.getById(2L, 5L)
        );
    }

    @Test
    @DisplayName("ItemService_deleteNotItem")
    void testDeleteNotItem() {

        assertThrows(
                NotFoundException.class,
                () -> itemService.delete(5L)
        );
    }

    @Test
    @DisplayName("ItemService_delete")
    void testDelete() {

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));

        itemService.delete(1L);

        verify(itemRepository).delete(any(Item.class));
    }

    @Test
    @DisplayName("Успешное получение предметов владельца")
    void testGetOwnerItemsSuccessfully() {
        when(userService.getOwner(1L)).thenReturn(user1);
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1));
        when(bookingRepository.findAllByIdIn(List.of(item1.getId()))).thenReturn(List.of(booking));
        when(commentRepository.findAllByItemIdIn(List.of(item1.getId()))).thenReturn(List.of(comment));

        List<ItemInfoDto> itemInfoDtos = itemService.getOwnerItems(1L);

        assertEquals(1, itemInfoDtos.size());
        assertEquals("Bass-Guitar", itemInfoDtos.get(0).getName());
        assertEquals("Cool", itemInfoDtos.get(0).getDescription());
        assertEquals(1, itemInfoDtos.get(0).getComments().size());
        assertEquals("Description6", itemInfoDtos.get(0).getComments().get(0).getText());
    }

    @Test
    @DisplayName("Получение предметов владельца без предметов")
    void testGetOwnerItemsWithoutItems() {
        when(userService.getOwner(1L)).thenReturn(user1);
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(Collections.emptyList());

        List<ItemInfoDto> itemInfoDtos = itemService.getOwnerItems(1L);

        assertEquals(0, itemInfoDtos.size());
    }

    @Test
    @DisplayName("Получение предметов владельца без комментариев и бронирований")
    void testGetOwnerItemsWithoutCommentsAndBookings() {
        when(userService.getOwner(1L)).thenReturn(user1);
        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item1));
        when(bookingRepository.findAllByIdIn(List.of(item1.getId()))).thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemIdIn(List.of(item1.getId()))).thenReturn(Collections.emptyList());

        List<ItemInfoDto> itemInfoDtos = itemService.getOwnerItems(1L);

        assertEquals(1, itemInfoDtos.size());
        assertEquals("Bass-Guitar", itemInfoDtos.get(0).getName());
        assertEquals("Cool", itemInfoDtos.get(0).getDescription());
        assertEquals(0, itemInfoDtos.get(0).getComments().size());
    }

    @Test
    @DisplayName("ItemService_searchTextEmpty")
    void testSearchTextEmpty() {

        final List<ItemDto> itemDtos = itemService.search("");

        assertTrue(itemDtos.isEmpty());
    }

    @Test
    @DisplayName("ItemService_searchText")
    void testSearchText() {

        when(itemRepository.search("as")).thenReturn(List.of(item1));

        final List<ItemDto> itemDtos = itemService.search("as");

        assertEquals("Bass-Guitar", itemDtos.getFirst().getName());
    }

    @Test
    @DisplayName("Успешное добавление комментария")
    void testAddCommentSuccessfully() {
        when(userService.getById(1L)).thenReturn(userDto);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(bookingRepository.getALLByItemIdAndBookerIdAndStatusEqualsOrderByEndDesc(1L, 1L, Status.APPROVED))
                .thenReturn(Collections.singletonList(booking2));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentInfoDto commentInfoDto = itemService.addComments(1L, commentDto1, 1L);

        assertEquals("Description6", commentInfoDto.getText());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Ошибка при добавлении комментария без брони")
    void testAddCommentWithoutBooking() {
        when(userService.getById(1L)).thenReturn(userDto);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(bookingRepository.getALLByItemIdAndBookerIdAndStatusEqualsOrderByEndDesc(1L, 1L, Status.APPROVED))
                .thenReturn(Collections.emptyList());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            itemService.addComments(1L, commentDto1, 1L);
        });

        assertNull(exception.getMessage());
    }
}