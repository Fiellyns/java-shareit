package ru.practicum.shareit.request;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService requestService;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        LocalDateTime created = LocalDateTime.now();
        requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(created)
                .build();
    }

    @AfterEach
    void clean() {
        requestDto = null;
    }

    @Test
    void create_whenRequestIsValid_thenStatusIsCreatedAndReturnSavedRequest() throws Exception {
        when(requestService.create(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(requestDto);

        String result = mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(requestDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(requestDto));
    }

    @Test
    void create_whenRequestIsNotValid_thenStatusIsBadRequest() throws Exception {
        requestDto.setDescription("");

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(requestDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).create(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void create_whenUserNotFound_thenStatusIsNotFound() throws Exception {
        when(requestService.create(anyLong(), any(ItemRequestDto.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(requestDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllPageable_whenArgsIsValid_thenStatusIsOkAndReturnListOfRequestDto() throws Exception {
        when(requestService.getAll(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(requestDto));

        String result = mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(List.of(requestDto)));
    }

    @Test
    void getAllPageable_whenParamFromIsNotValid_thenStatusIsBadRequest() throws Exception {
        mvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).getAll(anyLong(), any(Pageable.class));
    }

    @Test
    void getAllPageable_whenParamSizeIsNotValid_thenStatusIsBadRequest() throws Exception {
        mvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).getAll(anyLong(), any(Pageable.class));
    }

    @Test
    void getAll_whenArgsIsValid_thenStatusIsOkAndReturnListOfRequestDto() throws Exception {
        when(requestService.getAll(anyLong()))
                .thenReturn(List.of(requestDto));

        String result = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(List.of(requestDto)));
    }

    @Test
    void getAll_whenUserNotFound_thenStatusIsNotFound() throws Exception {
        when(requestService.getAll(anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void get_whenArgsISValid_thenStatusIsOkAndReturnRequestDto() throws Exception {
        when(requestService.get(anyLong(), anyLong()))
                .thenReturn(requestDto);

        String result = mvc.perform(get("/requests/{requestId}", requestDto.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(requestDto));
    }

    @Test
    void get_whenUserOrItemNotFound_thenStatusIsNotFound() throws Exception {
        when(requestService.get(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/requests/{requestId}", requestDto.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }
}
