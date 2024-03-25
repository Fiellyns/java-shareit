package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemDao {
    Item create(Item item);

    Item findById(Long itemId);

    Collection<Item> findAllByText(String text);

    Collection<Item> getItemsByOwner(Long userId);

    Item update(Item item, Long itemId);

    void existsItemById(Long itemId);
}
