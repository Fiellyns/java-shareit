package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.exception.NotAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDao;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingDao bookingDao;
    private final UserDao userDao;
    private final ItemDao itemDao;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingServiceImpl(BookingDao bookingDao, UserDao userDao, ItemDao itemDao, BookingMapper bookingMapper) {
        this.bookingDao = bookingDao;
        this.userDao = userDao;
        this.itemDao = itemDao;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingOutputDto create(BookingInputDto bookingDto, long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
        Item item = itemDao.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + bookingDto.getItemId() + " не найден"));

        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Предмет с id: " + bookingDto.getItemId() + " недоступен для аренды");
        }
        if (item.getOwner().getId().equals(userId)) {
            throw new NotAccessException("Владелец не может арендовать свой предмет");
        }
        Booking booking = bookingMapper.toModel(bookingDto, Status.WAITING, item, user);
        return bookingMapper.toDto(bookingDao.save(booking));
    }

    @Override
    public BookingOutputDto update(Long bookingId, long userId, Boolean approved) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Аренда с id: " + bookingId + " не найдена"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotAccessException("Только владелец может подтверждать аренду");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new IllegalArgumentException("Статус уже подтверждён");
        }
        booking.setStatus(approved ? Status.APPROVED : Status.REJECTED);
        return bookingMapper.toDto(bookingDao.save(booking));
    }

    @Override
    public BookingOutputDto getBooking(Long bookingId, long userId) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Аренда с id: " + bookingId + " не найдена"));
        if (!(booking.getItem().getOwner().getId().equals(userId) ||
                booking.getBooker().getId().equals(userId))) {
            throw new NotAccessException("Только владелец или арендатор может получить данные");
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    public Collection<BookingOutputDto> getAllByUserQuery(long userId, String state) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        Collection<Booking> requestedBooking;
        Sort sort = Sort.by("startTime").descending();
        LocalDateTime now = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                requestedBooking = bookingDao.findAllByBookerId(userId, sort);
                break;
            case CURRENT:
                requestedBooking = bookingDao
                        .findAllByBookerIdAndStartTimeBeforeAndEndTimeAfter(userId, now, now, sort);
                break;
            case FUTURE:
                requestedBooking = bookingDao
                        .findAllByBookerIdAndStartTimeAfter(userId, now, sort);
                break;
            case PAST:
                requestedBooking = bookingDao
                        .findAllByBookerIdAndEndTimeBefore(userId, now, sort);
                break;
            case REJECTED:
                requestedBooking = bookingDao
                        .findAllByBookerIdAndStatus(userId, Status.REJECTED, sort);
                break;
            case WAITING:
                requestedBooking = bookingDao
                        .findAllByBookerIdAndStatus(userId, Status.WAITING, sort);
                break;
            default:
                requestedBooking = Collections.emptyList();
        }
        return requestedBooking.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<BookingOutputDto> getAllByOwnerQuery(long userId, String state) {
        if (!userDao.existsById(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        Collection<Booking> requestedBooking;
        Sort sort = Sort.by("startTime").descending();
        LocalDateTime now = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                requestedBooking = bookingDao.findAllByItemOwnerId(userId, sort);
                break;
            case CURRENT:
                requestedBooking = bookingDao
                        .findAllByItemOwnerIdAndStartTimeBeforeAndEndTimeAfter(userId, now, now, sort);
                break;
            case FUTURE:
                requestedBooking = bookingDao
                        .findFAllByItemOwnerIdAndStartTimeAfter(userId, now, sort);
                break;
            case PAST:
                requestedBooking = bookingDao
                        .findAllByItemOwnerIdAndEndTimeBefore(userId, now, sort);
                break;
            case REJECTED:
                requestedBooking = bookingDao
                        .findAllByItemOwnerIdAndStatus(userId, Status.REJECTED, sort);
                break;
            case WAITING:
                requestedBooking = bookingDao
                        .findAllByItemOwnerIdAndStatus(userId, Status.WAITING, sort);
                break;
            default:
                requestedBooking = Collections.emptyList();
        }
        return requestedBooking.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }
}
