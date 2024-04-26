package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto create(BookingCreateDto bookingCreateDto, long userId);

    BookingDto update(Long bookingId, long userId, Boolean approved);

    BookingDto getById(Long bookingId, long userId);

    Collection<BookingDto> getAllByOwnerQuery(long userId, BookingState state, Pageable pageable);

    Collection<BookingDto> getAllByUserQuery(long userId, BookingState state, Pageable pageable);
}
