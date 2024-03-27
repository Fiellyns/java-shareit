package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
    private Long bookerId;
    private Status status;
}
