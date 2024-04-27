package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private RequestMapper requestMapper = new RequestMapper();

    @Spy
    private ItemMapper itemMapper = new ItemMapper();

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private ItemRequestDto requestCreateDto;
    private ItemRequestDto requestDto;
    private ItemRequest request;
    private User requestor;

    @BeforeEach
    void setUp() {
        LocalDateTime created = LocalDateTime.now().withNano(0);
        requestCreateDto = ItemRequestDto.builder()
                .description("description")
                .build();
        requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .created(created)
                .items(new ArrayList<>())
                .build();
        requestor = User.builder()
                .id(1L)
                .name("name")
                .email("name@email.com")
                .build();
        request = ItemRequest.builder()
                .id(1L)
                .description("description")
                .requestor(requestor)
                .created(created)
                .build();
    }

    @AfterEach
    void clean() {
        requestCreateDto = null;
        requestDto = null;
        request = null;
        requestor = null;
    }


    @Test
    void create_whenRequestIsValid_thenReturnItemRequestDto() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(request);

        ItemRequestDto savedRequest = requestService.create(requestor.getId(), requestCreateDto);

        assertThat(savedRequest).isEqualTo(requestDto);
    }

    @Test
    void create_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.create(userId, requestCreateDto));

        assertThat(e.getMessage()).isEqualTo(String.format("Пользователь с id: %d не найден", userId));

        verify(requestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void getAll_whenItemsFound_thenReturnRequestWithItems() {
        requestDto.setItems(List.of(ItemDto.builder()
                .id(1L)
                .comments(new ArrayList<>())
                .requestId(1L)
                .build()));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        when(requestRepository.findAllByRequestorId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(List.of(Item.builder()
                        .id(1L)
                        .request(request)
                        .build()));

        List<ItemRequestDto> itemRequestDtos = requestService.getAll(requestor.getId());

        assertThat(itemRequestDtos).isEqualTo(List.of(requestDto));
    }

    @Test
    void findAllByRequestorId_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.getAll(userId));

        assertThat(e.getMessage()).isEqualTo(String.format("Пользователь с id: %d не найден", userId));

        verify(requestRepository, never()).findAllByRequestorId(anyLong(), any(Sort.class));
    }

    @Test
    void getAll_whenRequestIsValid_thenReturnRequestDtoList() {
        int from = 5;
        int size = 10;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        requestDto.setItems(List.of(ItemDto.builder()
                .id(1L)
                .comments(new ArrayList<>())
                .requestId(1L)
                .build()));
        when(requestRepository.findAllByRequestorIdNot(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(request));
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(List.of(Item.builder()
                        .id(1L)
                        .request(request)
                        .build()));

        List<ItemRequestDto> itemRequestDtoList = requestService.getAll(
                requestor.getId(),
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "created")));

        assertThat(itemRequestDtoList).isEqualTo(List.of(requestDto));
    }

    @Test
    void get_whenUserAndRequestFound_thenReturnItemRequestDto() {
        requestDto.setItems(Collections.emptyList());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));

        ItemRequestDto itemRequestDto = requestService.get(requestor.getId(), request.getId());

        assertThat(itemRequestDto).isEqualTo(requestDto);
    }

    @Test
    void findById_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10L;
        Long requestId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.get(userId, requestId));

        assertThat(e.getMessage()).isEqualTo(String.format("Пользователь с id: %d не найден", userId));

        verify(requestRepository, never()).findById(requestId);
    }

    @Test
    void findById_whenRequestNotFound_thenNotFoundExceptionThrown() {
        long userId = 1L;
        Long requestId = 10L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(requestor));
        when(requestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> requestService.get(userId, requestId));

        assertThat(e.getMessage()).isEqualTo(String.format("Запрос на вещь с идентификатором id: %d не найден", requestId));
    }
}
