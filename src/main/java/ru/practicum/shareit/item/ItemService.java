package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    CommentDto create(CommentDto commentDto, long userId, Long itemId);

    List<CommentDto> getAllCommentsByItemId(Long itemId);

    ItemDto getByItemIdAndUserId(long userId, Long itemId);

    Collection<ItemDto> getItemsByOwner(long userId);

    Collection<ItemDto> findAllByText(String text);

    ItemDto update(long userId, ItemDto item);
}
