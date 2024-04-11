package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookingOutputDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @Valid @RequestBody BookingInputDto bookingInputDto) {
        log.info("Поступил POST-запрос в /bookings");
        BookingOutputDto createdBooking = bookingService.create(bookingInputDto, userId);
        log.info("POST-запрос /bookings был обработан: {}", createdBooking);
        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                                   @PathVariable Long bookingId,
                                   @RequestParam Boolean approved
    ) {
        log.info("Поступил PATCH-запрос в /bookings/{}", bookingId);
        BookingOutputDto updatedBooking = bookingService.update(bookingId, userId, approved);
        log.info("PATCH-запрос /bookings/{} был обработан: {}", bookingId, updatedBooking);
        return updatedBooking;
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                    @PathVariable Long bookingId
    ) {
        log.info("Поступил GET-запрос в /bookings/{}", bookingId);
        BookingOutputDto booking = bookingService.getBooking(bookingId, userId);
        log.info("GET-запрос /bookings/{} был обработан: {}", bookingId, booking);
        return booking;
    }

    @GetMapping
    public Collection<BookingOutputDto> getAllByUserQuery(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("Поступил GET-запрос в /bookings");
        Collection<BookingOutputDto> searchedBookings = bookingService.getAllByUserQuery(userId, state);
        log.info("GET-запрос /bookings был обработан: {}", searchedBookings);
        return searchedBookings;
    }

    @GetMapping("/owner")
    public Collection<BookingOutputDto> getAllByOwnerQuery(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("Поступил GET-запрос в /bookings/owner");
        Collection<BookingOutputDto> searchedBookings = bookingService.getAllByOwnerQuery(userId, state);
        log.info("GET-запрос /bookings/owner был обработан: {}", searchedBookings);
        return searchedBookings;
    }
}