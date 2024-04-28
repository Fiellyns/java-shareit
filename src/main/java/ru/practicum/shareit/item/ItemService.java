package ru.practicum.shareit.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    CommentDto create(CommentDto commentDto, long userId, Long itemId);

    List<CommentDto> getAllComments(Long itemId);

    ItemDto getByIdAndUserId(long userId, Long itemId);

    List<ItemDto> getByOwner(long userId, Pageable pageable);

    Collection<ItemDto> findAllByText(String text, Pageable pageable);

    ItemDto update(long userId, ItemDto item);
}
