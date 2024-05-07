package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    private ItemDto itemDto;
    private ItemDto itemCreateDto;
    private ItemDto itemUpdateDto;
    private ItemDto updatedItemDto;
    private List<ItemDto> items;

    @BeforeEach
    void setUp() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(TRUE)
                .lastBooking(null)
                .nextBooking(null)
                .requestId(null)
                .comments(Collections.emptyList())
                .build();
        itemCreateDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(TRUE)
                .build();
        itemUpdateDto = ItemDto.builder()
                .id(1L)
                .name("newName")
                .description("newDescription")
                .available(TRUE)
                .build();
        updatedItemDto = ItemDto.builder()
                .id(1L)
                .name("newName")
                .description("newDescription")
                .available(TRUE)
                .build();
        items = new ArrayList<>();
    }

    @AfterEach
    void clean() {
        itemDto = null;
        itemCreateDto = null;
        itemUpdateDto = null;
        updatedItemDto = null;
        items = null;
    }

    @Test
    void getByOwner_whenRequestParamFromFalse_thenBadRequest() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllByUser(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getByOwner_whenRequestSizeFromFalse_thenBadRequest() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).getAllByUser(anyLong(), anyInt(), anyInt());
    }

    @Test
    void create_whenItemIsNotValid_thenStatusIsBadRequest() throws Exception {
        itemCreateDto.setName("");

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemClient, never()).create(anyLong(), any(ItemDto.class));
    }
}