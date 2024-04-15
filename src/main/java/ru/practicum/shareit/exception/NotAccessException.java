package ru.practicum.shareit.exception;

public class NotAccessException extends RuntimeException {
    public NotAccessException(String message) {
        super(message);
    }
}
