package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.util.Collection;

public interface BookingService {
    BookingOutputDto create(BookingInputDto bookingInputDto, long userId);

    BookingOutputDto update(Long bookingId, long userId, Boolean approved);

    BookingOutputDto getBooking(Long bookingId, long userId);

    Collection<BookingOutputDto> getAllByOwnerQuery(long userId, String state);

    Collection<BookingOutputDto> getAllByUserQuery(long userId, String state);
}
