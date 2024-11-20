package ru.practicum.shareit.request.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestInfoDto;
import ru.practicum.shareit.request.dto.ItemRequestShortInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ItemRequestService itemRequestService;

    static final String HEADER = "X-Sharer-User-Id";

    @Test
    @DisplayName("Проверяем контроллер по созданию запросов")
    void testCreate() throws Exception {

        final ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Description6");

        final ItemRequestInfoDto responseInfoDto = new ItemRequestInfoDto();
        responseInfoDto.setId(1L);
        responseInfoDto.setDescription("Description6");
        responseInfoDto.setCreated(LocalDateTime.now());

        when(itemRequestService.create(anyLong(), any(ItemRequestDto.class))).thenReturn(responseInfoDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HEADER, 1)
                        .content("{\"description\": \"Bla-bla-bla\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Description6"));

        verify(itemRequestService, times(1))
                .create(anyLong(), any(ItemRequestDto.class));
    }


    @Test
    void getAllByUser() throws Exception {

        final List<ItemRequestShortInfoDto> requests = List.of(new ItemRequestShortInfoDto());

        when(itemRequestService.getAllByUser(anyLong())).thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemRequestService, times(1)).getAllByUser(anyLong());
    }

    @Test
    void getAll() throws Exception {

        final List<ItemRequestShortInfoDto> responceDtos = List.of(new ItemRequestShortInfoDto());

        when(itemRequestService.getAll(anyLong())).thenReturn(responceDtos);

        mockMvc.perform(get("/requests/all")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(itemRequestService, times(1)).getAll(anyLong());
    }

    @Test
    void getById() throws Exception {
        final ItemRequestShortInfoDto request = new ItemRequestShortInfoDto(); // создайте DTO

        when(itemRequestService.getById(anyLong())).thenReturn(request);

        mockMvc.perform(get("/requests/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()));

        verify(itemRequestService, times(1)).getById(anyLong());
    }
}