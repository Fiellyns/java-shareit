package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Spy
    private ItemMapper itemMapper = new ItemMapper();
    @Spy
    private CommentMapper commentMapper = new CommentMapper();
    @Spy
    private BookingMapper bookingMapper = new BookingMapper();

    @InjectMocks
    private ItemServiceImpl itemService;

    private User owner;
    private Item item;
    private ItemDto itemDto;
    private ItemDto itemCreateDto;
    private ItemDto itemUpdateDto;
    private List<Item> items;

    @BeforeEach
    public void setUp() {
        owner = User.builder()
                .id(1L)
                .name("name")
                .email("name@email.com")
                .build();
        item = Item.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(TRUE)
                .owner(owner)
                .request(null)
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("description")
                .available(TRUE)
                .requestId(null)
                .comments(Collections.emptyList())
                .build();
        itemCreateDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(TRUE)
                .requestId(null)
                .comments(Collections.emptyList())
                .build();
        itemUpdateDto = ItemDto.builder()
                .id(1L)
                .name("newName")
                .comments(Collections.emptyList())
                .build();
        items = new ArrayList<>();
    }

    @AfterEach
    public void clean() {
        owner = null;
        item = null;
        items = null;
    }


    @Test
    void findAllByOwnerId_whenOwnerFound_thenReturnItems() {
        int from = 5;
        int size = 10;
        items.add(item);
        List<ItemDto> itemDtoList = List.of(itemDto);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(items);
        List<ItemDto> actualItemDtoList = itemService.getByOwner(
                owner.getId(),
                PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id")));

        assertThat(actualItemDtoList).isNotNull();
        assertThat(actualItemDtoList).isEqualTo(itemDtoList);

        verify(itemRepository).findAllByOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void findAllByOwnerId_whenOwnerNotFound_thenNotFoundExceptionThrown() {
        long userId = 100L;
        int from = 5;
        int size = 10;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.getByOwner(
                        userId,
                        PageRequest.of(from / size, size, Sort.by(Sort.Direction.ASC, "id"))));

        assertThat(e.getMessage()).isEqualTo(String.format("Пользователь с id: %d не найден", userId));

        verify(itemRepository, never()).findAllByOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void getByIdAndUserId_whenItemFound_thenReturnItemDto() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        ItemDto actualItemDto = itemService.getByIdAndUserId(item.getId(), owner.getId());

        assertThat(actualItemDto).isNotNull();
        assertThat(actualItemDto).isEqualTo(itemDto);

        verify(itemRepository).findById(anyLong());
    }

    @Test
    void getByIdAndUserId_whenItemNotFound_thenNotFoundExceptionThrown() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.getByIdAndUserId(item.getId(), owner.getId()));

        assertThat(e.getMessage())
                .isEqualTo(String.format("Предмет с id: %d не найден", item.getId()));

        verify(itemRepository).findById(anyLong());
    }


    @Test
    void getByIdAndUserId_whenItemFound_thenReturnItemDtoWithLastBookingAndComments() {
        User user1 = User.builder()
                .id(2L)
                .name("name")
                .email("user2@email.com")
                .build();

        Booking lastBooking = Booking.builder()
                .id(1L)
                .startTime(LocalDateTime.now().minusMinutes(1))
                .endTime(LocalDateTime.now().plusMinutes(10))
                .booker(user1)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(new User())
                .created(LocalDateTime.now().minusHours(2))
                .build();

        itemDto.setLastBooking(BookingMapper.toBookingInfoDto(lastBooking));
        itemDto.setComments(List.of(commentMapper.toDto(comment)));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItemIdAndStatusEqualsAndStartTimeIsBefore(anyLong(), any(Status.class),
                any(LocalDateTime.class), any(Sort.class)))
                .thenReturn(Optional.of(lastBooking));
        when(commentRepository.findAllByItemId(anyLong(), any(Sort.class)))
                .thenReturn(List.of(comment));

        ItemDto actualItemDto = itemService.getByIdAndUserId(item.getId(), owner.getId());

        assertThat(actualItemDto).isNotNull();
        assertThat(actualItemDto).isEqualTo(itemDto);

        verify(itemRepository).findById(anyLong());
    }

    @Test
    void create_whenUserFound_thenReturnSavedItem() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto savedItemDto = itemService.create(itemCreateDto, owner.getId());

        assertThat(savedItemDto).isEqualTo(itemDto);

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void create_whenUserNotFound_thenNotFoundExceptionThrown() {
        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.create(itemCreateDto, owner.getId()));

        assertThat(e.getMessage()).isEqualTo(String.format("Пользователь с id: %d не найден", owner.getId()));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_whenUserIsOwnerAndItemFound_thenReturnUpdatedItemDto() {
        itemDto.setName(itemUpdateDto.getName());
        itemDto.setName(itemUpdateDto.getName());
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto updatedItemDto =
                itemService.update(owner.getId(), itemUpdateDto);

        assertThat(updatedItemDto).isEqualTo(itemDto);

        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void update_whenUserNotFound_thenNotFoundExceptionThrown() {
        long userId = 10L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.update(userId, itemUpdateDto));

        assertThat(e.getMessage()).isEqualTo(String.format("Пользователь с id: %d не найден", userId));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_whenUserIsNotOwner_thenNotAccessExceptionThrown() {
        User notOwner = User.builder()
                .id(3L)
                .name("name3")
                .email("name3@email.com")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(notOwner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.update(notOwner.getId(), itemUpdateDto));

        assertThat(e.getMessage()).isEqualTo(String.format("Предмет с id: %d не найден", item.getId()));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void update_whenItemNotFound_thenNotFoundExceptionThrown() {
        Long itemId = 10L;
        itemUpdateDto.setId(itemId);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.update(owner.getId(), itemUpdateDto));

        assertThat(e.getMessage()).isEqualTo(String.format("Предмет с id: %d не найден", itemId));

        verify(itemRepository, never()).save(any(Item.class));
    }


    @Test
    void findAllByText_whenTextIsEmpty_thenReturnEmptyList() {
        String text = "";
        int from = 5;
        int size = 10;
        Collection<ItemDto> itemDtoList = itemService.findAllByText(
                text,
                PageRequest.of(from / size, size));

        assertThat(itemDtoList).isNotNull();
        assertThat(itemDtoList.size()).isEqualTo(0);
    }

    @Test
    void findAllByText_whenRequestIsValid_thenReturnItemsList() {
        String text = "name";
        int from = 5;
        int size = 10;
        items.add(item);
        itemDto.setComments(Collections.emptyList());
        when(itemRepository.search(
                anyString(), any(Pageable.class)))
                .thenReturn(items);
        when(commentRepository.findAllByItemId(anyLong(), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        Collection<ItemDto> itemDtoList = itemService.findAllByText(
                text,
                PageRequest.of(from / size, size));

        assertThat(itemDtoList).isNotNull();
        assertThat(itemDtoList).isEqualTo(List.of(itemDto));

        verify(itemRepository)
                .search(anyString(), any(Pageable.class));
    }

    @Test
    void createComment_whenCommentIsValid_thenReturnCommentDto() {
        User author = User.builder()
                .id(5L)
                .name("author")
                .email("author@email.com")
                .build();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("author")
                .build();
        Comment comment = Comment.builder()
                .id(1L)
                .text("text")
                .item(item)
                .author(author)
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository
                .findAllByBookerIdAndItemIdAndStatusIsAndEndTimeBefore(
                        anyLong(),
                        anyLong(),
                        any(Status.class),
                        any(LocalDateTime.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .startTime(LocalDateTime.now().minusHours(2))
                        .endTime(LocalDateTime.now().minusHours(1))
                        .item(item)
                        .booker(author)
                        .status(Status.APPROVED)
                        .build()));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentDto savedCommentDto = itemService.create(
                CommentDto.builder()
                        .text("text")
                        .build(),
                author.getId(),
                item.getId());

        assertThat(savedCommentDto).isEqualTo(commentDto);

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_whenAuthorNotFound_thenNotFoundExceptionThrown() {
        long authorId = 1L;
        long itemId = 1L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.create(
                        CommentDto.builder()
                                .text("text")
                                .build(),
                        authorId,
                        itemId));

        assertThat(e.getMessage()).isEqualTo(String.format("Пользователь с id: %d не найден", authorId));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenItemNotFound_thenNotFoundExceptionThrown() {
        User author = User.builder()
                .id(5L)
                .name("author")
                .email("author@email.com")
                .build();
        Long itemId = 10L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException e = assertThrows(NotFoundException.class,
                () -> itemService.create(
                        CommentDto.builder()
                                .text("text")
                                .build(),
                        author.getId(),
                        itemId));

        assertThat(e.getMessage()).isEqualTo(String.format("Предмет с id: %d не найден", itemId));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createComment_whenBookingNotFound_thenIllegalArgumentExceptionThrown() {
        User author = User.builder()
                .id(5L)
                .name("author")
                .email("author@email.com")
                .build();
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(author));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findAllByBookerIdAndItemIdAndStatusIsAndEndTimeBefore(
                anyLong(),
                anyLong(),
                any(Status.class),
                any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> itemService.create(
                        CommentDto.builder()
                                .text("text")
                                .build(),
                        author.getId(),
                        item.getId()));

        assertThat(e.getMessage())
                .isEqualTo(String.format("Пользователь с id: %d не бронировал товар с id: %d", author.getId(), item.getId()));

        verify(commentRepository, never()).save(any(Comment.class));
    }
}
