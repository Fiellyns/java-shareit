package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(long requestorId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAll(long requestorId);

    ItemRequestDto get(long requestorId, long requestId);

    List<ItemRequestDto> getAll(long requestorId, Pageable pageable);
}
