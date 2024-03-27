package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequest {
    private Long id;
    private String description;
    private Long requestorId;
    private LocalDateTime created;
}
