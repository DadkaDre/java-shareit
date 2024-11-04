package ru.practicum.shareit.item.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentInfoDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemService itemService;

    static final String HEADER = "X-Sharer-User-Id";

    @Test
    @DisplayName("Проверяем контроллер создания предмета")
    void create() throws Exception {

        final ItemDto itemDto = new ItemDto();

        when(itemService.create(anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L)
                        .content("{\"name\": \"coffee machine\", \"description\": \"Italian\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));

        verify(itemService, times(1)).create(anyLong(), any(ItemDto.class));
    }


    @Test
    @DisplayName("Проверяем контроллер по нахождению предмета по id")
    void getById() throws Exception {

        final ItemInfoDto itemInfoDto = new ItemInfoDto();

        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemInfoDto);

        mockMvc.perform(get("/items/1")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemInfoDto.getId()));

        verify(itemService, times(1)).getById(anyLong(), anyLong());
    }


    @Test
    @DisplayName("Проверяем контроллер по обновлению предмета")
    void update() throws Exception {

        final ItemDto itemDto = new ItemDto();

        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1L)
                        .content("{\"name\": \"Updated item\", \"description\": \"Updated description\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));

        verify(itemService, times(1)).update(anyLong(), anyLong(), any(ItemDto.class));
    }


    @Test
    @DisplayName("Проверяем контроллер по удалению предмета")
    void testDelete() throws Exception {

        mockMvc.perform(delete("/items/1")
                        .header(HEADER, 1L))
                .andExpect(status().isOk());

        verify(itemService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Проверяем контроллер по получению предметов по собственнику")
    void testGetOwnerItems() throws Exception {

        final List<ItemInfoDto> items = List.of(new ItemInfoDto());

        when(itemService.getOwnerItems(anyLong())).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemService, times(1)).getOwnerItems(anyLong());
    }


    @Test
    @DisplayName("Проверяем контроллер по поиску предметов")
    void search() throws Exception {

        final List<ItemDto> items = List.of(new ItemDto());

        when(itemService.search(anyString())).thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemService, times(1)).search(anyString());
    }

    @Test
    @DisplayName("Проверяем контроллер по добавлению комментариев")
    void addComments() throws Exception {
        final CommentInfoDto commentInfoDto = new CommentInfoDto();
        commentInfoDto.setText("Comment-bla-bla");

        when(itemService.addComments(anyLong(), any(CommentDto.class), anyLong())).thenReturn(commentInfoDto);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1)
                        .content("{\"text\": \"Comment-bla-bla\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Comment-bla-bla"));

        verify(itemService, times(1)).addComments(anyLong(), any(CommentDto.class), anyLong());
    }
}