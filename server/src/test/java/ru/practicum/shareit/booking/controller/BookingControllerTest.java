package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
class BookingControllerTest {

    @MockBean
    BookingService bookingService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    BookingDto bookingDto;

    BookingInfoDto bookingInfoDto;

    static final String HEADER = "X-Sharer-User-Id";

    @BeforeEach
    public void setUp() {

        bookingInfoDto = new BookingInfoDto();
        bookingInfoDto.setId(1L);
        bookingInfoDto.setStatus(Status.WAITING);

        bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    @DisplayName("Проверяем создание бронирования")
    void testCreateBooking() throws Exception {

        when(bookingService.create(anyLong(), any(BookingDto.class)))
                .thenReturn(bookingInfoDto);

        final String bookingRequestJson = objectMapper.writeValueAsString(bookingInfoDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingRequestJson)
                        .header(HEADER, 1))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("Проверяем создание бронирования")
    void testApproved() throws Exception {

        bookingInfoDto.setStatus(Status.APPROVED);

        when(bookingService.approved(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingInfoDto);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Проверяем поиск бронирования по id")
    void testGetById() throws Exception {

        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingInfoDto);

        mockMvc.perform(get("/bookings/1")
                        .header(HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }


    @Test
    void getAllByBooker() throws Exception {
        when(bookingService.getAllByBooker(anyLong(), anyString()))
                .thenReturn(Collections.singletonList(bookingInfoDto));

        mockMvc.perform(get("/bookings")
                        .header(HEADER, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }


    @Test
    void getAllByOwner() throws Exception {
        when(bookingService.getAllByOwner(anyLong(), anyString()))
                .thenReturn(Collections.singletonList(bookingInfoDto));

        mockMvc.perform(get("/bookings/owner")
                        .header(HEADER, 1)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
