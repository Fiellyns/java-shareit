package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemDao {
    Item create(Item item);

    Item findById(Long itemId);

    Collection<Item> findAllByText(String text);

    Collection<Item> getItemsByOwner(long userId);

    Item update(Item item);

    boolean existsItemById(Long itemId);
}
