package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;

@Component
public class BookingMapper {

    public static BookingInfoDto toBookingInfoDto(Booking booking) {
        return new BookingInfoDto(booking.getId(), booking.getBooker().getId(), booking.getStartTime(), booking.getEndTime());
    }

    public BookingDto toDto(Booking booking) {
        UserDto userDto = new UserDto(booking.getBooker().getId(),
                booking.getBooker().getName(), booking.getBooker().getEmail());
        ItemDto itemDto = new ItemDto(booking.getItem().getId(), booking.getItem().getName(),
                booking.getItem().getDescription(), booking.getItem().getAvailable(),
                null, null,
                booking.getItem().getRequest() != null ? booking.getItem().getRequest().getId() : null,
                new ArrayList<>());
        return new BookingDto(booking.getId(), booking.getStartTime(), booking.getEndTime(), booking.getStatus(), userDto, itemDto);
    }

    public Booking toModel(BookingCreateDto bookingCreateDto, Status status,
                           Item item, User user) {
        return new Booking().toBuilder()
                .startTime(bookingCreateDto.getStartTime())
                .endTime(bookingCreateDto.getEndTime())
                .status(status)
                .booker(user)
                .item(item)
                .build();
    }
}
