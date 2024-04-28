package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        ItemRequest request = itemDto.getRequestId() != null ?
                itemRequestRepository.findById(itemDto.getRequestId()).orElse(null) : null;
        return itemMapper.toDto(itemRepository.save(itemMapper.toModel(itemDto, user, request)));
    }

    @Override
    public CommentDto create(CommentDto commentDto, long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + itemId + " не найден"));
        Collection<Booking> userBookings = bookingRepository
                .findAllByBookerIdAndItemIdAndStatusIsAndEndTimeBefore(
                        userId, itemId, Status.APPROVED, LocalDateTime.now());
        if (userBookings.isEmpty()) {
            throw new IllegalArgumentException("Пользователь с id: " + userId + " не бронировал товар с id: " + itemId);
        }
        commentDto.setCreated(LocalDateTime.now());
        Comment comment = commentMapper.toModel(commentDto, user, item);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getAllComments(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId, Sort.by("created"));
        return comments
                .stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getByIdAndUserId(long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + itemId + " не найден"));
        if (item.getOwner().getId().equals(userId)) {
            return itemMapper.toDto(item, getLastBooking(item.getId()), getNextBooking(item.getId()), getAllComments(itemId));
        }
        return itemMapper.toDto(item, getAllComments(itemId));
    }

    @Override
    public List<ItemDto> getByOwner(long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        List<Item> items = itemRepository.findAllByOwnerId(userId, pageable);
        List<Long> itemsIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByItemIdIn(itemsIds);
        if (bookings.isEmpty()) {
            return items.stream()
                    .map(itemMapper::toDto)
                    .collect(Collectors.toList());
        }
        List<Comment> comments = commentRepository.findAllByItemIdIn(itemsIds);
        Map<Long, List<Booking>> itemBookings = bookings.stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(), Collectors.toList()));
        Map<Long, List<Comment>> itemComments = comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(), Collectors.toList()));
        return items.stream()
                .map(item -> {
                    BookingInfoDto lastBooking = getLastBooking(itemBookings.getOrDefault(item.getId(), Collections.emptyList()));
                    BookingInfoDto nextBooking = getNextBooking(itemBookings.getOrDefault(item.getId(), Collections.emptyList()));
                    List<CommentDto> commentsItem = itemComments.getOrDefault(item.getId(), Collections.emptyList())
                            .stream()
                            .map(commentMapper::toDto)
                            .collect(Collectors.toList());
                    return itemMapper.toDto(item, lastBooking,
                            nextBooking, commentsItem);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> findAllByText(String text, Pageable pageable) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text, pageable)
                .stream()
                .map(item -> itemMapper.toDto(item, getLastBooking(item.getId()), getNextBooking(item.getId()),
                        getAllComments(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto update(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        Item item = itemRepository.findById(itemDto.getId())
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + itemDto.getId() + " не найден"));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Предмет с id: " + item.getId() + " не найден");
        }

        Item itemFromDto = itemMapper.toModel(itemDto, user);

        item.setName(Objects.requireNonNullElse(itemFromDto.getName(), item.getName()));
        item.setDescription(Objects.requireNonNullElse(itemFromDto.getDescription(), item.getDescription()));
        item.setAvailable(Objects.requireNonNullElse(itemFromDto.getAvailable(), item.getAvailable()));

        return itemMapper.toDto(itemRepository.save(item), getLastBooking(item.getId()), getNextBooking(item.getId()),
                getAllComments(item.getId()));
    }

    private BookingInfoDto getLastBooking(long itemId) {
        return bookingRepository.findFirstByItemIdAndStatusEqualsAndStartTimeIsBefore(itemId,
                        Status.APPROVED, LocalDateTime.now(), Sort.by("endTime").descending())
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private BookingInfoDto getNextBooking(long itemId) {
        return bookingRepository.findFirstByItemIdAndStatusEqualsAndStartTimeIsAfter(itemId,
                        Status.APPROVED, LocalDateTime.now(), Sort.by("startTime").ascending())
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private BookingInfoDto getLastBooking(List<Booking> bookings) {
        Optional<Booking> lastBooking = bookings
                .stream()
                .filter(booking -> booking.getStatus().equals(Status.APPROVED)
                        && booking.getStartTime().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEndTime));
        return lastBooking.map(BookingMapper::toBookingInfoDto).orElse(null);
    }

    private BookingInfoDto getNextBooking(List<Booking> bookings) {
        Optional<Booking> nextBooking = bookings
                .stream()
                .filter(booking -> booking.getStatus().equals(Status.APPROVED)
                        && booking.getStartTime().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getStartTime));
        return nextBooking.map(BookingMapper::toBookingInfoDto).orElse(null);
    }
}
