package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotAccessException;
import ru.practicum.shareit.exception.NotFoundException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingDto bookingDto;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.now().plusMinutes(1).withNano(0);
        LocalDateTime end = start.plusMinutes(10);
        bookingCreateDto = BookingCreateDto.builder()
                .itemId(1L)
                .startTime(start)
                .endTime(end)
                .build();
        bookingDto = BookingDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(Status.WAITING)
                .build();
    }

    @AfterEach
    void clean() {
        bookingCreateDto = null;
        bookingDto = null;
    }

    @Test
    void create_whenBookingIsValid_thenStatusIsCreatedAndReturnBookingDto() throws Exception {
        when(bookingService.create(any(BookingCreateDto.class), anyLong()))
                .thenReturn(bookingDto);

        String result = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(bookingCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(bookingDto));
    }

    @Test
    void create_whenStartTimeInPast_thenStatusIBadRequest() throws Exception {
        bookingCreateDto.setStartTime(LocalDateTime.now().minusMinutes(3));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(bookingCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(any(BookingCreateDto.class), anyLong());
    }

    @Test
    void create_whenEndBeforeStart_thenStatusIBadRequest() throws Exception {
        bookingCreateDto.setEndTime(LocalDateTime.now().minusMinutes(3));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(bookingCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(any(BookingCreateDto.class), anyLong());
    }

    @Test
    void create_whenItemIdIsNull_thenStatusIBadRequest() throws Exception {
        bookingCreateDto.setItemId(null);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(bookingCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(any(BookingCreateDto.class), anyLong());
    }

    @Test
    void create_whenUserNotFound_thenStatusIsNotFound() throws Exception {
        when(bookingService.create(any(BookingCreateDto.class), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(bookingCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_whenRequestIsValid_thenStatusIsOkAndReturnUpdatedBookingDto() throws Exception {
        bookingDto.setStatus(Status.APPROVED);
        Long userId = 1L;
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        String result = mvc.perform(patch("/bookings/{bookingId}", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "TRUE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(bookingDto));
    }

    @Test
    void update_whenRequestDataIsNotValid_thenStatusIsNotFound() throws Exception {
        Long userId = 10L;
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(NotFoundException.class);

        mvc.perform(patch("/bookings/{bookingId}", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "TRUE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_whenRequestDataIsNotAccepted_thenStatusIsNotFound() throws Exception {
        Long userId = 10L;
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(NotAccessException.class);

        mvc.perform(patch("/bookings/{bookingId}", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "TRUE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_whenStatusAlreadyChanged_thenStatusIsBadRequest() throws Exception {
        Long userId = 10L;
        when(bookingService.update(anyLong(), anyLong(), anyBoolean()))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(patch("/bookings/{bookingId}", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "TRUE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_whenRequestIsValid_thenStatusIsOk() throws Exception {
        Long userId = 1L;
        when(bookingService.getById(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", userId)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService).getById(anyLong(), anyLong());
    }

    @Test
    void getById_whenUserNotFound_thenStatusIsNotFound() throws Exception {
        Long userId = 1L;
        when(bookingService.getById(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings/{bookingId}", userId)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bookingService).getById(anyLong(), anyLong());
    }

    @Test
    void getById_whenUserIsNotOwner_thenStatusIsNotFound() throws Exception {
        Long userId = 1L;
        when(bookingService.getById(anyLong(), anyLong()))
                .thenThrow(NotAccessException.class);

        mvc.perform(get("/bookings/{bookingId}", userId)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByUserQuery_whenRequestIsValid_thenStatusIsOkAndReturnListOfBookingDto() throws Exception {
        Long userId = 1L;
        when(bookingService.getAllByUserQuery(anyLong(), any(BookingState.class), any(Pageable.class)))
                .thenReturn(List.of(bookingDto));

        String result = mvc.perform(get("/bookings", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "5")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(List.of(bookingDto)));
    }

    @Test
    void getAllByUserQuery_whenUserNotFound_thenStatusIsNotFound() throws Exception {
        Long userId = 1L;
        when(bookingService.getAllByUserQuery(anyLong(), any(BookingState.class), any(Pageable.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "5")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByUserQuery_whenStateIsNotValid_thenStatusIsBadRequest() throws Exception {
        Long userId = 1L;
        when(bookingService.getAllByUserQuery(anyLong(), any(BookingState.class), any(Pageable.class)))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/bookings", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "smth")
                        .param("from", "5")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getAllByUserQuery(anyLong(), any(BookingState.class), any(Pageable.class));
    }

    @Test
    void getAllByUserQuery_whenParamFromIsNotValid_thenStatusIsBadRequest() throws Exception {
        Long userId = 1L;

        mvc.perform(get("/bookings", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getAllByUserQuery(anyLong(), any(BookingState.class), any(Pageable.class));
    }

    @Test
    void getAllByUserQuery_whenParamSizeIsNotValid_thenStatusIsBadRequest() throws Exception {
        Long userId = 1L;

        mvc.perform(get("/bookings", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "5")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getAllByUserQuery(anyLong(), any(BookingState.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerQuery_whenRequestIsValid_thenStatusIsOkAndReturnListOfBookingDto() throws Exception {
        Long userId = 1L;
        when(bookingService.getAllByOwnerQuery(anyLong(), any(BookingState.class), any(Pageable.class)))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "5")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllByOwnerQuery_whenUserNotFound_thenStatusIsNotFound() throws Exception {
        Long userId = 10L;
        when(bookingService.getAllByOwnerQuery(anyLong(), any(BookingState.class), any(Pageable.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/bookings/owner", userId)
                        .header("X-Sharer-User-Id", 10)
                        .param("state", "ALL")
                        .param("from", "5")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByOwnerQuery_whenStateIsNotValid_thenStatusIsBadRequest() throws Exception {
        Long userId = 1L;
        when(bookingService.getAllByOwnerQuery(anyLong(), any(BookingState.class), any(Pageable.class)))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(get("/bookings/owner", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "smth")
                        .param("from", "5")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getAllByOwnerQuery(anyLong(), any(BookingState.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerQuery_whenParamFromIsNotValid_thenStatusIsBadRequest() throws Exception {
        Long userId = 1L;

        mvc.perform(get("/bookings/owner", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "-1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getAllByOwnerQuery(anyLong(), any(BookingState.class), any(Pageable.class));
    }

    @Test
    void getAllByOwnerQuery_whenParamSizeIsNotValid_thenStatusIsBadRequest() throws Exception {
        Long userId = 1L;

        mvc.perform(get("/bookings/owner", userId)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", "5")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).getAllByOwnerQuery(anyLong(), any(BookingState.class), any(Pageable.class));
    }
}
