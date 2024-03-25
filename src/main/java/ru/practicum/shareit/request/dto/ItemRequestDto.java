package ru.practicum.shareit.request.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequestDto {
    private String description;
    private Long requestorId;
    private LocalDateTime created;
}
