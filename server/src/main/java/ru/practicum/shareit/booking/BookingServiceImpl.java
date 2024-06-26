package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto create(BookingCreateDto bookingDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + bookingDto.getItemId() + " не найден"));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Предмет с id: " + bookingDto.getItemId() + " недоступен для аренды");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotAccessException("Владелец не может арендовать свой предмет");
        }
        Booking booking = bookingMapper.toModel(bookingDto, Status.WAITING, item, user);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(Long bookingId, long userId, Boolean approved) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Аренда с id: " + bookingId + " не найдена"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotAccessException("Только владелец может подтверждать аренду");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new IllegalArgumentException("Статус уже подтверждён");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long bookingId, long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Аренда с id: " + bookingId + " не найдена"));
        if (!(booking.getItem().getOwner().getId().equals(userId) ||
                booking.getBooker().getId().equals(userId))) {
            throw new NotAccessException("Только владелец или арендатор может получить данные");
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    public Collection<BookingDto> getAllByUserQuery(long userId, BookingState bookingState, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        Collection<Booking> requestedBooking;
        LocalDateTime now = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                requestedBooking = bookingRepository.findAllByBookerId(userId, pageable);
                break;
            case CURRENT:
                requestedBooking = bookingRepository
                        .findAllByBookerIdAndStartTimeBeforeAndEndTimeAfter(userId, now, now, pageable);
                break;
            case FUTURE:
                requestedBooking = bookingRepository
                        .findAllByBookerIdAndStartTimeAfter(userId, now, pageable);
                break;
            case PAST:
                requestedBooking = bookingRepository
                        .findAllByBookerIdAndEndTimeBefore(userId, now, pageable);
                break;
            case REJECTED:
                requestedBooking = bookingRepository
                        .findAllByBookerIdAndStatus(userId, Status.REJECTED, pageable);
                break;
            case WAITING:
                requestedBooking = bookingRepository
                        .findAllByBookerIdAndStatus(userId, Status.WAITING, pageable);
                break;
            default:
                requestedBooking = Collections.emptyList();
        }
        return requestedBooking.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingDto> getAllByOwnerQuery(long userId, BookingState bookingState, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        Collection<Booking> requestedBooking;
        LocalDateTime now = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                requestedBooking = bookingRepository.findAllByItemOwnerId(userId, pageable);
                break;
            case CURRENT:
                requestedBooking = bookingRepository
                        .findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfter(userId, now, now, pageable);
                break;
            case FUTURE:
                requestedBooking = bookingRepository
                        .findAllByItemOwnerIdAndStartTimeAfter(userId, now, pageable);
                break;
            case PAST:
                requestedBooking = bookingRepository
                        .findAllByItemOwnerIdAndEndTimeBefore(userId, now, pageable);
                break;
            case REJECTED:
                requestedBooking = bookingRepository
                        .findAllByItemOwnerIdAndStatus(userId, Status.REJECTED, pageable);
                break;
            case WAITING:
                requestedBooking = bookingRepository
                        .findAllByItemOwnerIdAndStatus(userId, Status.WAITING, pageable);
                break;
            default:
                requestedBooking = Collections.emptyList();
        }
        return requestedBooking.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
