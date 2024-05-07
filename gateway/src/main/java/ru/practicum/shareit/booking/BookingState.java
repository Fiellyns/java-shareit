package ru.practicum.shareit.booking;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum BookingState {
    ALL,

    CURRENT,

    FUTURE,

    PAST,

    REJECTED,

    WAITING;

    private static final Map<String, BookingState> lookup = new HashMap<>();

    static {
        for (BookingState state : values()) {
            lookup.put(state.name(), state);
        }
    }

    public static Optional<BookingState> from(String state) {
        return Optional.ofNullable(lookup.get(state));
    }
}

