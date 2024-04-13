package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto create(BookingCreateDto bookingCreateDto, long userId);

    BookingDto update(Long bookingId, long userId, Boolean approved);

    BookingDto getBooking(Long bookingId, long userId);

    Collection<BookingDto> getAllByOwnerQuery(long userId, BookingState state);

    Collection<BookingDto> getAllByUserQuery(long userId, BookingState state);
}
