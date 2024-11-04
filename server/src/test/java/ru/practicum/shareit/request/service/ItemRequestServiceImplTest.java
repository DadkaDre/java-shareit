package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestServiceImplTest {

    ItemRequestService itemRequestService;

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserRepository userRepository;
    @Mock
    UserService userService;

    @Mock
    ItemRepository itemRepository;

    @Mock
    ItemMapper itemMapper;

    final Sort sort = Sort.by(Sort.Direction.DESC, "created");

    User user1;

    UserDto userDto;

    ItemRequestDto itemRequestDto1;

    ItemRequest itemRequest1;

    ItemRequest itemRequest2;

    Item item1;

    Item item2;

    @BeforeEach
    public void setUp() {

        ItemRequestMapper itemRequestMapper = new ItemRequestMapper();

        UserMapper userMapper = new UserMapper();

        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService, itemRepository);

        itemRequestDto1 = new ItemRequestDto();
        itemRequestDto1.setDescription("Description");

        user1 = new User();
        user1.setName("Andrew");
        user1.setEmail("Andrew@yandex.ru");
        user1.setId(1L);
        userRepository.save(user1);

        itemRequest1 = new ItemRequest();
        itemRequest1.setId(1L);
        itemRequest1.setDescription("Description");
        itemRequest1.setCreated(LocalDateTime.now());
        itemRequest1.setRequestor(user1);

        itemRequest2 = new ItemRequest();
        itemRequest2.setId(2L);
        itemRequest2.setDescription("Description2");
        itemRequest2.setCreated(LocalDateTime.now());
        itemRequest2.setRequestor(user1);

        item1 = new Item();
        item1.setName("Guitar");
        item1.setDescription("Electro");
        item1.setOwner(user1);
        item1.setAvailable(true);
        item1.setRequest(itemRequest1);

        item2 = new Item();
        item2.setName("Bass-Guitar");
        item2.setDescription("Cool");
        item2.setOwner(user1);
        item2.setAvailable(true);
        item2.setRequest(itemRequest2);

        ItemShortDto i1 = new ItemShortDto(1L, "Guitar", 1L);

    }

    @Test
    @DisplayName("ItemRequestService_create")
    void testCreate() {

        ItemRequestInfoDto ItemRequestInfoDto = new ItemRequestInfoDto();
        ItemRequestInfoDto.setId(itemRequest1.getId());
        ItemRequestInfoDto.setDescription(itemRequest1.getDescription());
        ItemRequestInfoDto.setRequestor(new UserDto(user1.getId(), user1.getName(), user1.getEmail()));

        when(userService.getById(1L)).thenReturn(UserMapper.toUserDto(user1));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest1);

        ItemRequestInfoDto actualDto = itemRequestService.create(1L, itemRequestDto1);

        assertNotNull(actualDto);
        assertEquals(ItemRequestInfoDto.getDescription(), actualDto.getDescription());
        assertEquals(ItemRequestInfoDto.getRequestor().getName(), actualDto.getRequestor().getName());

        verify(userService).getById(1L);
        verify(itemRequestRepository).save(any(ItemRequest.class));
    }

    @Test
    @DisplayName("ItemRequestService_getAllByUser")
    void testGetAllByUser() {

        when(itemRequestRepository.findAllByRequestorId(1L))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        when(itemRepository.findAll()).thenReturn(List.of(item1, item2));

        final List<ItemRequestShortInfoDto> itemRequestResponseDtoList = itemRequestService.getAllByUser(1L);
        assertEquals(2, itemRequestResponseDtoList.size());
    }

    @Test
    @DisplayName("ItemRequestService_getAll")
    void testGetAll() {

        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(2L))
                .thenReturn(List.of(itemRequest1, itemRequest2));

        final List<ItemRequestShortInfoDto> itemRequestResponceDtoList = itemRequestService.getAll(2L);

        assertEquals(2, itemRequestResponceDtoList.size());

    }

    @Test
    @DisplayName("ItemRequestService_getByIdNotRequest")
    void testGetByIdNotRequest() {

        assertThrows(
                NotFoundException.class,
                () -> itemRequestService.getById(1L)
        );
    }

    @Test
    @DisplayName("ItemRequestService_getById")
    void testGetById() {

        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest1));
        when(itemRepository.findAllByRequest(itemRequest1)).thenReturn(List.of(item1));

        final ItemRequestShortInfoDto itemRequestResponceDto = itemRequestService.getById(1L);

        assertEquals("Description", itemRequestResponceDto.getDescription());
        assertEquals(1, itemRequestResponceDto.getItems().size());
    }
}