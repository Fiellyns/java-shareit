package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto itemDto, long userId);

    ItemDto findById(Long itemId);

    Collection<ItemDto> findAllByText(String text);

    Collection<ItemDto> getItemsByOwner(long userId);

    ItemDto update(long userId, ItemDto item);

    void existsItemById(Long itemId);
}
