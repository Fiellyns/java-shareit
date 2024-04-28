package ru.practicum.shareit.item;

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
import ru.practicum.shareit.exception.NotAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemServiceImpl itemService;

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
    void getByOwner_whenUsersExist_thenStatusOkAndReturnList() throws Exception {
        when(itemService.getByOwner(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(itemDto));

        String result = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(List.of(itemDto)));
    }

    @Test
    void getByOwner_whenRequestParamFromFalse_thenBadRequest() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getByOwner(anyLong(), any(Pageable.class));
    }

    @Test
    void getByOwner_whenRequestSizeFromFalse_thenBadRequest() throws Exception {
        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).getByOwner(anyLong(), any(Pageable.class));
    }

    @Test
    void getByOwner_whenUserNotFound_thenStatusIsNotFound() throws Exception {
        when(itemService.getByOwner(anyLong(), any(Pageable.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 10L)
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByIdAndUserId_whenArgsIsValid_thenStatusIsOkAndReturnItemDto() throws Exception {
        when(itemService.getByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(itemDto);

        String result = mvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(itemDto));
    }

    @Test
    void getByIdAndUserId_whenItemNotFound_thenStatusIsNotFound() throws Exception {
        when(itemService.getByIdAndUserId(anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(get("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_whenItemIsValid_thenStatusIsCreatedAndReturnSavedItem() throws Exception {
        when(itemService.create(any(ItemDto.class), anyLong()))
                .thenReturn(itemDto);

        String result = mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemCreateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(itemDto));
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

        verify(itemService, never()).create(any(ItemDto.class), anyLong());
    }

    @Test
    void update_whenItemIsValid_thenStatusIsOkAndReturnUpdatedItem() throws Exception {
        when(itemService.update(anyLong(), any(ItemDto.class)))
                .thenReturn(updatedItemDto);

        String result = mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemUpdateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(updatedItemDto));
    }

    @Test
    void update_whenFieldsEmpty_thenStatusIsOkAndReturnUpdatedItem() throws Exception {
        itemUpdateDto.setName(null);
        itemUpdateDto.setDescription(null);
        when(itemService.update(anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemUpdateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        verify(itemService).update(anyLong(), any(ItemDto.class));
    }

    @Test
    void update_whenUserOrItemNotFound_thenStatusIsNotFound() throws Exception {
        when(itemService.update(anyLong(), any(ItemDto.class)))
                .thenThrow(NotFoundException.class);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemUpdateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_whenUserIsNotOwner_thenStatusIsNotFound() throws Exception {
        when(itemService.update(anyLong(), any(ItemDto.class)))
                .thenThrow(NotAccessException.class);

        mvc.perform(patch("/items/{itemId}", itemDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(itemUpdateDto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllByText_whenArgsIsValid_thenStatusIsOkAndReturnListOfItemDto() throws Exception {
        items.add(itemDto);
        when(itemService.findAllByText(anyString(), any(Pageable.class)))
                .thenReturn(items);

        String result = mvc.perform(get("/items/search")
                        .param("text", "text")
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(items));
    }

    @Test
    void createComment_whenCommentIsValid_thenStatusIsOkAndReturnCommentDto() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("name")
                .created(LocalDateTime.now())
                .build();
        when(itemService.create(any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(commentDto);

        String result = mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(
                                CommentDto.builder()
                                        .text("text")
                                        .build())))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(commentDto));
    }

    @Test
    void createComment_whenUserOrItemNotFound_thenStatusIsNotFound() throws Exception {
        when(itemService.create(any(CommentDto.class), anyLong(), anyLong()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(
                                CommentDto.builder()
                                        .text("text")
                                        .build())))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void createComment_whenUserHasNotBookings_thenStatusIsBadRequest() throws Exception {
        when(itemService.create(any(CommentDto.class), anyLong(), anyLong()))
                .thenThrow(IllegalArgumentException.class);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1)
                        .content(String.valueOf(mapper.writeValueAsString(
                                CommentDto.builder()
                                        .text("text")
                                        .build())))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
