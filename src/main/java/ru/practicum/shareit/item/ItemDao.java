package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.Optional;

public interface ItemDao {
    Item create(Item item);

    Optional<Item> findById(Long itemId);

    Collection<Item> findAllByText(String text);

    Collection<Item> getItemsByOwner(long userId);

    Item update(Item item);
}
