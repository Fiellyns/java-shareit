package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
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
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                             @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        log.info("Поступил POST-запрос в /bookings");
        BookingDto createdBooking = bookingService.create(bookingCreateDto, userId);
        log.info("POST-запрос /bookings был обработан: {}", createdBooking);
        return createdBooking;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") long userId,
                             @PathVariable Long bookingId,
                             @RequestParam Boolean approved) {
        log.info("Поступил PATCH-запрос в /bookings/{}", bookingId);
        BookingDto updatedBooking = bookingService.update(bookingId, userId, approved);
        log.info("PATCH-запрос /bookings/{} был обработан: {}", bookingId, updatedBooking);
        return updatedBooking;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable Long bookingId) {
        log.info("Поступил GET-запрос в /bookings/{}", bookingId);
        BookingDto booking = bookingService.getById(bookingId, userId);
        log.info("GET-запрос /bookings/{} был обработан: {}", bookingId, booking);
        return booking;
    }

    @GetMapping
    public Collection<BookingDto> getAllByUserQuery(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @Min(0) @RequestParam(value = "from", defaultValue = "0") int offset,
            @Min(1) @RequestParam(value = "size", defaultValue = "10") int limit) {
        log.info("Поступил GET-запрос в /bookings");
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "startTime"));
        Collection<BookingDto> searchedBookings = bookingService.getAllByUserQuery(userId, bookingState, pageable);
        log.info("GET-запрос /bookings был обработан: {}", searchedBookings);
        return searchedBookings;
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getAllByOwnerQuery(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @Min(0) @RequestParam(value = "from", defaultValue = "0") int offset,
            @Min(1) @RequestParam(value = "size", defaultValue = "10") int limit) {
        log.info("Поступил GET-запрос в /bookings/owner");
        BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "startTime"));
        Collection<BookingDto> searchedBookings = bookingService.getAllByOwnerQuery(userId, bookingState, pageable);
        log.info("GET-запрос /bookings/owner был обработан: {}", searchedBookings);
        return searchedBookings;
    }
}