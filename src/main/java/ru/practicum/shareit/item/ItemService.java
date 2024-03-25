package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto findById(Long itemId);

    Collection<ItemDto> findAllByText(String text);

    Collection<ItemDto> getItemsByOwner(Long userId);

    ItemDto update(Long userId, Long itemId, ItemDto item);
}
